package piece;

import utils.Color;
import utils.Position;

/**
 * Rook piece class.
 * TODO: Add class description and usage details.
 */
public class Rook extends Piece {
    public Rook(Color color, Position position) {
        super(color, position);
    }

    @Override
    public void findPossibleMoves() {
        possibleMoves.clear();
        int x = position.getX();
        int y = position.getY();
    }
}
