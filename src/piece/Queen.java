package piece;

import utils.Color;
import utils.Position;

/**
 * Queen piece class.
 * TODO: Add class description and usage details.
 */
public class Queen extends Piece {
    public Queen(Color color, Position position, int ID) {
        super(color, position, ID);
    }

    public boolean canMove(int newX, int newY) {
        return false;
    }
}
