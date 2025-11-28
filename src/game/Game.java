package game;

import java.io.Serializable;

import board.Board;
import utils.CheckmateDetector;
import utils.Color;

/**
 * Abstract base class for different types of chess games.
 * 
 * This class provides the common structure and functionality for all game modes
 * including turn management, game state tracking, and basic game flow control.
 * Specific game implementations (Console, PVE, Lan) extend this class to
 * provide
 * their own user interface and game logic while sharing common functionality.
 * 
 * The class follows the Template Method pattern where the overall game
 * structure
 * is defined here, but specific implementations of play() and turn() are left
 * to subclasses.
 */
public abstract class Game implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Tracks whose turn it is to move (starts with WHITE) */
    protected Color WhosTurn = Color.WHITE;

    /** The chess board for this game */
    protected Board board;

    /** The winner of the game (null if game is ongoing) */
    protected Color winner;

    /** Reference to the current player (avoids repeated ternary operations) */
    protected player.Player currentPlayer;

    /** Move validation and checkmate detector (set by subclasses) */
    protected CheckmateDetector validMoveDetector;

    /**
     * Creates a new Game with the specified parameters.
     * 
     * @param isPvP   true for Player vs Player mode, false for other modes
     * @param p1Color the color that player 1 will play
     */
    public Game(boolean isPvP, Color p1Color) {
        this.board = new Board(isPvP, p1Color);
        this.currentPlayer = board.getPlayer(Color.WHITE); // White starts
    }

    /**
     * Gets the current player whose turn it is.
     * 
     * @return The current player
     */
    public player.Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Gets the opponent of the current player.
     * 
     * @return The opponent player
     */
    public player.Player getOpponentPlayer() {
        Color opponentColor = (WhosTurn == Color.WHITE) ? Color.BLACK : Color.WHITE;
        return board.getPlayer(opponentColor);
    }

    /**
     * Switches to the next player's turn.
     * Updates both WhosTurn and currentPlayer.
     */
    protected void switchTurn() {
        WhosTurn = (WhosTurn == Color.WHITE) ? Color.BLACK : Color.WHITE;
        currentPlayer = board.getPlayer(WhosTurn);
    }

    /**
     * Gets the winner of the game.
     * 
     * @return The color of the winner, or null if the game is ongoing
     */
    public Color getWinner() {
        return winner;
    }

    /**
     * Gets the move validator and checkmate detector.
     * 
     * @return The CheckmateDetector instance
     */
    public CheckmateDetector getValidMoveDetector() {
        return validMoveDetector;
    }

    /**
     * Sets the move validator and checkmate detector.
     * Should be called by subclasses after board initialization.
     * 
     * @param detector The CheckmateDetector instance to use
     */
    protected void setValidMoveDetector(CheckmateDetector detector) {
        this.validMoveDetector = detector;
    }

    /**
     * Sets the winner of the game.
     * 
     * @param winnerColor The color of the winning player
     */
    public void setWinner(Color winnerColor) {
        this.winner = winnerColor;
    }

    /**
     * Handles the end of the game and displays the winner.
     * 
     * @param winner The color of the winning player
     */
    public abstract void end(Color winner);

    /**
     * Abstract method to handle a single turn of the game.
     * 
     * Each game type must implement this method to define how individual
     * turns are processed, including input handling and move validation.
     */
    public abstract void turn();

    public abstract void displayBoard(Color whosMove);

    public Board getBoard() {
        return board;
    }
}
