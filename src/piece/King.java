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

    @Override
    public void findPossibleMoves() {
        possibleMoves.clear();
        int x = position.getX();
        int y = position.getY();
    }
    public boolean canCastle() {
        return false;
    }
    
}
