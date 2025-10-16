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
    /** All eight possible L-shaped moves a knight can make */
    private static final int[][] KNIGHT_DIRECTIONS = {
        {2, 1}, {2, -1}, {-2, 1}, {-2, -1},  // 2 horizontal, 1 vertical
        {1, 2}, {1, -2}, {-1, 2}, {-1, -2}   // 1 horizontal, 2 vertical
    };

    /**
     * Creates a new Knight piece with the specified color and position.
     * 
     * @param color The color of the knight (WHITE or BLACK)
     * @param position The initial position of the knight on the board
     */
    public Knight(Color color, Position position) {
        super(color, position);
        this.displaySymbol.append("N");
    }

    /**
     * Calculates all possible moves for the knight from its current position.
     * 
     * The knight moves in an L-shape: two squares in one direction and then
     * one square perpendicular. This method calculates all eight possible
     * L-shaped moves and adds valid ones (within board bounds) to the
     * possible moves list.
     */
    @Override
    public void findPossibleMoves() {
        possibleMoves.clear();
        int x = position.getX();
        int y = position.getY();

        for (int[] direction : KNIGHT_DIRECTIONS) {
            int newX = x + direction[0];
            int newY = y + direction[1];
            
            if (newX >= 0 && newX < 8 && newY >= 0 && newY < 8) {
                possibleMoves.add(new Position(newX, newY));
            }
        }
    }
}
