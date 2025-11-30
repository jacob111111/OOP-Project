package utils;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import board.Board;
import piece.*;
import player.Player;

/**
 * Efficiently calculates and caches which squares are under attack by each
 * color.
 * 
 * This class pre-computes all attacked squares once per board state, then
 * provides
 * O(1) lookup for attack queries. It focuses solely on tracking attacks - move
 * validation logic has been moved to MoveValidator for better separation of
 * concerns.
 * 
 * Responsibilities:
 * - Track which squares are attacked by each color
 * - Identify which pieces are attacking specific squares
 * - Provide fast check detection
 * - Regenerate attack data after moves
 * 
 * Usage Pattern:
 * AttackMap attackMap = new AttackMap(board);
 * boolean isUnderAttack = attackMap.isSquareAttackedBy(position, Color.WHITE);
 * boolean kingInCheck = attackMap.isKingInCheck(Color.BLACK);
 * 
 * @see MoveValidator for move validation logic
 */
public class AttackMap {
    private Board board;
    private Set<Position> whiteAttacks = new HashSet<>();
    private Set<Position> blackAttacks = new HashSet<>();

    // Track which pieces attack which squares for O(1) checker identification
    private Map<Position, Set<Piece>> whiteAttackers = new HashMap<>();
    private Map<Position, Set<Piece>> blackAttackers = new HashMap<>();

    private boolean isValid = false;

    public AttackMap(Board board) {
        this.board = board;
        generateAttackMaps();
    }

    /**
     * Pre-computes all squares attacked by both colors.
     * 
     * This method performs the expensive calculation of determining which squares
     * each piece can attack, storing the results in hash sets for fast lookup.
     * The calculation is performed once per board state and cached until the
     * board changes.
     */
    private void generateAttackMaps() {
        Player whitePlayer = board.getPlayer(Color.WHITE);
        Player blackPlayer = board.getPlayer(Color.BLACK);

        whiteAttacks.clear();
        blackAttacks.clear();
        whiteAttackers.clear();
        blackAttackers.clear();

        // Generate white attacks
        for (Piece piece : whitePlayer.getCurrentPieces()) {
            Set<Position> attacks = getAttackSquares(piece);
            whiteAttacks.addAll(attacks);

            // Track which piece attacks which squares
            for (Position pos : attacks) {
                whiteAttackers.computeIfAbsent(pos, k -> new HashSet<>()).add(piece);
            }
        }

        // Generate black attacks
        for (Piece piece : blackPlayer.getCurrentPieces()) {
            Set<Position> attacks = getAttackSquares(piece);
            blackAttacks.addAll(attacks);

            // Track which piece attacks which squares
            for (Position pos : attacks) {
                blackAttackers.computeIfAbsent(pos, k -> new HashSet<>()).add(piece);
            }
        }

        isValid = true;
    }

    /**
     * Regenerates the entire attack map after a move.
     * 
     * Instead of complex incremental updates, this simply recalculates all attacks
     * from scratch. With a maximum of 32 pieces, this is actually faster and much
     * simpler than tracking which linear pieces were blocked/unblocked.
     * 
     * @param pieceThatMoved the piece that was moved (unused, kept for API
     *                       compatibility)
     * @param oldPosition    where the piece was before the move (unused, kept for
     *                       API compatibility)
     * @param newPosition    where the piece is after the move (unused, kept for API
     *                       compatibility)
     */
    public void updateAfterMove(Piece pieceThatMoved, Position oldPosition, Position newPosition) {
        // Simple regeneration - fast enough with max 32 pieces
        generateAttackMaps();
    }

    /**
     * Gets all squares attacked by a specific piece for check/threat detection.
     * Uses the piece's own getPossibleMoves() method which knows its movement
     * rules.
     * 
     * This method calculates attack squares differently from legal moves:
     * - For pawns, ONLY includes diagonal attack squares (for threat detection)
     * - For linear pieces, stops at first piece but includes that square
     * - For knights and kings, includes all reachable squares
     * 
     * @param piece the piece whose attack squares should be calculated
     * @return a Set of Position objects representing squares under attack by this
     *         piece
     */
    private Set<Position> getAttackSquares(Piece piece) {
        // For most pieces, attacks = possible moves
        Set<Position> attacks = piece.getPossibleMoves(board);

        // Special case: Pawns attack diagonally even if no piece is there
        if (piece instanceof Pawn) {
            Position pos = piece.getPosition();
            int x = pos.getX();
            int y = pos.getY();
            int direction = (piece.getColor() == Color.WHITE) ? 1 : -1;
            int newY = y + direction;

            if (newY >= 0 && newY < 8) {
                // Add diagonal attack squares even if empty
                if (x - 1 >= 0) {
                    attacks.add(new Position(x - 1, newY));
                }
                if (x + 1 < 8) {
                    attacks.add(new Position(x + 1, newY));
                }
            }
        }

        return attacks;
    }

    /**
     * This is the primary interface for attack queries.
     * 
     * @param pos   the position to check for attacks
     * @param color the color whose attacks to check for (WHITE or BLACK)
     * @return True if the specified position is under attack by the specified color
     * @throws NullPointerException if pos or color is null
     * @implNote If the attack maps are invalid, this method will automatically
     *           regenerate them before performing the lookup
     */
    public boolean isSquareAttackedBy(Position pos, Color color) {
        if (!isValid)
            generateAttackMaps();
        return (color == Color.WHITE) ? whiteAttacks.contains(pos) : blackAttacks.contains(pos);
    }

    /**
     * Gets all pieces of the specified color that are attacking the given position.
     * Perfect for finding which pieces are giving check!
     * 
     * @param pos            the position being attacked
     * @param attackingColor the color of pieces doing the attacking
     * @return Set of pieces attacking that position (empty set if none)
     */
    public Set<Piece> getPiecesAttacking(Position pos, Color attackingColor) {
        if (!isValid)
            generateAttackMaps();

        Map<Position, Set<Piece>> attackerMap = (attackingColor == Color.WHITE) ? whiteAttackers : blackAttackers;
        return attackerMap.getOrDefault(pos, new HashSet<>());
    }

    /**
     * Fast check if the king of specified color is in check.
     * 
     * This convenience method combines king position lookup with attack detection
     * to provide a simple interface for check detection.
     * 
     * @param kingColor the color of the king to check (WHITE or BLACK)
     * @return True if the king of the specified color is currently in check
     * @throws IllegalStateException if no king is found for the specified color
     * @see #isSquareAttackedBy(Position, Color)
     */
    public boolean isKingInCheck(Color kingColor) {
        Player player = board.getPlayer(kingColor);
        Position kingPos = player.getKingPosition();
        Color opponentColor = (kingColor == Color.WHITE) ? Color.BLACK : Color.WHITE;
        return isSquareAttackedBy(kingPos, opponentColor);
    }
}