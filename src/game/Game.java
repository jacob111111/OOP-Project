package game;

import java.util.Scanner;

import board.Board;
import utils.Color;

/**
 * Abstract base class for different types of chess games.
 * 
 * This class provides the common structure and functionality for all game modes
 * including turn management, game state tracking, and basic game flow control.
 * Specific game implementations (Console, PVE, LAN) extend this class to provide
 * their own user interface and game logic while sharing common functionality.
 * 
 * The class follows the Template Method pattern where the overall game structure
 * is defined here, but specific implementations of play() and turn() are left
 * to subclasses.
 */
public abstract class Game {
    /** Tracks whose turn it is to move (starts with WHITE) */
    protected Color WhosTurn = Color.WHITE;
    
    /** The chess board for this game */
    protected Board board;
    
    /** Scanner for reading user input */
    protected Scanner scnr;
    
    
    /** The winner of the game (null if game is ongoing) */
    protected Color winner;

    /**
     * Creates a new Game with the specified parameters.
     * 
     * @param isPvP true for Player vs Player mode, false for other modes
     * @param p1Color the color that player 1 will play
     * @param scnr Scanner for reading user input
     */
    public Game(boolean isPvP, Color p1Color, Scanner scnr){
        this.board = new Board(isPvP, p1Color);
        this.scnr = scnr;
    }

    /**
     * Gets the winner of the game.
     * 
     * @return The color of the winner, or null if the game is ongoing
     */
    public Color getWinner() { return winner; }

    /**
     * Sets the winner of the game.
     * 
     * @param winnerColor The color of the winning player
     */
    public void setWinner(Color winnerColor){ this.winner = winnerColor; } 

    /**
     * Factory method to create appropriate game instances based on user choice.
     * 
     * Prompts the user for game type and player color, then creates and returns
     * the appropriate game instance. Currently supports Console mode, with
     * other modes falling back to Console implementation.
     * 
     * @return A new Game instance of the selected type
     */
    public Game createGame(){
        System.out.println("What type of game do you want to play?");
        System.out.println("1. Console   2. Player vs AI   3. Player vs Player ");
        System.out.print("Input 1-3: ");

        int userResponse = scnr.nextInt();
        
        // Ask for player color (common for all game types)
        System.out.println("Choose your color:");
        System.out.println("1. White   2. Black");
        System.out.print("Input 1-2: ");
        int colorChoice = scnr.nextInt();
        Color p1Color = (colorChoice == 1) ? Color.WHITE : Color.BLACK;
        
        switch(userResponse){
            case 1: // Console
                return new Console(false, p1Color, scnr); // isPvP = false for console mode
            case 2: // PvE (Player vs AI)
                // You'll need to create a PvE class similar to Console
                System.out.println("Player vs AI not yet implemented");
                return new Console(false, p1Color, scnr); // Fallback to console for now
            case 3: // PvP (Player vs Player)
                // You'll need to create a PvP class similar to Console
                System.out.println("Player vs Player not yet implemented");
                return new Console(true, p1Color, scnr); // isPvP = true for PvP mode
            default:
                System.out.println("Invalid choice, defaulting to Console mode");
                return new Console(false, p1Color, scnr);
        }
    }

    /**
     * Handles the end of the game and displays the winner.
     * 
     * @param winner The color of the winning player
     */
    public void end(Color winner){
        System.out.println("Game over! The winner is " + winner);
    }
    
    /**
     * Abstract method to start and manage the main game loop.
     * 
     * Each game type must implement this method to define how the game
     * progresses from start to finish.
     */
    public abstract void play();
    
    /**
     * Abstract method to handle a single turn of the game.
     * 
     * Each game type must implement this method to define how individual
     * turns are processed, including input handling and move validation.
     */
    public abstract void turn();
}
