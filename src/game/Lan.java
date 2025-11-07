package game;

import java.util.Scanner;
import utils.Color;

/**
 * Local Area Network (Lan) multiplayer game implementation.
 * 
 * This class represents a chess game played over a local network between
 * two human players on different machines. Currently this is a placeholder
 * implementation that needs network functionality to be developed.
 * 
 * The class will handle network communication, move synchronization,
 * and remote player connectivity when fully implemented.
 * 
 * @see Game
 */
public class Lan extends Game {
    
    /**
     * Creates a new Lan multiplayer game.
     * 
     * @param isPvP Should be true for Lan multiplayer mode
     * @param p1Color The color the local player will play
     * @param scnr Scanner for reading local player input
     */
    public Lan(boolean isPvP, Color p1Color, Scanner scnr) {
        super(isPvP, p1Color, scnr);
    }
    
    /**
     * Starts and manages the game loop for Lan multiplayer mode.
     * 
     * This method is currently a placeholder and needs implementation
     * for network communication and synchronization between players.
     */
    public void play() {
        // Implement the game loop for Lan multiplayer mode
        System.out.println("Starting Lan multiplayer game...");
        // Network game loop logic goes here
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
        System.out.println("Waiting for player move over network...");
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
