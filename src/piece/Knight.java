package piece;

import utils.Color;
import utils.Position;

/**
 * Knight piece class.
 * TODO: Add class description and usage details.
 */

public class Knight extends Piece {
    private static final int[][] KNIGHT_DIRECTIONS = {
        {2, 1}, {2, -1}, {-2, 1}, {-2, -1},  // 2x1 moves
        {1, 2}, {1, -2}, {-1, 2}, {-1, -2}   // 1x2 moves
    };

    public Knight(Color color, Position position) {
        super(color, position);
        this.displaySymbol.append("N");
    }

    @Override
    public void findPossibleMoves() {
        possibleMoves.clear();
        int x = position.getX();
        int y = position.getY();

        for (int[] direction : KNIGHT_DIRECTIONS) {
            int newX = x + direction[0];
            int newY = y + direction[1];
            
            if (newX >= 0 && newX < 8 && newY >= 0 && newY < 8) {
                possibleMoves.add(new Position(newX, newY));
            }
        }
    }
}
