package player;

import java.io.Serializable;
import java.util.ArrayList;

import piece.*;
import utils.Color;
import utils.Position;

/**
 * Represents a chess player and manages their pieces.
 * Base class for both human and AI players.
 */
public class Player implements Serializable {
    private static final long serialVersionUID = 1L;

    /** The color this player controls (WHITE or BLACK) */
    protected Color color;

    /** List of all pieces currently owned by this player */
    protected ArrayList<Piece> currentPieces;

    /**
     * Creates a player with specified color and initializes pieces.
     * 
     * @param color player's color (WHITE or BLACK)
     */
    public Player(Color color) {
        this.currentPieces = new ArrayList<Piece>();
        this.color = color;
        initializePieces();
    }

    /**
     * Gets the color this player controls.
     * 
     * @return The player's color (WHITE or BLACK)
     */
    public Color getColor() {
        return color;
    }

    /**
     * Gets the list of pieces currently owned by this player.
     * 
     * @return ArrayList of all pieces belonging to this player
     */
    public ArrayList<Piece> getCurrentPieces() {
        return currentPieces;
    }

    /**
     * Finds and returns the king's position.
     * 
     * @return king's position, or null if not found
     */
    public Position getKingPosition() {
        for (Piece piece : currentPieces) {
            if (piece instanceof King) {
                return piece.getPosition();
            }
        }
        return null; // should never happen in a valid game
    }

    /**
     * Sets up all pieces in standard chess starting positions.
     */
    private void initializePieces() {
        int colorOffset = (color == Color.WHITE) ? 0 : 7;
        int pawnRow = (color == Color.WHITE) ? 1 : 6;

        currentPieces.add(new King(color, new Position(4, colorOffset)));
        currentPieces.add(new Queen(color, new Position(3, colorOffset)));

        currentPieces.add(new Rook(color, new Position(0, colorOffset)));
        currentPieces.add(new Rook(color, new Position(7, colorOffset)));

        currentPieces.add(new Bishop(color, new Position(2, colorOffset)));
        currentPieces.add(new Bishop(color, new Position(5, colorOffset)));

        currentPieces.add(new Knight(color, new Position(1, colorOffset)));
        currentPieces.add(new Knight(color, new Position(6, colorOffset)));

        for (int i = 0; i < 8; i++) {
            currentPieces.add(new Pawn(color, new Position(i, pawnRow)));
        }
    }
}