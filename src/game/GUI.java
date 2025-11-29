package game;

import utils.Color;
import utils.Position;
import javax.swing.JOptionPane;

import piece.Piece;

public class GUI extends Game {
    private transient gui.board.BoardPanel boardPanel;
    private transient gui.chessFrame parentFrame;

    /**
     * Creates a new Lan game, 1 or 2 player.
     * 
     * @param isPvP   Should the AI be intialized as player 2
     * @param p1Color The color the 1st player will use, same for if isPVP is false
     */
    public GUI(boolean isPvP, Color p1Color) {
        super(isPvP, p1Color);
        // Initialize move validator from board
        setValidMoveDetector(board.getCheckmateDetector());
    }

    /**
     * In co-op mode, these method are intentionally left empty because the game
     * is event-driven rather than blocking based.
     */
    public void turn() {
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
     * 1. Get current player
     * 2. Validate move (BE)
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
        // Piece color validation is done here to maintain OOP architecture
        if (pieceToMove.getColor() == currentPlayer.getColor()) {
            // Validate the move (BE validation)
            boolean moveSuccessful = board.attemptMove(toPosition, pieceToMove);

            if (!moveSuccessful) {
                // Invalid move - show popup and return false
                showInvalidMovePopup();
                return false;
            }

            // Step 4: Execute the move on backend And Step 5: Handle captures
            Piece capturedPiece = board.getPieceAt(toPosition);
            boolean kingCaptured = false;

            // Check if there's a piece to capture and it's not the same color
            if (capturedPiece != null && capturedPiece.getColor() != pieceToMove.getColor()) {
                // Capture the piece at destination
                kingCaptured = board.capturePiece(pieceToMove, toPosition);
            }

            // Update piece position on backend
            board.updatePiecePosition(pieceToMove, fromPosition, toPosition);

            // Check if game ended by King capture
            if (kingCaptured) {
                winner = WhosTurn;
                end(winner);
                return true;
            }

            // Step 7: Look for checkmate
            player.Player opponent = getOpponentPlayer();

            if (validMoveDetector != null && validMoveDetector.isCheckmate(opponent.getColor())) {
                winner = WhosTurn;
                System.out.println("Checkmate! " + winner + " wins!");
                end(winner);
                return true;
            }

            // Step 8: Switch turns on BE (FE board flip handled in BoardPanel)
            switchTurn();

            return true;
        }
        return false;
    }

    /**
     * Shows a popup message when an invalid move is attempted.
     */
    protected void showInvalidMovePopup() {
        JOptionPane.showMessageDialog(null,
                "Invalid move! Please try a different move.",
                "Invalid Move",
                JOptionPane.WARNING_MESSAGE);
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
        JOptionPane.showMessageDialog(null,
                winnerText + " wins by capturing the King!\n\nGame Over",
                "Chess Game - Winner!",
                JOptionPane.INFORMATION_MESSAGE);

        // Clear the game after user clicks OK
        if (parentFrame != null) {
            parentFrame.clearGame();
        }
    }
}
