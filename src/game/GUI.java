package game;

import utils.Color;
import javax.swing.JOptionPane;
import java.io.Serializable;


public class GUI extends Game implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private transient gui.chessFrame parentFrame;
    
    /**
     * Creates a new Lan game, 1 or 2 player.
     * 
     * @param isPvP Should the AI be intialized as player 2
     * @param p1Color The color the 1st player will use, same for if isPVP is false
     */
    public GUI(boolean isPvP, Color p1Color) {
        super(isPvP, p1Color);
    }

    /**
     * Sets the parent frame reference for clearing the game.
     * 
     * @param frame The parent chessFrame instance
     */
    public void setParentFrame(gui.chessFrame frame) {
        this.parentFrame = frame;
    }
    
    /**
     * Starts and manages the game loop for Lan multiplayer mode.
     * 
     * This method is currently a placeholder and needs implementation
     * for network communication and synchronization between players.
     */
    public void play() {

    }
    
    /**
     * Handles a single turn in Lan mode.
     * 
     * Manages turn coordination between local and remote players,
     * including network communication for move transmission.
     * Currently a placeholder for future implementation.
     */
    public void turn() {
        // Implement network move coordination logic here
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
    
    /**
     * Checks if the game has ended due to King capture.
     * 
     * @return true if a King has been captured, false otherwise
     */
    public boolean isGameOver() {
        return winner != null;
    }
    
    /**
     * Checks for King capture and sets winner if found.
     * Should be called after each move to detect game end.
     */
    public void checkForKingCapture() {
        // Check if white king is still on the board
        boolean whiteKingExists = false;
        boolean blackKingExists = false;
        
        for (piece.Piece piece : board.getPlayer(Color.WHITE).getCurrentPieces()) {
            if (piece instanceof piece.King) {
                whiteKingExists = true;
                break;
            }
        }
        
        for (piece.Piece piece : board.getPlayer(Color.BLACK).getCurrentPieces()) {
            if (piece instanceof piece.King) {
                blackKingExists = true;
                break;
            }
        }
        
        // Set winner if a king is missing
        if (!whiteKingExists) {
            winner = Color.BLACK;
        } else if (!blackKingExists) {
            winner = Color.WHITE;
        }
    }

    /**
     * Determines the winner of the Lan game.
     * 
     * @return The winning color, or null if game is ongoing
     */
    public Color getWinner() {
        return winner;
    }

    public void displayBoard(Color whosMove){ 

    }
}
