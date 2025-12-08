package game;

import utils.Color;
import utils.Position;
import javax.swing.JOptionPane;

import piece.Piece;

public class GUI extends Game {
    private transient gui.board.BoardPanel boardPanel;
    private transient gui.chessFrame parentFrame;
    private boolean aiShouldMakeFirstMove = false;
    private boolean aiIsThinking = false; // Prevent recursive AI moves

    /**
     * Creates a new 1 or 2 player game instance
     * 
     * @param isPvP
     * @param p1Color The color the 1st player
     */
    public GUI(boolean isPvP, Color p1Color) {
        super(isPvP, p1Color);
        // Initialize move validator from board
        setValidMoveDetector(board.getCheckmateDetector());

        // Check if AI is white (goes first)
        if (!isPvP && currentPlayer instanceof player.AI) {
            aiShouldMakeFirstMove = true;
        }
    }

    // ============================================================================
    // SETTERS
    // ============================================================================

    /**
     * Sets the parent frame reference for clearing the game.
     * 
     * @param frame The parent chessFrame instance
     */
    public void setParentFrame(gui.chessFrame frame) {
        this.parentFrame = frame;
    }

    /**
     * Sets the board panel reference for frontend updates.
     * 
     * @param panel The BoardPanel instance
     */
    public void setBoardPanel(gui.board.BoardPanel panel) {
        this.boardPanel = panel;
    }

    // ============================================================================
    // GETTERS
    // ============================================================================

    /**
     * Checks if the game has ended due to King capture.
     * 
     * @return true if a King has been captured, false otherwise
     */
    public boolean isGameOver() {
        return winner != null;
    }

    /**
     * Determines the winner of the Lan game.
     * 
     * @return The winning color, or null if game is ongoing
     */
    public Color getWinner() {
        return winner;
    }

    // ============================================================================
    // GAME LOGIC
    // ============================================================================

