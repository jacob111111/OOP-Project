package piece;

import java.util.HashSet;
import java.util.Set;

import utils.Color;
import utils.Position;

/**
 * Represents the King piece in chess.
 * 
 * The King can move one square in any direction (horizontal, vertical, or
 * diagonal).
 * This is the most important piece in chess - the game is lost when the king is
 * checkmated. The king also participates in the special castling move (not yet
 * implemented).
 * 
 */
public class King extends Piece {
    /** Tracks whether this king has moved (used for castling rules) */
    private boolean hasMoved = false;

    /**
     * Creates a new King piece with the specified color and position.
     * 
     * @param color    The color of the king (WHITE or BLACK)
     * @param position The initial position of the king on the board
     */
    public King(Color color, Position position) {
        super(color, position);
        this.displaySymbol.append("K");
    }

    /**
     * Sets whether this king has moved (affects castling eligibility).
     * 
     * @param hasMoved true if the king has moved, false otherwise
     */
    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    /**
     * Gets whether this king has moved from its starting position.
     * 
     * @return true if the king has moved, false if it's still in starting position
     */
    public boolean getHasMoved() {
        return hasMoved;
    }

    @Override
    public Set<Position> getPossibleMoves(Object boardObj) {
        board.Board board = (board.Board) boardObj;
        Set<Position> validMoves = new HashSet<>();
        int x = position.getX();
        int y = position.getY();

        // All 8 adjacent squares
        int[][] kingMoves = {
                { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 },
                { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 }
        };

        for (int[] move : kingMoves) {
            int newX = x + move[0];
            int newY = y + move[1];

            if (newX >= 0 && newX < 8 && newY >= 0 && newY < 8) {
                Position target = new Position(newX, newY);
                Piece targetPiece = board.getPieceAt(target);

                // Can move if square is empty or occupied by enemy
                if (targetPiece == null || targetPiece.getColor() != color) {
                    validMoves.add(target);
                }
            }
        }

        return validMoves;
    }
}
