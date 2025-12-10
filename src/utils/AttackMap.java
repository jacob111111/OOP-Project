package utils;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import board.Board;
import piece.*;
import player.Player;

/**
 * Efficiently calculates and caches attacked squares for O(1) lookups.
 * Focuses on tracking attacks; move validation is handled by MoveValidator.
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
     * Pre-computes all attacked squares for both colors and caches results.
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
     * Regenerates attack map after a move.
     * 
     * @param pieceThatMoved piece that moved (unused)
     * @param oldPosition    old position (unused)
     * @param newPosition    new position (unused)
     */
    public void updateAfterMove(Piece pieceThatMoved, Position oldPosition, Position newPosition) {
        // Simple regeneration - fast enough with max 32 pieces
        generateAttackMaps();
    }

    /**
     * Gets all squares attacked by a piece.
     * Special handling for pawns (diagonal attacks only).
     * 
     * @param piece piece whose attacks to calculate
     * @return set of attacked positions
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
     * Checks if position is under attack by specified color.
     * 
     * @param pos   position to check
     * @param color attacking color
     * @return true if position is attacked
     */
    public boolean isSquareAttackedBy(Position pos, Color color) {
        if (!isValid)
            generateAttackMaps();
        return (color == Color.WHITE) ? whiteAttacks.contains(pos) : blackAttacks.contains(pos);
    }

    /**
     * Gets all pieces of specified color attacking a position.
     * 
     * @param pos            position being attacked
     * @param attackingColor color of attacking pieces
     * @return set of pieces attacking position
     */
    public Set<Piece> getPiecesAttacking(Position pos, Color attackingColor) {
        if (!isValid)
            generateAttackMaps();

        Map<Position, Set<Piece>> attackerMap = (attackingColor == Color.WHITE) ? whiteAttackers : blackAttackers;
        return attackerMap.getOrDefault(pos, new HashSet<>());
    }

    /**
     * Fast check if king is in check.
     * 
     * @param kingColor king's color
     * @return true if king is in check
     */
    public boolean isKingInCheck(Color kingColor) {
        Player player = board.getPlayer(kingColor);
        Position kingPos = player.getKingPosition();
        Color opponentColor = (kingColor == Color.WHITE) ? Color.BLACK : Color.WHITE;
        return isSquareAttackedBy(kingPos, opponentColor);
    }
}