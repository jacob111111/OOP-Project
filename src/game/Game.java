package game;

import java.util.Scanner;

import board.Board;
import utils.Color;

/**
 * Abstract base class for different types of chess games.
 * 
 * This class provides the common structure and functionality for all game modes
 * including turn management, game state tracking, and basic game flow control.
 * Specific game implementations (Console, PVE, Lan) extend this class to provide
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
    
    /** The winner of the game (null if game is ongoing) */
    protected Color winner;

    /**
     * Creates a new Game with the specified parameters.
     * 
     * @param isPvP true for Player vs Player mode, false for other modes
     * @param p1Color the color that player 1 will play
     */
    public Game(boolean isPvP, Color p1Color){
        this.board = new Board(isPvP, p1Color);
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
     * Handles the end of the game and displays the winner.
     * 
     * @param winner The color of the winning player
     */
    public abstract void end(Color winner);
    
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

    public abstract void displayBoard(Color whosMove);
    
    public Board getBoard() { return board; }
}
