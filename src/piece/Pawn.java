package piece;

import utils.Color;
import utils.Position;

/**
 * Pawn piece class.
 * TODO: Add class description and usage details.
 */

public class Pawn extends Piece {
    private boolean hasMoved = false;

    public Pawn(Color color, Position position) {
        super(color, position);
        this.displaySymbol.append("P");
    }

    @Override
    public void findPossibleMoves() {
        possibleMoves.clear();
        int x = position.getX();
        int y = position.getY();

        int moveDirection = (color == Color.WHITE) ? 1 : -1;

        //  one-square move
        int newY = y + moveDirection;
        if (newY >= 0 && newY < 8) {
            possibleMoves.add(new Position(x, newY));
            // two-square move
            if (!hasMoved && newY + moveDirection >= 0 && newY + moveDirection < 8) {
                possibleMoves.add(new Position(x, newY + moveDirection));
            }
        }

        // captures
        if(newY >= 0 && newY < 8) {
            if (x - 1 >= 0) {
                possibleMoves.add(new Position(x - 1, newY));
            }
            if (x + 1 < 8) {
                possibleMoves.add(new Position(x + 1, newY));
            }
        }
    }

    // Getter and Setter for hasMoved
    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }
    public boolean getHasMoved() {
        return hasMoved;
    }
}
