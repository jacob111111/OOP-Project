package piece;

import utils.Color;
import utils.Position;

/**
 * Queen piece - moves any distance horizontally, vertically, or diagonally.
 * Most powerful piece combining Rook and Bishop movement.
 */
public class Queen extends LinearPiece {

    /**
     * Creates a Queen piece.
     * 
     * @param color    queen's color
     * @param position initial position
     */
    public Queen(Color color, Position position) {
        super(color, position);
        this.displaySymbol.append("Q");
    }

    @Override
    public int[][] getDirections() {
        // Queen moves in all 8 directions: horizontal, vertical, and diagonal
        return new int[][] {
                { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 },
                { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 }
        };
    }
}
