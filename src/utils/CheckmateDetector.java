package utils;

import java.util.ArrayList;
import java.util.List;

import board.Board;
import piece.*;
import player.Player;

/**
 * checkmate detection using pre-computed attack maps and optimized algorithms.
 * 
 * Checkmate Detection Algorithm:
 * 1. Quick check: Is the king actually in check? (Exit early if not)
 * 2. King escape analysis: Can the king move to any safe square?
 * 3. Blocking analysis: Can any piece block the attack?
 * 4. Capture analysis: Can any piece capture the attacking piece?
 */
public class CheckmateDetector {
    private Board board;
    private AttackMap attackMap;

    public CheckmateDetector(Board board, AttackMap attackMap) {
        this.board = board;
        this.attackMap = attackMap;
    }

    /**
     * Determines if the specified color is in checkmate.
     * 
     * This method implements a fast checkmate detection algorithm that evaluates
     * the three conditions for checkmate in order of computational efficiency:
     * 1. Check validation: King must be in check (quick exit if not)
     * 2. King escape: King has no legal moves to safety
     * 3. Resolution: No piece can block or capture the attacker
     * 
     * @param kingColor the color of the king to check for checkmate (WHITE or
     *                  BLACK)
     * @return true if the specified color is in checkmate, false otherwise
     * @throws IllegalArgumentException if kingColor is null
     * @throws IllegalStateException    if no king is found for the specified color
     * 
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
     * Determines if the king can escape to any safe square.
     * 
     * This method checks all 8 adjacent squares around the king to see if any
     * provide a safe escape route. It's much more efficient than generating
     * full move lists because it only examines the relevant squares.
     * 
     * @param kingPos   the current position of the king
     * @param kingColor the color of the king being analyzed
     * @return true if the king can move to at least one safe square, false if
     *         trapped
     * 
     * @implNote This method simulates king movement to account for pieces that
     *           would no longer be attacking after the king moves (e.g., discovered
     *           attacks)
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
     * Checks if a piece can attack a specific square, considering king movement
     * simulation.
     * 
     * This method handles different piece types appropriately:
     * - Knights and Kings: Use standard move patterns (no blocking)
     * - Pawns: Only diagonal attack patterns
     * - Linear pieces: Check line-of-sight with king position simulation
     * 
     * @param piece          the piece whose attack capability is being checked
     * @param target         the target square to check for attack
     * @param currentKingPos the current king position (ignored during line-of-sight
     *                       calculation)
     * @return true if the piece can attack the target square, false otherwise
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
     * Determines if a pawn can attack a specific target square.
     * 
     * Pawns attack diagonally forward, one square at a time. This method
     * validates that the target is exactly one diagonal square forward
     * from the pawn's current position.
     * 
     * @param pawn   the pawn piece to check
     * @param target the target square
     * @return true if the pawn can attack the target square
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
     * Determines if a linear piece (Queen, Rook, or Bishop) can attack a target
     * square.
     * 
     * This method first validates that the target is on a valid attack line for
     * the piece type, then checks if the path is clear.
     * 
     * @param piece          the linear piece (Queen, Rook, or Bishop)
     * @param target         the target square
     * @param currentKingPos king position to ignore during path calculation
     * @return true if the piece can attack the target
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
     * Checks if the path between two positions is clear of pieces.
     * 
     * This method walks along the line from the starting position to the target,
     * checking each intermediate square for pieces. It can ignore a specific
     * position (typically the king's current position during simulation).
     * 
     * @param from         the starting position (exclusive)
     * @param to           the ending position (exclusive)
     * @param ignoreKingAt position to ignore during path checking (for king move
     *                     simulation)
     * @return true if the path is clear, false if blocked
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
     * Overloaded version of isPathClear that also collects the positions it
     * traverses.
     * 
     * This method does the same path checking as the original but additionally
     * adds each position it checks to the provided list. Useful for finding
     * blocking squares between a checking piece and the king.
     * 
     * @param from          the starting position (exclusive)
     * @param to            the ending position (exclusive)
     * @param ignoreKingAt  position to ignore during path checking
     * @param pathPositions list to collect the positions traversed (will be
     *                      modified)
     * @return true if the path is clear, false if blocked
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
     * Iterates through all defending pieces to see if any can attack the checker's
     * position. Uses the same attack calculation logic as king escape simulation.
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

            // Check if this piece can attack the checker's position
            if (canPieceAttackSquare(piece, checkerPos, kingPos)) {
                return true; // Found a piece that can capture the checker
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
     * Uses the AttackMap to efficiently find pieces that can move to blocking
     * squares.
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

        // Check if any defending piece can move to any blocking square
        Color defendingColor = defendingPlayer.getColor();
        for (Position blockSquare : blockingSquares) {
            java.util.Set<Piece> piecesAttackingThisSquare = attackMap.getPiecesAttacking(blockSquare, defendingColor);

            // Filter out the king - it can't block (already tried to escape)
            for (Piece piece : piecesAttackingThisSquare) {
                if (!(piece instanceof King)) {
                    return true; // Found a piece that can block!
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