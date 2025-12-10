package piece;

import utils.Color;
import utils.Position;

/**
 * Bishop piece - moves any distance diagonally.
 * Constrained to its starting square color throughout the game.
 */

public class Bishop extends LinearPiece {

    /**
     * Creates a Bishop piece.
     * 
     * @param color    bishop's color
     * @param position initial position
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
