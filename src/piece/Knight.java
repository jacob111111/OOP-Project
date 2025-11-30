package piece;

import java.util.HashSet;
import java.util.Set;

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
