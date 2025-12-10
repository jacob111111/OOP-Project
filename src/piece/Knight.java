package piece;

import java.util.HashSet;
import java.util.Set;

import utils.Color;
import utils.Position;

/**
 * Knight piece - moves in L-shape (2+1 squares).
 * Only piece that can jump over others.
 */
public class Knight extends Piece {
    /**
     * Creates a Knight piece.
     * 
     * @param color    knight's color
     * @param position initial position
     */
    public Knight(Color color, Position position) {
        super(color, position);
        this.displaySymbol.append("N");
    }

    @Override
    public Set<Position> getPossibleMoves(Object boardObj) {
        board.Board board = (board.Board) boardObj;
        Set<Position> validMoves = new HashSet<>();
        int x = position.getX();
        int y = position.getY();

        // All 8 possible L-shaped moves
        int[][] knightMoves = {
                { 2, 1 }, { 2, -1 }, { -2, 1 }, { -2, -1 },
                { 1, 2 }, { 1, -2 }, { -1, 2 }, { -1, -2 }
        };

        for (int[] move : knightMoves) {
            int newX = x + move[0];
            int newY = y + move[1];

            if (newX >= 0 && newX < 8 && newY >= 0 && newY < 8) {
                Position target = new Position(newX, newY);
                Piece targetPiece = board.getPieceAt(target);

                // Can move if square is empty or occupied by enemy
                if (targetPiece == null || targetPiece.getColor() != color) {
                    validMoves.add(target);
                }
            }
        }

        return validMoves;
    }
}
