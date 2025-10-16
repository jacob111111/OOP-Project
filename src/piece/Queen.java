package piece;

import utils.Color;
import utils.Position;

/**
 * Queen piece class.
 * TODO: Add class description and usage details.
 */
public class Queen extends LinearPiece {
    private static final int[][] QUEEN_DIRECTIONS = {
        {1, 0}, {-1, 0}, {0, 1}, {0, -1},   // Rook-like moves
        {1, 1}, {1, -1}, {-1, 1}, {-1, -1}  // Bishop-like moves
    };

    public Queen(Color color, Position position) {
        super(color, position);
        this.displaySymbol.append("Q");
    }
    
    @Override
    public void findPossibleMoves() {
        addMovesInDirections(QUEEN_DIRECTIONS);
    }
}
