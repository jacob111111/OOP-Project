package piece;

import java.util.HashSet;
import java.util.Set;

import utils.Color;
import utils.Position;

/**
 * Pawn piece - moves forward one square, captures diagonally.
 * Can move two squares on first move. Subject to promotion at opposite end.
 */
public class Pawn extends Piece {
    /** Tracks whether this pawn has moved from its starting position */
    private boolean hasMoved = false;

    /**
     * Creates a Pawn piece.
     * 
     * @param color    pawn's color
     * @param position initial position
     */
    public Pawn(Color color, Position position) {
        super(color, position);
        this.displaySymbol.append("P");
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

    @Override
    public Set<Position> getPossibleMoves(Object boardObj) {
        board.Board board = (board.Board) boardObj;
        Set<Position> validMoves = new HashSet<>();
        int x = position.getX();
        int y = position.getY();
        int direction = (color == Color.WHITE) ? 1 : -1;
        int newY = y + direction;

        // Forward movement (one square)
        if (newY >= 0 && newY < 8) {
            Position forwardOne = new Position(x, newY);
            if (board.getPieceAt(forwardOne) == null) {
                validMoves.add(forwardOne);

                // Two squares forward on first move
                if (!hasMoved) {
                    int twoSquaresY = y + (direction * 2);
                    if (twoSquaresY >= 0 && twoSquaresY < 8) {
                        Position forwardTwo = new Position(x, twoSquaresY);
                        if (board.getPieceAt(forwardTwo) == null) {
                            validMoves.add(forwardTwo);
                        }
                    }
                }
            }

            // Diagonal captures
            if (x - 1 >= 0) {
                Position diagLeft = new Position(x - 1, newY);
                Piece targetPiece = board.getPieceAt(diagLeft);
                if (targetPiece != null && targetPiece.getColor() != color) {
                    validMoves.add(diagLeft);
                }
            }
            if (x + 1 < 8) {
                Position diagRight = new Position(x + 1, newY);
                Piece targetPiece = board.getPieceAt(diagRight);
                if (targetPiece != null && targetPiece.getColor() != color) {
                    validMoves.add(diagRight);
                }
            }
        }

        return validMoves;
    }
}