    /**
     * Executes a single move attempt with full validation and GUI updates.
     * Entry point for moves executed through the GUI.
     * 
     * Flow:
     * 1. Validate ownership (player moving their own piece)
     * 2. Validate move legality (reachability + king safety)
     * 3. If invalid, show popup and return false
     * 4. If valid, update backend board state
     * 5. Handle captures if needed
     * 6. Update frontend (refresh BoardPanel)
     * 7. Check for checkmate
     * 8. Switch turns
     * 
     * @param fromPosition Starting position of the piece
     * @param toPosition   Target position for the move
     * @param pieceToMove  The piece being moved
     * @return true if move was valid and executed, false if invalid
     */
    public boolean executeTurn(Position fromPosition, Position toPosition, Piece pieceToMove) {
        System.out.println("\n========== EXECUTE TURN START ==========");
        System.out.println("Current turn: " + WhosTurn + " | Current player: " + currentPlayer.getColor());
        System.out.println("Moving: " + pieceToMove.getClass().getSimpleName() + " (" + pieceToMove.getColor() + ")");
        System.out.println("From: " + fromPosition + " -> To: " + toPosition);

        // Ownership validation - prevent moving opponent's pieces (including AI pieces)
        if (pieceToMove.getColor() != currentPlayer.getColor()) {
            System.out.println("FAILED at executeTurn: Ownership check - trying to move opponent's piece");
            showWrongColorError();
            return false;
        }

        // Check for captures BEFORE moving
        Piece capturedPiece = board.getPieceAt(toPosition);
        boolean isCapture = capturedPiece != null && capturedPiece.getColor() != pieceToMove.getColor();
        if (isCapture) {
            System.out.println("Capture detected: " + capturedPiece.getClass().getSimpleName() + " ("
                    + capturedPiece.getColor() + ") at " + toPosition);
        }

        // Validate the move (reachability + king safety)
        System.out.println("Calling board.attemptMove()...");
        boolean moveSuccessful = board.attemptMove(toPosition, pieceToMove);

        if (!moveSuccessful) {
            System.out.println("FAILED at executeTurn: board.attemptMove() returned false");
            System.out.println("========== EXECUTE TURN END (FAILED) ==========\\n");
            showInvalidMoveMessage();
            return false;
        }
        System.out.println("SUCCESS: board.attemptMove() passed");
        System.out.println("Piece position after attemptMove: " + pieceToMove.getPosition());

        // Check if this is a castling move
        boolean isCastling = board.isCastlingMove(pieceToMove, fromPosition, toPosition);
        if (isCastling && pieceToMove instanceof piece.King) {
            System.out.println("Castling move detected!");
            // Execute castling (moves both king and rook)
            board.executeCastling((piece.King) pieceToMove, fromPosition, toPosition);
        }

        // NOTE: attemptMove() already called piece.move() which updated the piece's
        // position
        // We need to update the position index to reflect the move
        board.updatePiecePosition(pieceToMove, fromPosition, toPosition);

        // Then handle captures (after the piece has moved)
        if (isCapture) {
            // Capture the piece at destination
            board.capturePiece(pieceToMove, toPosition, capturedPiece);
        }

        // Check for pawn promotion
        if (board.isPawnPromotion(pieceToMove, toPosition)) {
            System.out.println("Pawn promotion triggered!");
            String promotionType = promptForPromotion();
            if (promotionType != null) {
                pieceToMove = board.promotePawn((piece.Pawn) pieceToMove, toPosition, promotionType);
                // Regenerate attack map after promotion
                board.getAttackMap().updateAfterMove(pieceToMove, toPosition, toPosition);
            }
        }

        // Step 7: Look for checkmate
        player.Player opponent = getOpponentPlayer();

        if (validMoveDetector != null && validMoveDetector.isCheckmate(opponent.getColor())) {
            winner = currentPlayer.getColor();
            System.out.println("Checkmate! " + winner + " wins!");
            end(winner);
            return true;
        }

        // Check if opponent is in check (but not checkmate)
        if (validMoveDetector != null && validMoveDetector.isKingInCheck(opponent.getColor())) {
            String opponentColorName = (opponent.getColor() == Color.WHITE) ? "White" : "Black";
            if (parentFrame != null) {
                parentFrame.displayMessage(opponentColorName + " is in check!", "warning");
            }
        }

        // Step 8: Switch turns on BE (FE board flip handled in BoardPanel)
        switchTurn();
        System.out.println("Move executed successfully!");
        System.out.println("========== EXECUTE TURN END (SUCCESS) ==========\n");

        // Check if next player is AI and trigger AI move
        // Only trigger if AI is not already thinking (prevent double moves)
        if (currentPlayer instanceof player.AI && parentFrame != null && !aiIsThinking) {
            javax.swing.SwingUtilities.invokeLater(() -> {
                try {
                    Thread.sleep(150); // Brief delay so human can see their move
                    makeAIMove();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }

        return true;
    }

    /**
     * Makes the AI calculate and execute its move.
     */
    private void makeAIMove() {
        if (!(currentPlayer instanceof player.AI)) {
            return;
        }

        // Prevent double moves
        if (aiIsThinking) {
            System.out.println("AI is already thinking, skipping duplicate call");
            return;
        }

        aiIsThinking = true;
        System.out.println("AI starting to think...");

        player.AI ai = (player.AI) currentPlayer;
        player.AI.Move aiMove = ai.getBestMove(board);

        if (aiMove != null) {
            // Wait 150ms before executing move so user can see the board state
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            System.out.println("AI executing move: " + aiMove);
            boolean success = executeTurn(aiMove.from, aiMove.to, aiMove.piece);
            if (success && parentFrame != null) {
                parentFrame.refreshDisplay();
                // Flip board after AI move with slight delay
                javax.swing.Timer flipTimer = new javax.swing.Timer(150, e -> parentFrame.flipBoard());
                flipTimer.setRepeats(false);
                flipTimer.start();
            }
        }

        aiIsThinking = false;
        System.out.println("AI finished thinking");
    }

    /**
     * Triggers the AI to make the first move if it's playing as white.
     * Should be called after the GUI is fully initialized.
     */
    public void triggerAIFirstMoveIfNeeded() {
        if (aiShouldMakeFirstMove && currentPlayer instanceof player.AI) {
            javax.swing.SwingUtilities.invokeLater(() -> {
                try {
                    Thread.sleep(150); // Brief delay before AI's first move
                    makeAIMove();
                    aiShouldMakeFirstMove = false; // Reset flag
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    /**
     * Shows an error message when a player tries to move the opponent's piece.
     */
    protected void showWrongColorError() {
        if (parentFrame != null) {
            String currentColor = (currentPlayer.getColor() == Color.WHITE) ? "White" : "Black";
            parentFrame.displayMessage("You cannot move your opponent's pieces! It is " + currentColor + "'s turn.",
                    "error");
        }
    }

    /**
     * Shows a message when an invalid move is attempted.
     */
    protected void showInvalidMoveMessage() {
        if (parentFrame != null) {
            parentFrame.displayMessage("Invalid move! Please try a different move.", "warning");
        }
    }

    /**
     * Shows a message when player tries to move during opponent's turn.
     */
    protected void showWaitForOpponentMessage() {
        if (parentFrame != null) {
            parentFrame.displayMessage("Wait for your opponent's move!", "warning");
        }
    }

    /*
     * Prompts the user to select a piece for pawn promotion.
     * 
     * @return String representing the chosen piece type (Q, R, B, N), or null if
     * cancelled
     */
    protected String promptForPromotion() {
        String[] options = { "Queen", "Rook", "Bishop", "Knight" };
        int choice = JOptionPane.showOptionDialog(
                null,
                "Choose a piece for pawn promotion:",
                "Pawn Promotion",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        switch (choice) {
            case 0:
                return "Q";
            case 1:
                return "R";
            case 2:
                return "B";
            case 3:
                return "N";
            default:
                return "Q"; // Default to queen if closed/cancelled
        }
    }

    /**
     * Refreshes the board panel display.
     * Used for updating GUI after state changes (e.g., rollback).
     */
    protected void refreshBoardPanel() {
        if (boardPanel != null) {
            boardPanel.repaint();
            boardPanel.revalidate();
        }
    }

    public void end(Color winner) {
        String winnerText = (winner == Color.WHITE) ? "White" : "Black";
        String loserText = (winner == Color.WHITE) ? "Black" : "White";

        // Display in message board
        if (parentFrame != null) {
            parentFrame.displayMessage(
                    "Checkmate! " + loserText + " is in checkmate. " + winnerText + " wins! Game Over.", "info");
        }

        JOptionPane.showMessageDialog(null,
                "Checkmate!\n\n" + loserText + " is in checkmate.\n" + winnerText + " wins!",
                "Chess Game - Checkmate!",
                JOptionPane.INFORMATION_MESSAGE);

        // Clear the game after user clicks OK
        if (parentFrame != null) {
            parentFrame.clearGame();
        }
    }
}
