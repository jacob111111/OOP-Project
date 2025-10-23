package piece;

import utils.Color;
import utils.Position;

/**
 * Represents the Queen piece in chess.
 * 
 * The Queen is the most powerful piece on the chessboard, combining the movement
 * of both a Rook and Bishop. It can move any number of squares horizontally,
 * vertically, or diagonally as long as the path is clear. The Queen extends
 * LinearPiece to inherit the linear movement calculation functionality.
 * 
 */
public class Queen extends LinearPiece {
    /** All eight directions the queen can move (combining rook and bishop movements) */
    private static final int[][] QUEEN_DIRECTIONS = {
        {1, 0}, {-1, 0}, {0, 1}, {0, -1},   // Rook-like moves (horizontal/vertical)
        {1, 1}, {1, -1}, {-1, 1}, {-1, -1}  // Bishop-like moves (diagonals)
    };

    /**
     * Creates a new Queen piece with the specified color and position.
     * 
     * @param color The color of the queen (WHITE or BLACK)
     * @param position The initial position of the queen on the board
     */
    public Queen(Color color, Position position) {
        super(color, position);
        this.displaySymbol.append("Q");
    }
    
    /**
     * Calculates all possible moves for the queen from its current position.
     * 
     * Uses the inherited addMovesInDirections method to calculate linear moves
     * in all eight directions (horizontal, vertical, and diagonal).
     */
    @Override
    public void findPossibleMoves() {
        addMovesInDirections(QUEEN_DIRECTIONS);
    }
}
