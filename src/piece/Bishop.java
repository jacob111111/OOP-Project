package piece;

import utils.Color;
import utils.Position;

/**
 * Bishop piece class.
 * TODO: Add class description and usage details.
 */
public class Bishop extends Piece {
    public Bishop(Color color, Position position, int ID) {
        super(color, position, ID);
    }

    public boolean canMove(int newX, int newY) {
        return false;
    }
}
