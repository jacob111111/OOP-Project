package game;

import utils.Color;


public class GUI extends Game {
    
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
        
    }
    
    /**
     * Determines the winner of the Lan game.
     * 
     * @return The winning color, or null if game is ongoing (placeholder)
     */
    public Color getWinner() {
        // Implement logic to determine the winner
        return null; // Placeholder
    }

    public void displayBoard(Color whosMove){ 

    }
}
