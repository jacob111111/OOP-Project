package piece;

import utils.Color;
import utils.Position;

/**
 * Represents the Bishop piece in chess.
 * 
 * The Bishop moves diagonally any number of squares. Each player starts
 * with two bishops - one on light squares and one on dark squares.
 * A bishop is constrained to the color of square it starts on throughout
 * the entire game. The Bishop extends LinearPiece to inherit linear
 * movement calculation functionality.
 */

public class Bishop extends LinearPiece {

    /**
     * Creates a new Bishop piece with the specified color and position.
     * 
     * @param color    The color of the bishop (WHITE or BLACK)
     * @param position The initial position of the bishop on the board
     */
    public Bishop(Color color, Position position) {
        super(color, position);
        this.displaySymbol.append("B");
    }

    @Override
    public int[][] getDirections() {
        // Bishop moves diagonally only
        return new int[][] {
                { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 }
        };
    }
}
