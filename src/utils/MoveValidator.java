package utils;

import java.util.Map;
import java.util.Set;

import board.Board;
import piece.*;
import player.Player;

/**
 * Centralized move validation system for chess.
 * 
 * This class consolidates all move validation logic in one place, separating
 * concerns from the AttackMap (which now only tracks attacks) and Board (which
 * manages state). It handles:
 * 
 * - Basic move validation (can piece reach target?)
 * - Check safety validation (does move expose king?)
 * - Piece-specific movement rules
 * - Turn/ownership validation
 * 
 * Usage:
 * MoveValidator validator = new MoveValidator(board, attackMap);
 * if (validator.isMoveLegal(piece, from, to, currentPlayer)) {
 * // Execute move
 * }
 */
public class MoveValidator {
    private Board board;
    private AttackMap attackMap;
    
    // Cached reference to board's position index for move simulation
    private Map<Position, Piece> positionIndex;

    /**
     * Creates a new MoveValidator with references to board and attack map.
     * 
     * @param board     the chess board to validate moves on
     * @param attackMap the attack map for checking king safety
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
     * Validates if a move is completely legal.
     * 
     * Performs comprehensive validation:
     * 1. Ownership: Does the current player own this piece?
     * 2. Reachability: Can the piece reach the target square?
     * 3. Safety: Does the move leave the king in check?
     * 
     * @param piece         the piece attempting to move
     * @param fromPosition  the piece's current position
     * @param toPosition    the target position
     * @param currentPlayer the player attempting the move
     * @return true if move is fully legal, false otherwise
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
     * Checks if a piece can physically reach the target square.
     * 
     * This validates piece-specific movement rules without considering
     * whether the move would leave the king in check.
     * 
     * @param piece  the piece attempting to move
     * @param target the target position
     * @return true if piece can reach target, false otherwise
     */
    public boolean isDestinationReachable(Piece piece, Position target) {
        Set<Position> validMoves = getValidMovesForPiece(piece);
        return validMoves.contains(target);
    }

    /**
     * Validates that a move doesn't leave the player's king in check.
     * 
     * Simulates the move, regenerates the attack map, checks if king is safe,
     * then rolls back if unsafe.
     * 
     * @param piece        the piece to move
     * @param fromPosition the piece's current position
     * @param toPosition   the target position
     * @return true if king remains safe after move, false if exposed to check
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
     * Gets all squares a piece can legally move to (without check validation).
     * 
     * Delegates to the piece's getLegalMoves() method, which handles
     * piece-specific movement rules including:
     * - Pawns: Forward movement and diagonal captures
     * - Knights: L-shaped jumps
     * - Kings: One square in any direction + castling
     * - Linear pieces: Sliding along lines until blocked
     * 
     * @param piece the piece to get moves for
     * @return Set of positions the piece can move to
     */
    public Set<Position> getValidMovesForPiece(Piece piece) {
        return piece.getLegalMoves(board);
    }
}
