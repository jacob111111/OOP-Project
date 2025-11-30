package game;

import utils.Color;
import utils.Position;
import javax.swing.JOptionPane;

import piece.Piece;

public class GUI extends Game {
    private transient gui.board.BoardPanel boardPanel;
    private transient gui.chessFrame parentFrame;

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
        // Check for captures BEFORE moving
        Piece capturedPiece = board.getPieceAt(toPosition);
        boolean isCapture = capturedPiece != null && capturedPiece.getColor() != pieceToMove.getColor();

        // Validate the move (reachability + king safety)
        boolean moveSuccessful = board.attemptMove(toPosition, pieceToMove);

        if (!moveSuccessful) {
            showInvalidMoveMessage();
            return false;
        }

        // Step 5: Execute the move on backend and handle captures
        if (isCapture) {
            // Capture the piece at destination
            board.capturePiece(pieceToMove, toPosition, capturedPiece);
        }

        // Update piece position on backend
        board.updatePiecePosition(pieceToMove, fromPosition, toPosition);

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

        return true;
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
