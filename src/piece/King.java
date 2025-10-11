package piece;

// Possible change imports to utils.*

import utils.Color;
import utils.Position;

/**
 * King piece class.
 * TODO: Add class description and usage details.
 */
public class King extends Piece {
    public King(Color color, Position position, int ID) {
        super(color, position, ID);
    }

    public boolean canMove(int newX, int newY) {
        return false;
    }
    public boolean canCastle() {
        return false;
    }
    
}
