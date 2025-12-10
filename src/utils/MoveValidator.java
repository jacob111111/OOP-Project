package utils;

import java.util.Map;
import java.util.Set;

import board.Board;
import piece.*;
import player.Player;

/**
 * Centralized move validation for chess.
 * Handles reachability, ownership, and check safety validation.
 */
public class MoveValidator {
    private Board board;
    private AttackMap attackMap;

    // Cached reference to board's position index for move simulation
    private Map<Position, Piece> positionIndex;

    /**
     * Creates a MoveValidator.
     * 
     * @param board     chess board
     * @param attackMap attack map for king safety checks
     */
    public MoveValidator(Board board, AttackMap attackMap) {
        this.board = board;
        this.attackMap = attackMap;
        this.positionIndex = board.getPositionIndex();
    }

    // ============================================================================
    // PUBLIC API - Main Validation Methods
    // ============================================================================

    /**
     * Validates if a move is fully legal (ownership, reachability, safety).
     * 
     * @param piece         piece attempting to move
     * @param fromPosition  current position
     * @param toPosition    target position
     * @param currentPlayer player attempting the move
     * @return true if move is legal
     */
    public boolean isMoveLegal(Piece piece, Position fromPosition, Position toPosition, Player currentPlayer) {
        System.out.println("\n=== MOVE VALIDATION START ===");
        System.out.println("Piece: " + piece.getClass().getSimpleName() + " (" + piece.getColor() + ")");
        System.out.println("From: " + fromPosition + " -> To: " + toPosition);
        System.out.println("Current Player: " + currentPlayer.getColor());

        // 1. Ownership validation
        if (!isPieceOwnedByPlayer(piece, currentPlayer)) {
            System.out.println("FAILED: Ownership check - piece belongs to " + piece.getColor() + ", current player is "
                    + currentPlayer.getColor());
            System.out.println("=== MOVE VALIDATION END (FAILED) ===\n");
            return false;
        }
        System.out.println("PASSED: Ownership check");

        // 2. Reachability validation
        if (!isDestinationReachable(piece, toPosition)) {
            Set<Position> validMoves = getValidMovesForPiece(piece);
            System.out.println("FAILED: Reachability check");
            System.out.println("  Valid moves for this piece: " + validMoves);
            System.out.println("  Target position: " + toPosition);
            System.out.println("=== MOVE VALIDATION END (FAILED) ===\n");
            return false;
        }
        System.out.println("PASSED: Reachability check");

        // 3. King safety validation
        if (!isMoveKingSafe(piece, fromPosition, toPosition)) {
            System.out.println("FAILED: King safety check - this move would leave your king in check");
            System.out.println("=== MOVE VALIDATION END (FAILED) ===\n");
            return false;
        }
        System.out.println("PASSED: King safety check");
        System.out.println("=== MOVE VALIDATION END (SUCCESS) ===\n");

        return true;
    }

    /**
     * Checks if a piece is owned by the specified player.
     * 
     * @param piece  the piece to check
     * @param player the player to validate ownership for
     * @return true if player owns the piece, false otherwise
     */
    public boolean isPieceOwnedByPlayer(Piece piece, Player player) {
        return piece.getColor() == player.getColor();
    }

    /**
     * Checks if piece can physically reach target square.
     * 
     * @param piece  piece attempting to move
     * @param target target position
     * @return true if piece can reach target
     */
    public boolean isDestinationReachable(Piece piece, Position target) {
        Set<Position> validMoves = getValidMovesForPiece(piece);
        return validMoves.contains(target);
    }

    /**
     * Validates move doesn't leave king in check.
     * Simulates move, checks king safety, then rolls back.
     * 
     * @param piece        piece to move
     * @param fromPosition current position
     * @param toPosition   target position
     * @return true if king remains safe
     */
    public boolean isMoveKingSafe(Piece piece, Position fromPosition, Position toPosition) {
        // Check if this move captures an enemy piece
        Piece capturedPiece = board.getPieceAt(toPosition);
        boolean isCapture = capturedPiece != null && capturedPiece.getColor() != piece.getColor();

        // Temporarily remove captured piece from its player if this is a capture
        Player capturedPieceOwner = null;
        if (isCapture) {
            capturedPieceOwner = board.getPlayer(capturedPiece.getColor());
            capturedPieceOwner.getCurrentPieces().remove(capturedPiece);
        }

        // Simulate the move: update both piece position AND board's position index
        positionIndex.remove(fromPosition);
        positionIndex.put(toPosition, piece);
        piece.setPosition(toPosition);

        // Regenerate attack map with the new board state
        attackMap.updateAfterMove(piece, fromPosition, toPosition);

        // Check if king is safe after the move
        Color pieceColor = piece.getColor();
        Player player = board.getPlayer(pieceColor);
        Position kingPos = player.getKingPosition();
        Color opponentColor = (pieceColor == Color.WHITE) ? Color.BLACK : Color.WHITE;
        boolean kingIsSafe = !attackMap.isSquareAttackedBy(kingPos, opponentColor);

        // Rollback the simulation: restore piece position and board state
        positionIndex.remove(toPosition);
        positionIndex.put(fromPosition, piece);
        piece.setPosition(fromPosition);

        // CRITICAL: If this was a capture simulation, restore the captured piece to the
        // board
        if (isCapture) {
            positionIndex.put(toPosition, capturedPiece);
        }

        if (!kingIsSafe) {
            // Move leaves king in check - restore captured piece to player list
            if (isCapture) {
                capturedPieceOwner.getCurrentPieces().add(capturedPiece);
            }

            // Regenerate attack map to restore original state
            attackMap.updateAfterMove(piece, toPosition, fromPosition);
        } else {
            // Move is safe - restore captured piece to player list temporarily
            // (will be permanently removed during actual move execution)
            if (isCapture) {
                capturedPieceOwner.getCurrentPieces().add(capturedPiece);
            }

            // Regenerate attack map to restore original state
            attackMap.updateAfterMove(piece, toPosition, fromPosition);
        }

        return kingIsSafe;
    }

    // ============================================================================
    // PIECE-SPECIFIC MOVEMENT VALIDATION
    // ============================================================================

    /**
     * Gets all squares a piece can move to (without check validation).
     * 
     * @param piece piece to get moves for
     * @return set of valid positions
     */
    public Set<Position> getValidMovesForPiece(Piece piece) {
        return piece.getLegalMoves(board);
    }
}
