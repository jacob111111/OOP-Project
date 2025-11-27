package piece;

import utils.Color;
import utils.Position;

/**
 * Represents the Rook piece in chess.
 * 
 * The Rook moves horizontally and vertically any number of squares.
 * It is one of the most valuable pieces after the Queen and participates
 * in the castling move with the King. The Rook extends LinearPiece to
 * inherit linear movement calculation functionality.
 * 
 */
public class Rook extends LinearPiece {

    /**
     * Creates a new Rook piece with the specified color and position.
     * 
     * @param color    The color of the rook (WHITE or BLACK)
     * @param position The initial position of the rook on the board
     */
    public Rook(Color color, Position position) {
        super(color, position);
        this.displaySymbol.append("R");
    }
}