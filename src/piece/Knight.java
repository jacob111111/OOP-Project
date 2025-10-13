package piece;

import utils.Color;
import utils.Position;

/**
 * Knight piece class.
 * TODO: Add class description and usage details.
 */
public class Knight extends Piece {
    public Knight(Color color, Position position) {
        super(color, position);
    }

    @Override
    public void findPossibleMoves() {
        possibleMoves.clear();
        int x = position.getX();
        int y = position.getY();
    }
}
