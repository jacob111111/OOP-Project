package piece;

import utils.Color;
import utils.Position;

/**
 * Represents the King piece in chess.
 * 
 * The King can move one square in any direction (horizontal, vertical, or diagonal).
 * This is the most important piece in chess - the game is lost when the king is
 * checkmated. The king also participates in the special castling move (not yet implemented).
 * 
 */
public class King extends Piece {
    /** All possible movement directions for a king (8 directions: horizontal, vertical, diagonal) */
    private static final int[][] KING_DIRECTIONS = {
        {1, 0}, {-1, 0}, {0, 1}, {0, -1},    // Horizontal/vertical
        {1, 1}, {1, -1}, {-1, 1}, {-1, -1}   // Diagonals
    };

    /** Tracks whether this king has moved (used for castling rules) */
    private boolean hasMoved = false;

    /**
     * Creates a new King piece with the specified color and position.
     * 
     * @param color The color of the king (WHITE or BLACK)
     * @param position The initial position of the king on the board
     */
    public King(Color color, Position position) {
        super(color, position);
        this.displaySymbol.append("K");
    }

    /**
     * Calculates all possible moves for the king from its current position.
     * 
     * The king can move one square in any of eight directions (horizontal, vertical, diagonal).
     * Also includes castling moves if the king hasn't moved yet. Note that this method
     * only calculates basic movement - additional validation for check/checkmate and
     * castling prerequisites is handled elsewhere.
     */
    @Override
    public void findPossibleMoves() {
        possibleMoves.clear();
        int x = position.getX();
        int y = position.getY();
        
        // Move one square in each direction
        for (int[] direction : KING_DIRECTIONS) {
            int newX = x + direction[0];
            int newY = y + direction[1];
            
            // Check if the new position is within board bounds
            if (newX >= 0 && newX < 8 && newY >= 0 && newY < 8) {
                possibleMoves.add(new Position(newX, newY));
            }
        }

        // Castling moves
        if (!hasMoved) {
            // Kingside/short castling
            possibleMoves.add(new Position(x + 2, y));

            // Queenside/long castling
            possibleMoves.add(new Position(x - 2, y));
        }
    }

    /**
     * Sets whether this king has moved (affects castling eligibility).
     * 
     * @param hasMoved true if the king has moved, false otherwise
     */
    public void setHasMoved(boolean hasMoved) { this.hasMoved = hasMoved; }
    
    /**
     * Gets whether this king has moved from its starting position.
     * 
     * @return true if the king has moved, false if it's still in starting position
     */
    public boolean getHasMoved() { return hasMoved; }
}
