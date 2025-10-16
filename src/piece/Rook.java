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
    /** The four directions a rook can move (horizontal and vertical) */
    private static final int[][] ROOK_DIRECTIONS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
    
    /**
     * Creates a new Rook piece with the specified color and position.
     * 
     * @param color The color of the rook (WHITE or BLACK)
     * @param position The initial position of the rook on the board
     */
    public Rook(Color color, Position position) {
        super(color, position);
        this.displaySymbol.append("R");
    }

    /**
     * Calculates all possible moves for the rook from its current position.
     * 
     * Uses the inherited addMovesInDirections method to calculate linear moves
     * in the four horizontal and vertical directions.
     */
    @Override
    public void findPossibleMoves() {
        addMovesInDirections(ROOK_DIRECTIONS);
    }
}