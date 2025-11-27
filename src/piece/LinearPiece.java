package piece;

import utils.Color;
import utils.Position;

/**
 * Abstract base class for chess pieces that move in straight lines.
 * 
 * This class provides common functionality for pieces like Queen, Rook, and
 * Bishop
 * that can move multiple squares in straight lines (horizontal, vertical, or
 * diagonal).
 * 
 */
public abstract class LinearPiece extends Piece {

    /**
     * Creates a new LinearPiece with the specified color and position.
     * 
     * @param color    The color of the piece (WHITE or BLACK)
     * @param position The initial position of the piece on the board
     */
    public LinearPiece(Color color, Position position) {
        super(color, position);
    }

    /**
     * Validates if a target position is on a valid attack line for this linear
     * piece.
     * 
     * This method checks whether the target square lies on a line that this piece
     * can attack along, without considering whether the path is clear. Different
     * piece types have different valid lines:
     * - Rook: Horizontal or vertical lines only
     * - Bishop: Diagonal lines only
     * - Queen: All eight directions (horizontal, vertical, and diagonal)
     * 
     * @param target the target position to check
     * @return true if the target is on a valid attack line for this piece type
     */
    public boolean isOnValidLineTo(Position target) {
        int dx = target.getX() - position.getX();
        int dy = target.getY() - position.getY();

        if (this instanceof Rook) {
            return (dx == 0) || (dy == 0); // Horizontal or vertical
        } else if (this instanceof Bishop) {
            return Math.abs(dx) == Math.abs(dy); // Diagonal
        } else if (this instanceof Queen) {
            return (dx == 0) || (dy == 0) || (Math.abs(dx) == Math.abs(dy)); // All directions
        }

        return false;
    }
}