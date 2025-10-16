package piece;

import utils.Color;
import utils.Position;

/**
 * Represents the Pawn piece in chess.
 * 
 * Pawns have unique movement rules: they move forward one square, capture diagonally,
 * can move two squares on their first move, and have special rules for en passant
 * capture and promotion (not yet fully implemented). White pawns move "up" the board
 * (increasing y) while black pawns move "down" (decreasing y).
 * 
 */
public class Pawn extends Piece {
    /** Tracks whether this pawn has moved from its starting position */
    private boolean hasMoved = false;

    /**
     * Creates a new Pawn piece with the specified color and position.
     * 
     * @param color The color of the pawn (WHITE or BLACK)
     * @param position The initial position of the pawn on the board
     */
    public Pawn(Color color, Position position) {
        super(color, position);
        this.displaySymbol.append("P");
    }

    /**
     * Calculates all possible moves for the pawn from its current position.
     * 
     * Pawns move forward one square, or two squares on their first move.
     * They capture diagonally forward. White pawns move in the positive y direction,
     * black pawns move in the negative y direction. This method includes both
     * movement and capture possibilities.
     */
    @Override
    public void findPossibleMoves() {
        possibleMoves.clear();
        int x = position.getX();
        int y = position.getY();

        int moveDirection = (color == Color.WHITE) ? 1 : -1;

        // Forward movement: one-square move
        int newY = y + moveDirection;
        if (newY >= 0 && newY < 8) {
            possibleMoves.add(new Position(x, newY));
            // Forward movement: two-square move (only if pawn hasn't moved)
            if (!hasMoved && newY + moveDirection >= 0 && newY + moveDirection < 8) {
                possibleMoves.add(new Position(x, newY + moveDirection));
            }
        }

        // Diagonal captures
        if(newY >= 0 && newY < 8) {
            if (x - 1 >= 0) {
                possibleMoves.add(new Position(x - 1, newY));
            }
            if (x + 1 < 8) {
                possibleMoves.add(new Position(x + 1, newY));
            }
        }
    }

    /**
     * Sets whether this pawn has moved from its starting position.
     * 
     * @param hasMoved true if the pawn has moved, false otherwise
     */
    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }
    
    /**
     * Gets whether this pawn has moved from its starting position.
     * 
     * @return true if the pawn has moved, false if it's still in starting position
     */
    public boolean getHasMoved() {
        return hasMoved;
    }
}
