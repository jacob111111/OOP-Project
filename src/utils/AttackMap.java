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
 * color by pre-computing all attacked squares once per board state, then
 * providing O(1) lookup for attack queries.
 * 
 * Usage Pattern:
 * AttackMap attackMap = new AttackMap(board);
 * boolean isUnderAttack = attackMap.isSquareAttackedBy(position, Color.WHITE);
 * boolean kingInCheck = attackMap.isKingInCheck(Color.BLACK);
 * 
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
     * Updates attack maps incrementally after a piece move.
     * Much faster than full regeneration for single piece moves.
     * 
     * @param pieceThatMoved the piece that was moved
     * @param oldPosition    where the piece was before the move
     * @param newPosition    where the piece is after the move
     * @Note This doesn't handle pieces that were "unblocked" or "newly blocked"
     *       For full accuracy, we'd need to recalculate all linear pieces that
     *       might be affected and update them as well.
     */
    public void updateAfterMove(Piece pieceThatMoved, Position oldPosition, Position newPosition) {
        if (!isValid) {
            generateAttackMaps(); // If invalid, do full regeneration
            return;
        }

        // Temporarily set piece to old position to calculate old attacks
        Position currentPos = pieceThatMoved.getPosition();
        pieceThatMoved.setPosition(oldPosition);
        Set<Position> oldAttacks = getAttackSquares(pieceThatMoved);

        // Set piece to new position and calculate new attacks
        pieceThatMoved.setPosition(newPosition);
        Set<Position> newAttacks = getAttackSquares(pieceThatMoved);

        // Restore the correct position (should already be newPosition, but being safe)
        pieceThatMoved.setPosition(currentPos);

        // Update the appropriate attack set
        Color pieceColor = pieceThatMoved.getColor();
        Set<Position> targetAttackSet = (pieceColor == Color.WHITE) ? whiteAttacks : blackAttacks;

        // Remove old attacks and add new attacks
        targetAttackSet.removeAll(oldAttacks);
        targetAttackSet.addAll(newAttacks);
    }

    /**
     * Gets all squares attacked by a specific piece.
     * 
     * This method calculates attack squares differently from legal moves:
     * - Includes squares occupied by own pieces (for king safety analysis)
     * - For pawns, only includes diagonal attack squares (not forward movement)
     * - For linear pieces (rooks, etc), stops at the first piece encountered but
     * includes that square
     * - For knights and kings, includes all reachable squares regardless of
     * occupation
     * 
     * @param piece the piece whose attack squares should be calculated
     * @return a Set of Position objects representing squares under attack by this
     *         piece
     * @throws NullPointerException if piece is null
     * @implNote This method does not validate that the piece is actually on the
     *           board - (we have other checks for this, shouldnt be an issue)
     */
    private Set<Position> getAttackSquares(Piece piece) {
        Set<Position> attacks = new HashSet<>();
        Position pos = piece.getPosition();
        int x = pos.getX();
        int y = pos.getY();

        if (piece instanceof Pawn) {
            // Pawns only attack diagonally
            int direction = (piece.getColor() == Color.WHITE) ? 1 : -1;
            int newY = y + direction;

            if (newY >= 0 && newY < 8) {
                if (x - 1 >= 0)
                    attacks.add(new Position(x - 1, newY));
                if (x + 1 < 8)
                    attacks.add(new Position(x + 1, newY));
            }
        } else if (piece instanceof Knight) {
            // Knight L-shaped attacks
            int[][] knightMoves = { { 2, 1 }, { 2, -1 }, { -2, 1 }, { -2, -1 }, { 1, 2 }, { 1, -2 }, { -1, 2 },
                    { -1, -2 } };
            for (int[] move : knightMoves) {
                int newX = x + move[0];
                int newY = y + move[1];
                if (newX >= 0 && newX < 8 && newY >= 0 && newY < 8) {
                    attacks.add(new Position(newX, newY));
                }
            }
        } else if (piece instanceof King) {
            // King attacks one square in all directions
            int[][] kingMoves = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 }, { 1, 1 }, { 1, -1 }, { -1, 1 },
                    { -1, -1 } };
            for (int[] move : kingMoves) {
                int newX = x + move[0];
                int newY = y + move[1];
                if (newX >= 0 && newX < 8 && newY >= 0 && newY < 8) {
                    attacks.add(new Position(newX, newY));
                }
            }
        } else {
            // Linear pieces (Queen, Rook, Bishop)
            int[][] directions = getDirections(piece);
            for (int[] dir : directions) {
                for (int i = 1; i < 8; i++) {
                    int newX = x + (dir[0] * i);
                    int newY = y + (dir[1] * i);

                    if (newX < 0 || newX >= 8 || newY < 0 || newY >= 8)
                        break;

                    attacks.add(new Position(newX, newY));

                    // Stop if we hit any piece (but include the attack on that square)
                    if (board.getPieceAt(new Position(newX, newY)) != null)
                        break;
                }
            }
        }

        return attacks;
    }

    /**
     * Gets the movement direction vectors for linear pieces.
     * 
     * @param piece the piece whose directions are needed (Queen, Rook, or Bishop)
     * @return array of direction vectors as [x, y] pairs
     */
    private int[][] getDirections(Piece piece) {
        if (piece instanceof Rook) {
            return new int[][] { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
        } else if (piece instanceof Bishop) {
            return new int[][] { { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 } };
        } else if (piece instanceof Queen) {
            return new int[][] { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 }, { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 } };
        }
        return new int[0][0];
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