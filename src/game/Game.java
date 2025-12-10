package game;

import java.io.Serializable;

import board.Board;
import utils.CheckmateDetector;
import utils.Color;

/**
 * Abstract base class for chess game modes.
 * Provides common turn management and game state tracking.
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
    protected transient player.Player currentPlayer;

    /** Move validation and checkmate detector (set by subclasses) */
    protected transient CheckmateDetector validMoveDetector;

    /**
     * Creates a new game with specified parameters.
     * 
     * @param isPvP   true for Player vs Player
     * @param p1Color player 1's color
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
     * 
     * @param detector CheckmateDetector instance to use
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

    public Board getBoard() {
        return board;
    }

    /**
     * Custom deserialization method to reinitialize transient fields.
     * Called automatically during deserialization to restore non-serializable
     * objects.
     */
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        // Reinitialize currentPlayer based on WhosTurn
        this.currentPlayer = board.getPlayer(WhosTurn);
        // Reinitialize validMoveDetector from board
        this.validMoveDetector = board.getCheckmateDetector();
    }
}
