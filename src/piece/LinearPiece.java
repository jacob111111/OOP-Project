package piece;

import utils.Color;
import utils.Position;

public abstract class LinearPiece extends Piece {
    public LinearPiece(Color color, Position position) {
        super(color, position);
    }

    protected void addMovesInDirections(int[][] directions) {
        possibleMoves.clear();
        int x = position.getX();
        int y = position.getY();
        
        for (int[] dir : directions) {
            for (int i = 1; i < 8; i++) {
                int newX = x + (dir[0] * i);
                int newY = y + (dir[1] * i);
                
                if (newX >= 0 && newX < 8 && newY >= 0 && newY < 8) {
                    possibleMoves.add(new Position(newX, newY));
                } else {
                    break;
                }
            }
        }
    }
}