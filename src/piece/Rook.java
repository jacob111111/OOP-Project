package piece;

import utils.Color;
import utils.Position;

/**
 * Rook piece - moves any distance horizontally or vertically.
 * Participates in castling with the King.
 */
public class Rook extends LinearPiece {
    /** Tracks whether this rook has moved (used for castling rules) */
    private boolean hasMoved = false;

    /**
     * Creates a Rook piece.
     * 
     * @param color    rook's color
     * @param position initial position
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