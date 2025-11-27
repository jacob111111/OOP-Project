package piece;

import utils.Color;
import utils.Position;

/**
 * Represents the Pawn piece in chess.
 * 
 * Pawns have unique movement rules: they move forward one square, capture
 * diagonally,
 * can move two squares on their first move, and have special rules for en
 * passant
 * capture and promotion (not yet fully implemented). White pawns move "up" the
 * board
 * (increasing y) while black pawns move "down" (decreasing y).
 * 
 */
public class Pawn extends Piece {
    /** Tracks whether this pawn has moved from its starting position */
    private boolean hasMoved = false;

    /**
     * Creates a new Pawn piece with the specified color and position.
     * 
     * @param color    The color of the pawn (WHITE or BLACK)
     * @param position The initial position of the pawn on the board
     */
    public Pawn(Color color, Position position) {
        super(color, position);
        this.displaySymbol.append("P");
    }

    /**
     * Sets whether this pawn has moved from its starting position.
     * 
     * @param hasMoved true if the pawn has moved, false otherwise
     */
    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    /**
     * Gets whether this pawn has moved from its starting position.
     * 
     * @return true if the pawn has moved, false if it's still in starting position
     */
    public boolean getHasMoved() {
        return hasMoved;
    }
}
