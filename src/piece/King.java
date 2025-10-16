package piece;

import utils.Color;
import utils.Position;

/**
 * King piece class.
 * TODO: Add class description and usage details.
 */

public class King extends Piece {
    private static final int[][] KING_DIRECTIONS = {
        {1, 0}, {-1, 0}, {0, 1}, {0, -1},    // Horizontal/vertical
        {1, 1}, {1, -1}, {-1, 1}, {-1, -1}   // Diagonals
    };

    private boolean hasMoved = false;

    public King(Color color, Position position) {
        super(color, position);
        this.displaySymbol.append("K");
    }

    @Override
    public void findPossibleMoves() {
        possibleMoves.clear();
        int x = position.getX();
        int y = position.getY();
        
        // Move one square in each direction
        for (int[] direction : KING_DIRECTIONS) {
            int newX = x + direction[0];
            int newY = y + direction[1];
            
            // Check if the new position is within board bounds
            if (newX >= 0 && newX < 8 && newY >= 0 && newY < 8) {
                possibleMoves.add(new Position(newX, newY));
            }
        }

        // Castling moves
        if (!hasMoved) {
            // Kingside/short castling
            possibleMoves.add(new Position(x + 2, y));

            // Queenside/long castling
            possibleMoves.add(new Position(x - 2, y));
        }
    }

    // Getter and Setter for hasMoved
    public void setHasMoved(boolean hasMoved) { this.hasMoved = hasMoved; }
    public boolean getHasMoved() { return hasMoved; }
}
