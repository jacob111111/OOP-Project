package piece;

import utils.Color;
import utils.Position;

/**
 * Bishop piece class.
 * TODO: Add class description and usage details.
 */

public class Bishop extends LinearPiece {
    private static final int[][] BISHOP_DIRECTIONS = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

    public Bishop(Color color, Position position) {
        super(color, position);
        this.displaySymbol.append("B");
    }

    @Override
    public void findPossibleMoves() {
        addMovesInDirections(BISHOP_DIRECTIONS);
    }
}
