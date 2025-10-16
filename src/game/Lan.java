package game;

import java.util.Scanner;
import utils.Color;

/**
 * Lan class for LAN gameplay.
 * TODO: Add class description and usage details.
 */

public class Lan extends Game {
    public Lan(boolean isPvP, Color p1Color, Scanner scnr) {
        super(isPvP, p1Color, scnr);
    }
    public void play() {
        // Implement the game loop for Player vs AI mode
        System.out.println("Starting Player vs AI game...");
        // Game loop logic goes here
    }
    public void turn() {
        // Implement AI move logic here
        System.out.println("AI is making a move...");
    }
    public Color getWinner() {
        // Implement logic to determine the winner
        return null; // Placeholder
    }
    
}
