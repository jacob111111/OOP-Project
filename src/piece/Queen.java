package piece;

import utils.Color;
import utils.Position;

/**
 * Represents the Queen piece in chess.
 * 
 * The Queen is the most powerful piece on the chessboard, combining the
 * movement
 * of both a Rook and Bishop. It can move any number of squares horizontally,
 * vertically, or diagonally as long as the path is clear. The Queen extends
 * LinearPiece to inherit the linear movement calculation functionality.
 * 
 */
public class Queen extends LinearPiece {

    /**
     * Creates a new Queen piece with the specified color and position.
     * 
     * @param color    The color of the queen (WHITE or BLACK)
     * @param position The initial position of the queen on the board
     */
    public Queen(Color color, Position position) {
        super(color, position);
        this.displaySymbol.append("Q");
    }
}
