package piece;

import utils.Color;
import utils.Position;

/**
 * Pawn piece class.
 * TODO: Add class description and usage details.
 */
public class Pawn extends Piece {
    public Pawn(Color color, Position position, int ID) {
        super(color, position, ID);
    }

    @Override
    public void findPossibleMoves() {
        possibleMoves.clear();
        int x = position.getX();
        int y = position.getY();
    }
}
