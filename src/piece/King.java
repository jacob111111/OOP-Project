package piece;

import java.util.HashSet;
import java.util.Set;

import utils.Color;
import utils.Position;

/**
 * King piece - can move one square in any direction.
 * Most important piece; losing it ends the game.
 */
public class King extends Piece {
    /** Tracks whether this king has moved (used for castling rules) */
    private boolean hasMoved = false;

    /**
     * Creates a King piece.
     * 
     * @param color    king's color
     * @param position initial position
     */
    public King(Color color, Position position) {
        super(color, position);
        this.displaySymbol.append("K");
    }

    /**
     * Sets whether this king has moved (affects castling eligibility).
     * 
     * @param hasMoved true if the king has moved, false otherwise
     */
    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    /**
     * Gets whether this king has moved from its starting position.
     * 
     * @return true if the king has moved, false if it's still in starting position
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

        // All 8 adjacent squares - basic king movement only
        int[][] kingMoves = {
                { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 },
                { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 }
        };

        for (int[] move : kingMoves) {
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

        // NOTE: Castling is NOT included here to avoid circular dependency
        // during AttackMap generation. Use getLegalMoves() for castling.
        return validMoves;
    }

    @Override
    public Set<Position> getLegalMoves(board.Board board) {
        // Start with basic king moves
        Set<Position> legalMoves = new HashSet<>(getPossibleMoves(board));
        int x = position.getX();
        int y = position.getY();

        // Add castling moves (safe to access AttackMap here)
        if (!hasMoved) {
            // King-side castling (O-O)
            if (canCastle(board, true)) {
                legalMoves.add(new Position(x + 2, y));
            }
            // Queen-side castling (O-O-O)
            if (canCastle(board, false)) {
                legalMoves.add(new Position(x - 2, y));
            }
        }

        return legalMoves;
    }

    /**
     * Checks if castling is possible.
     * Validates all castling requirements (unmoved pieces, clear path, no check).
     * 
     * @param board    chess board
     * @param kingside true for kingside castling
     * @return true if castling is legal
     */
    private boolean canCastle(board.Board board, boolean kingside) {
        int x = position.getX();
        int y = position.getY();

        // Determine rook position and squares to check
        int rookX = kingside ? 7 : 0;
        Position rookPos = new Position(rookX, y);
        Piece rookPiece = board.getPieceAt(rookPos);

        // Check if rook exists and hasn't moved
        if (!(rookPiece instanceof Rook) || ((Rook) rookPiece).getHasMoved()) {
            return false;
        }

        // Check if rook is same color
        if (rookPiece.getColor() != color) {
            return false;
        }

        // Check if squares between king and rook are empty
        int start = Math.min(x, rookX) + 1;
        int end = Math.max(x, rookX);
        for (int i = start; i < end; i++) {
            if (board.getPieceAt(new Position(i, y)) != null) {
                return false; // Path blocked
            }
        }

        // Check if king is currently in check
        utils.AttackMap attackMap = board.getAttackMap();
        Color opponentColor = (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
        if (attackMap.isSquareAttackedBy(position, opponentColor)) {
            return false; // Can't castle out of check
        }

        // Check if king passes through or ends in check
        // King moves two squares toward rook
        int direction = kingside ? 1 : -1;
        Position passThroughSquare = new Position(x + direction, y);
        Position destinationSquare = new Position(x + (direction * 2), y);

        if (attackMap.isSquareAttackedBy(passThroughSquare, opponentColor)) {
            return false; // Can't pass through check
        }

        if (attackMap.isSquareAttackedBy(destinationSquare, opponentColor)) {
            return false; // Can't end in check
        }

        return true;
    }
}
