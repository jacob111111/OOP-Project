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
 * It implements the shared linear movement calculation logic that these pieces
 * use.
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
     * Calculates possible moves in the specified directions.
     * 
     * For each direction, this method calculates all valid moves by extending
     * from the current position until hitting a board boundary. This method
     * is used by subclasses to implement their specific movement patterns.
     * 
     * @param directions Array of direction vectors, where each direction is
     *                   represented as {dx, dy} indicating the change in x and y
     */
    protected void addMovesInDirections(int[][] directions) {
        possibleMoves.clear();
        int x = position.getX();
        int y = position.getY();

        for (int[] dir : directions) {
            for (int i = 1; i < 8; i++) {
                int newX = x + (dir[0] * i);
                int newY = y + (dir[1] * i);

                if (newX >= 0 && newX < 8 && newY >= 0 && newY < 8) {
                    possibleMoves.add(new Position(newX, newY));
                } else {
                    break;
                }
            }
        }
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