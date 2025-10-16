package piece;

import utils.Color;
import utils.Position;

public class Rook extends LinearPiece {
    private static final int[][] ROOK_DIRECTIONS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
    
    public Rook(Color color, Position position) {
        super(color, position);
        this.displaySymbol.append("R");
    }

    @Override
    public void findPossibleMoves() {
        addMovesInDirections(ROOK_DIRECTIONS);
    }
}