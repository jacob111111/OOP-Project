package piece;

import utils.Color;
import utils.Position;

/**
 * Rook piece class.
 * TODO: Add class description and usage details.
 */
public class Rook extends Piece {
    public Rook(Color color, Position position, int ID) {
        super(color, position, ID);
    }

    public boolean canMove(int newX, int newY) {
        return false;
    }
}
