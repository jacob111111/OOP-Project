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

    public boolean canMove(int newX, int newY) {
        return false;
    }
}
