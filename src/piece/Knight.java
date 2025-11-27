package piece;

import utils.Color;
import utils.Position;

/**
 * Represents the Knight piece in chess.
 * 
 * The Knight has a unique L-shaped movement pattern - it moves two squares
 * in one direction (horizontal or vertical) and then one square perpendicular
 * to that direction. The Knight is the only piece that can "jump over" other
 * pieces. It's represented by 'N' to avoid confusion with the King's 'K'.
 * 
 */
public class Knight extends Piece {
    /**
     * Creates a new Knight piece with the specified color and position.
     * 
     * @param color    The color of the knight (WHITE or BLACK)
     * @param position The initial position of the knight on the board
     */
    public Knight(Color color, Position position) {
        super(color, position);
        this.displaySymbol.append("N");
    }
}
