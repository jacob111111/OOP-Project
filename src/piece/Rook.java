package piece;

import utils.Color;
import utils.Position;

/**
 * Represents the Rook piece in chess.
 * 
 * The Rook moves horizontally and vertically any number of squares.
 * It is one of the most valuable pieces after the Queen and participates
 * in the castling move with the King. The Rook extends LinearPiece to
 * inherit linear movement calculation functionality.
 * 
 */
public class Rook extends LinearPiece {
    /** Tracks whether this rook has moved (used for castling rules) */
    private boolean hasMoved = false;

    /**
     * Creates a new Rook piece with the specified color and position.
     * 
     * @param color    The color of the rook (WHITE or BLACK)
     * @param position The initial position of the rook on the board
     */
    public Rook(Color color, Position position) {
        super(color, position);
        this.displaySymbol.append("R");
    }

    /**
     * Sets whether this rook has moved (affects castling eligibility).
     * 
     * @param hasMoved true if the rook has moved, false otherwise
     */
    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    /**
     * Gets whether this rook has moved from its starting position.
     * 
     * @return true if the rook has moved, false if it's still in starting position
     */
    public boolean getHasMoved() {
        return hasMoved;
    }

    @Override
    public int[][] getDirections() {
        // Rook moves horizontally and vertically only
        return new int[][] {
                { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 }
        };
    }
}