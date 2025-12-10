package utils;

import java.util.ArrayList;
import java.util.List;

import board.Board;
import piece.*;
import player.Player;

/**
 * Detects checkmate using pre-computed attack maps.
 * Checks: (1) king in check, (2) king can't escape, (3) can't block/capture.
 */
public class CheckmateDetector {
    private Board board;
    private AttackMap attackMap;
    private MoveValidator moveValidator;

    public CheckmateDetector(Board board, AttackMap attackMap) {
        this.board = board;
        this.attackMap = attackMap;
        // MoveValidator will be set after board is fully initialized
    }

    /**
     * Sets the move validator (called after board init to avoid circular
     * dependency).
     * 
     * @param validator MoveValidator instance
     */
    public void setMoveValidator(MoveValidator validator) {
        this.moveValidator = validator;
    }

    /**
     * Determines if specified color is in checkmate.
     * Checks if king is in check, can't escape, and check can't be resolved.
     * 
     * @param kingColor color to check for checkmate
     * @return true if in checkmate
     */
    public boolean isCheckmate(Color kingColor) {
        // First, is the king even in check?
        if (!attackMap.isKingInCheck(kingColor)) {
            return false; // Not in check, definitely not checkmate
        }

        Player player = board.getPlayer(kingColor);
        Position kingPos = player.getKingPosition();

        // Can the king move to safety?
        if (canKingEscape(kingPos, kingColor)) {
            return false; // King can escape, not checkmate
        }

        // Can any piece block the check or capture the attacker?
        if (canBlockOrCaptureChecker(kingColor)) {
            return false; // Check can be resolved, not checkmate
        }

        return true; // No escape, no block, no capture = checkmate
    }

    /**
     * Determines if the specified color's king is currently in check.
     * 
     * @param kingColor the color of the king to check (WHITE or BLACK)
     * @return true if the king is in check, false otherwise
     */
    public boolean isKingInCheck(Color kingColor) {
        return attackMap.isKingInCheck(kingColor);
    }

