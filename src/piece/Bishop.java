package piece;

import utils.Color;
import utils.Position;

/**
 * Bishop piece class.
 * TODO: Add class description and usage details.
 */
public class Bishop extends Piece {
    public Bishop(Color color, Position position) {
        super(color, position);
    }

    @Override
    public void findPossibleMoves() {
        possibleMoves.clear();
        int x = position.getX();
        int y = position.getY();
    }
}
