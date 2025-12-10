package piece;

import java.util.HashSet;
import java.util.Set;

import utils.Color;
import utils.Position;

/**
 * Base class for pieces that move in straight lines (Queen, Rook, Bishop).
 */
public abstract class LinearPiece extends Piece {

    /**
     * Creates a LinearPiece.
     * 
     * @param color    piece color
     * @param position initial position
     */
    public LinearPiece(Color color, Position position) {
        super(color, position);
    }

    /**
     * Checks if target is on a valid attack line (Rook: h/v, Bishop: diag, Queen:
     * all).
     * 
     * @param target target position
     * @return true if on valid line
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

    /**
     * Gets direction vectors this piece can move along.
     * 
     * @return array of [dx, dy] direction vectors
     */
    public abstract int[][] getDirections();

    @Override
    public Set<Position> getPossibleMoves(Object boardObj) {
        board.Board board = (board.Board) boardObj;
        Set<Position> validMoves = new HashSet<>();
        int x = position.getX();
        int y = position.getY();

        int[][] directions = getDirections();

        for (int[] dir : directions) {
            // Slide along this direction until we hit something
            for (int i = 1; i < 8; i++) {
                int newX = x + (dir[0] * i);
                int newY = y + (dir[1] * i);

                // Check board boundaries
                if (newX < 0 || newX >= 8 || newY < 0 || newY >= 8) {
                    break;
                }

                Position target = new Position(newX, newY);
                Piece targetPiece = board.getPieceAt(target);

                if (targetPiece == null) {
                    // Empty square - can move here and continue
                    validMoves.add(target);
                } else {
                    // Hit a piece
                    if (targetPiece.getColor() != color) {
                        // Enemy piece - can capture
                        validMoves.add(target);
                    }
                    // Stop sliding in this direction (whether enemy or ally)
                    break;
                }
            }
        }

        return validMoves;
    }
}