package board;

import java.io.Serializable;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;

import piece.*;
import player.*;
import utils.Color;
import utils.Position;
import utils.CheckmateDetector;

/**
 * Represents the chess board and manages piece positions and game state.
 * 
 * The Board class is responsible for:
 * - Managing piece positions using an efficient position index for O(1) lookups
 * - Tracking captured pieces for both players
 * - Handling piece movement and capture mechanics
 * - Displaying the board state to players
 * - Managing player instances (human players and AI)
 * 
 * The board uses a coordinate system where positions are represented as
 * (x,y) coordinates with (0,0) at a1 and (7,7) at h8 in standard chess
 * notation.
 * 
 */
public class Board implements Serializable {
    private static final long serialVersionUID = 1L;

    /** The white player instance */
    protected Player white, black;

    /** Dictionary tracking pieces captured by white player */
    private Dictionary<Piece, Integer> whiteHasCaptured = new Hashtable<>();

    /** Dictionary tracking pieces captured by black player */
    private Dictionary<Piece, Integer> blackHasCaptured = new Hashtable<>();

    /** Hash map for O(1) piece position lookups */
    private Map<Position, Piece> positionIndex = new HashMap<>();

    /** Fast checkmate detector using pre-computed attack maps */
    private transient CheckmateDetector checkmateDetector; // Don't serialize this, recreate on load

    /**
     * Constructs a new chess board with players based on game mode.
     * 
     * Creates appropriate player instances based on whether this is a
     * Player vs Player game or Player vs AI game. For PvP mode, both
     * players are human. For PvE mode, one player is human and the other is AI.
     * 
     * @param isPvP   true for Player vs Player mode, false for Player vs AI
     * @param P1Color the color that Player 1 will control (WHITE, BLACK, or RANDOM)
     */
    public Board(boolean isPvP, Color P1Color) {
        if (isPvP) {
            this.white = new Player(Color.WHITE);
            this.black = new Player(Color.BLACK);
        } else {
            Random rand = new Random();
            if (P1Color == Color.RANDOM) { // coin flip decides users (p1's) color
                if (rand.nextBoolean()) {
                    P1Color = Color.WHITE;
                } else {
                    P1Color = Color.BLACK;
                }
            }
            if (P1Color == Color.WHITE) {
                // user chooses white
                this.white = new Player(Color.WHITE);
                this.black = new AI(Color.BLACK);
            } else {
                // user chooses black
                this.white = new AI(Color.WHITE);
                this.black = new Player(Color.BLACK);
            }
        }

        // Initialize position index for O(1) lookups
        // Add all white pieces to index
        for (Piece piece : white.getCurrentPieces()) {
            positionIndex.put(piece.getPosition(), piece);
        }
        // Add all black pieces to index
        for (Piece piece : black.getCurrentPieces()) {
            positionIndex.put(piece.getPosition(), piece);
        }
    }

    /**
     * Gets the dictionary of pieces captured by the specified color.
     * 
     * @param colorOfPiece the color whose captures to retrieve (WHITE or BLACK)
     * @return Dictionary containing captured pieces and their IDs
     */
    public Dictionary<Piece, Integer> getCaptures(Color colorOfPiece) {
        return colorOfPiece == Color.WHITE ? whiteHasCaptured : blackHasCaptured;
    }

    /**
     * Gets the player instance for the specified color.
     * 
     * @param colorOfPiece the color of the player to retrieve (WHITE or BLACK)
     * @return The Player or AI instance for that color
     */
    public Player getPlayer(Color colorOfPiece) {
        return colorOfPiece == Color.WHITE ? white : black;
    }

    /**
     * Gets the piece at the specified position, if any.
     * 
     * Uses the position index for O(1) lookup performance.
     * 
     * @param pos the position to check
     * @return The piece at that position, or null if empty
     */
    public Piece getPieceAt(Position pos) {
        return positionIndex.get(pos);
    }

    /**
     * Updates a piece's position in both the piece object and the position index.
     * 
     * This method maintains consistency between the piece's internal position
     * and the board's position tracking system.
     * 
     * @param piece  the piece being moved
     * @param oldPos the piece's previous position
     * @param newPos the piece's new position
     */
    public void updatePiecePosition(Piece piece, Position oldPos, Position newPos) {
        positionIndex.remove(oldPos);
        positionIndex.put(newPos, piece);
        piece.setPosition(newPos);
    }

    /**
     * Handles piece capture mechanics while maintaining position index consistency.
     * 
     * This method is called once a valid capture has been confirmed. It removes
     * the captured piece from the appropriate player's piece collection, adds it
     * to the capturing player's capture list, and updates the position index.
     * 
     * @param capturingPiece the piece performing the capture
     * @param capturePos     the position where the capture occurs
     * @return true if a King was captured, false otherwise
     */
    public boolean capturePiece(Piece capturingPiece, Position capturePos) {
        Piece capturedPiece = positionIndex.get(capturePos);
        boolean kingCaptured = false;

        if (capturedPiece != null) {
            // Check if the captured piece is a King
            if (capturedPiece instanceof King) {
                kingCaptured = true;
            }

            // Remove from appropriate player's piece list
            if (capturedPiece.getColor() == Color.WHITE) {
                white.getCurrentPieces().remove(capturedPiece);
                blackHasCaptured.put(capturedPiece, capturedPiece.hashCode()); // Using hashCode as ID
            } else {
                black.getCurrentPieces().remove(capturedPiece);
                whiteHasCaptured.put(capturedPiece, capturedPiece.hashCode()); // Using hashCode as ID
            }
        }
        // Update position index
        positionIndex.remove(capturingPiece.getPosition());
        positionIndex.put(capturePos, capturingPiece);

        return kingCaptured;
    }

    /**
     * Adds a captured piece to the specified capture dictionary.
     * 
     * @param dict     the capture dictionary to add to
     * @param capPiece the piece that was captured
     */
    public void addPieceToCaptures(Dictionary<Piece, Integer> dict, Piece capPiece) {
        dict.put(capPiece, capPiece.hashCode()); // Using hashCode as piece ID
    }

    /**
     * Used exclusively in Console.java
     * Converts chess notation (like "e4") to a Position object
     * Chess board: a-h columns (0-7), 1-8 rows (0-7)
     * 
     * @param notation Chess notation string (e.g., "e4")
     * @return Position object or null if invalid notation
     */
    public Position chessNotationToPosition(String notation) {
        if (notation == null || notation.length() != 2) {
            return null;
        }

        char file = notation.toLowerCase().charAt(0); // column (a-h)
        char rank = notation.charAt(1); // row (1-8)

        // Validate input
        if (file < 'a' || file > 'h' || rank < '1' || rank > '8') {
            return null;
        }

        // Convert to 0-based coordinates
        int x = file - 'a'; // a=0, b=1, ..., h=7
        int y = rank - '1'; // 1=0, 2=1, ..., 8=7

        return new Position(x, y);
    }
}