    /**
     * Checks if king can escape to any safe adjacent square.
     * 
     * @param kingPos   king's current position
     * @param kingColor king's color
     * @return true if king can escape
     */
    private boolean canKingEscape(Position kingPos, Color kingColor) {
        int x = kingPos.getX();
        int y = kingPos.getY();
        Color opponentColor = (kingColor == Color.WHITE) ? Color.BLACK : Color.WHITE;
        Player opponent = board.getPlayer(opponentColor);

        // Check all 8 adjacent squares
        int[][] kingMoves = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 }, { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 } };

        for (int[] move : kingMoves) {
            int newX = x + move[0];
            int newY = y + move[1];

            // Must be within board bounds
            if (newX < 0 || newX >= 8 || newY < 0 || newY >= 8)
                continue;

            Position newPos = new Position(newX, newY);
            Piece pieceAtSquare = board.getPieceAt(newPos);

            // Can't move to square occupied by own piece
            if (pieceAtSquare != null && pieceAtSquare.getColor() == kingColor)
                continue;

            // Simulate the king move: check if any opponent piece can attack the new
            // position
            boolean isSquareSafe = true;
            for (Piece piece : opponent.getCurrentPieces()) {
                if (canPieceAttackSquare(piece, newPos, kingPos)) {
                    isSquareSafe = false;
                    break;
                }
            }

            if (isSquareSafe) {
                return true; // Found a safe escape square
            }
        }

        return false; // No safe squares found
    }

    /**
     * Checks if piece can attack target square, handling king position simulation.
     * 
     * @param piece          piece to check
     * @param target         target square
     * @param currentKingPos current king position (for simulation)
     * @return true if piece can attack target
     */
    private boolean canPieceAttackSquare(Piece piece, Position target, Position currentKingPos) {
        if (piece instanceof Knight || piece instanceof King) {
            // Knights and kings don't care about blocking pieces
            // Use AttackMap to check if this piece can attack the target
            return attackMap.getPiecesAttacking(target, piece.getColor()).contains(piece);
        } else if (piece instanceof Pawn) {
            return canPawnAttack(piece, target);
        } else {
            // Linear pieces (Queen, Rook, Bishop) - check line of sight
            return canLinearPieceAttack(piece, target, currentKingPos);
        }
    }

    /**
     * Checks if pawn can attack target (diagonal forward one square).
     * 
     * @param pawn   pawn piece
     * @param target target square
     * @return true if pawn can attack target
     */
    private boolean canPawnAttack(Piece pawn, Position target) {
        Position pawnPos = pawn.getPosition();
        int dx = target.getX() - pawnPos.getX();
        int dy = target.getY() - pawnPos.getY();

        // Pawns attack diagonally forward
        int direction = (pawn.getColor() == Color.WHITE) ? 1 : -1;
        return (Math.abs(dx) == 1) && (dy == direction);
    }

    /**
     * Checks if linear piece can attack target with clear path.
     * 
     * @param piece          linear piece (Queen, Rook, Bishop)
     * @param target         target square
     * @param currentKingPos king position to ignore
     * @return true if piece can attack target
     */
    private boolean canLinearPieceAttack(Piece piece, Position target, Position currentKingPos) {
        Position piecePos = piece.getPosition();

        // Check if target is on a valid line for this piece type
        if (!((LinearPiece) piece).isOnValidLineTo(target))
            return false;

        // Check if path is clear (ignoring the current king position)
        return isPathClear(piecePos, target, currentKingPos);
    }

    /**
     * Checks if path between positions is clear.
     * 
     * @param from         starting position
     * @param to           ending position
     * @param ignoreKingAt position to ignore (for king simulation)
     * @return true if path is clear
     */
    private boolean isPathClear(Position from, Position to, Position ignoreKingAt) {
        int dx = Integer.signum(to.getX() - from.getX());
        int dy = Integer.signum(to.getY() - from.getY());

        int x = from.getX() + dx;
        int y = from.getY() + dy;

        while (x != to.getX() || y != to.getY()) {
            Position checkPos = new Position(x, y);

            // Ignore the king's current position (since we're simulating its move)
            if (!checkPos.equals(ignoreKingAt)) {
                if (board.getPieceAt(checkPos) != null) {
                    return false; // Path blocked
                }
            }

            x += dx;
            y += dy;
        }

        return true; // Path is clear
    }

    /**
     * Checks path and collects traversed positions.
     * 
     * @param from          starting position
     * @param to            ending position
     * @param ignoreKingAt  position to ignore
     * @param pathPositions list to collect positions (modified)
     * @return true if path is clear
     */
    private boolean isPathClear(Position from, Position to, Position ignoreKingAt, List<Position> pathPositions) {
        int dx = Integer.signum(to.getX() - from.getX());
        int dy = Integer.signum(to.getY() - from.getY());

        int x = from.getX() + dx;
        int y = from.getY() + dy;

        while (x != to.getX() || y != to.getY()) {
            Position checkPos = new Position(x, y);

            // Add this position to the path (regardless of whether it's occupied)
            pathPositions.add(checkPos);

            // Ignore the king's current position (since we're simulating its move)
            if (!checkPos.equals(ignoreKingAt)) {
                if (board.getPieceAt(checkPos) != null) {
                    return false; // Path blocked
                }
            }

            x += dx;
            y += dy;
        }

        return true; // Path is clear
    }

    /**
     * Determines if any piece can block the check or capture the checking piece.
     * 
     * Note: This is a simplified implementation placeholder.
     * A complete implementation would need to:
     * - Identify the specific piece(s) giving check
     * - Calculate all squares that would block the attack line
     * - Check if any friendly piece can move to those blocking squares
     * - Check if any friendly piece can capture the checking piece
     * - Handle double check situations (only king moves allowed)
     * 
     * @param kingColor the color of the king in check
     * @return true if the check can be resolved by blocking or capturing, false
     *         otherwise
     * 
     * @implNote This method currently returns false as a placeholder. Implementing
     *           full blocking analysis requires significant additional complexity.
     * @todo Implement complete blocking and capture analysis
     */
    private boolean canBlockOrCaptureChecker(Color KingColor) {
        Player user1 = board.getPlayer(KingColor); // player who is possibly being checked
        Player user2 = board.getPlayer(KingColor == Color.WHITE ? Color.BLACK : Color.WHITE);
        Position user1KingPos = user1.getKingPosition();

        List<Piece> piecesGivingCheck = new ArrayList<>();
        for (Piece p : user2.getCurrentPieces()) {
            if (canPieceAttackSquare(p, user1KingPos, user1KingPos)) {
                piecesGivingCheck.add(p);
            }
        }

        // If no pieces are giving check, the king is not in check
        if (piecesGivingCheck.isEmpty()) {
            return true; // No check means no need to block/capture
        }

        // Special case: Double check means ONLY king can move
        if (piecesGivingCheck.size() > 1) {
            return false; // No blocking/capturing possible in double check
        }

        Piece checker = piecesGivingCheck.get(0);

        // Can any piece capture the checker?
        if (canAnyPieceCaptureTarget(checker.getPosition(), user1, user1KingPos)) {
            return true;
        }

        // Can any piece block the attack line?
        if (canAnyPieceBlockAttackLine(checker, user1, user1KingPos)) {
            return true;
        }

        // checkmate!
        return false;
    }

    /**
     * Checks if any of the defending player's pieces can capture the attacking
     * piece.
     * 
     * Iterates through all defending pieces to see if any can legally move to
     * capture the checker. Uses MoveValidator to ensure the capture doesn't
     * leave the king in check.
     * 
     * @param checkerPos      the position of the piece giving check
     * @param defendingPlayer the player whose pieces might capture the checker
     * @param kingPos         the king's position (for line-of-sight simulation)
     * @return true if any piece can capture the checker, false otherwise
     */
    private boolean canAnyPieceCaptureTarget(Position checkerPos, Player defendingPlayer, Position kingPos) {
        for (Piece piece : defendingPlayer.getCurrentPieces()) {
            if (piece instanceof King) {
                continue; // skip King as it's capture was already checked in canKingEscape
            }

            Position piecePos = piece.getPosition();

            // Check if this piece can reach the checker's position
            if (moveValidator != null && moveValidator.isDestinationReachable(piece, checkerPos)) {
                // Check if capturing would leave king safe
                if (moveValidator.isMoveKingSafe(piece, piecePos, checkerPos)) {
                    return true; // Found a piece that can safely capture the checker
                }
            }
        }
        return false; // No piece can capture the checker
    }

    /**
     * Checks if any piece can block the attack line between the checker and king.
     * 
     * This method handles several edge cases:
     * - Knights and Pawns cannot be blocked (their attacks are direct)
     * - Linear pieces (Rook, Bishop, Queen) can be blocked by moving a piece
     * into the attack line between the attacker and the king
     * 
     * Uses MoveValidator to verify that blocking moves don't leave king in check.
     * 
     * @param checker         the piece giving check
     * @param defendingPlayer the player whose pieces might block
     * @param kingPos         the king's position
     * @return true if any piece can block the attack line, false otherwise
     */
    private boolean canAnyPieceBlockAttackLine(Piece checker, Player defendingPlayer, Position kingPos) {
        // Knights and Pawns cannot be blocked - their attacks are direct/adjacent
        if (checker instanceof Knight || checker instanceof Pawn) {
            return false;
        }

        // For linear pieces, calculate the blocking squares (positions between checker
        // and king)
        List<Position> blockingSquares = new ArrayList<>();
        Position checkerPos = checker.getPosition();

        // Use the overloaded isPathClear to collect positions between checker and king
        isPathClear(checkerPos, kingPos, null, blockingSquares);

        // If there are no squares between checker and king, can't block (adjacent
        // attack)
        if (blockingSquares.isEmpty()) {
            return false;
        }

        // Check if any defending piece can legally move to any blocking square
        for (Piece piece : defendingPlayer.getCurrentPieces()) {
            if (piece instanceof King) {
                continue; // King can't block (already tried to escape)
            }

            Position piecePos = piece.getPosition();

            // Check each potential blocking square
            for (Position blockSquare : blockingSquares) {
                // Can this piece reach the blocking square?
                if (moveValidator != null && moveValidator.isDestinationReachable(piece, blockSquare)) {
                    // Would moving there keep the king safe?
                    if (moveValidator.isMoveKingSafe(piece, piecePos, blockSquare)) {
                        return true; // Found a piece that can block!
                    }
                }
            }
        }

        return false; // No piece can block the attack line
    }

    /**
     * Updates the internal attack map after a move is made.
     * 
     * This method should be called after any board state change to ensure
     * that subsequent checkmate analysis uses accurate attack information.
     * 
     * @param pieceThatMoved the piece that was moved
     * @param oldPosition    where the piece was before the move
     * @param newPosition    where the piece is after the move
     */
    public void updateAfterMove(Piece pieceThatMoved, Position oldPosition, Position newPosition) {
        attackMap.updateAfterMove(pieceThatMoved, oldPosition, newPosition);
    }
}