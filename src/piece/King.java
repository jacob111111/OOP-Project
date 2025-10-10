package piece;
import utils.Color;
/**
 * King piece class.
 * TODO: Add class description and usage details.
 */
public class King extends Piece {
    public King(Color color, int x, int y) {
        super(color, x, y);
    }

    public boolean canCastle() {
        return false;
    }
    
}
