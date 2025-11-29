package utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
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
     * 
     * This method now properly handles pieces that were blocked or unblocked by the
     * move.
     * When a piece moves, it can affect the attack lines of linear pieces (Queens,
     * Rooks, Bishops)
     * that were pointing through the old or new position.
     * 
     * @param pieceThatMoved the piece that was moved
     * @param oldPosition    where the piece was before the move
     * @param newPosition    where the piece is after the move
     */
    public void updateAfterMove(Piece pieceThatMoved, Position oldPosition, Position newPosition) {
        if (!isValid) {
            generateAttackMaps(); // If invalid, do full regeneration
            return;
        }

        // Update the moved piece's attacks
        updatePieceAttacks(pieceThatMoved, oldPosition, newPosition);

        // Find and update all linear pieces that might be affected by this move
        // A linear piece is affected if the old or new position lies on one of its
        // attack lines
        Player whitePlayer = board.getPlayer(Color.WHITE);
        Player blackPlayer = board.getPlayer(Color.BLACK);

        // Check all pieces from both colors
        for (Piece piece : whitePlayer.getCurrentPieces()) {
            if (piece != pieceThatMoved && isLinearPiece(piece)) {
                if (isPositionOnAttackLine(piece, oldPosition) || isPositionOnAttackLine(piece, newPosition)) {
                    // This piece's attack line was affected, recalculate it
                    recalculatePieceAttacks(piece);
                }
            }
        }

        for (Piece piece : blackPlayer.getCurrentPieces()) {
            if (piece != pieceThatMoved && isLinearPiece(piece)) {
                if (isPositionOnAttackLine(piece, oldPosition) || isPositionOnAttackLine(piece, newPosition)) {
                    // This piece's attack line was affected, recalculate it
                    recalculatePieceAttacks(piece);
                }
            }
        }
    }

    /**
     * Updates the attack map for a specific piece that moved.
     * 
     * @param piece       the piece that moved
     * @param oldPosition the old position
     * @param newPosition the new position
     */
    private void updatePieceAttacks(Piece piece, Position oldPosition, Position newPosition) {
        // Temporarily set piece to old position to calculate old attacks
        Position currentPos = piece.getPosition();
        piece.setPosition(oldPosition);
        Set<Position> oldAttacks = getAttackSquares(piece);

        // Set piece to new position and calculate new attacks
        piece.setPosition(newPosition);
        Set<Position> newAttacks = getAttackSquares(piece);

        // Restore the correct position (should already be newPosition, but being safe)
        piece.setPosition(currentPos);

        // Update the appropriate attack set and attacker map
        Color pieceColor = piece.getColor();
        Set<Position> targetAttackSet = (pieceColor == Color.WHITE) ? whiteAttacks : blackAttacks;
        Map<Position, Set<Piece>> targetAttackerMap = (pieceColor == Color.WHITE) ? whiteAttackers : blackAttackers;

        // Remove old attacks
        for (Position pos : oldAttacks) {
            targetAttackSet.remove(pos);
            Set<Piece> attackers = targetAttackerMap.get(pos);
            if (attackers != null) {
                attackers.remove(piece);
                if (attackers.isEmpty()) {
                    targetAttackerMap.remove(pos);
                }
            }
        }

        // Add new attacks
        for (Position pos : newAttacks) {
            targetAttackSet.add(pos);
            targetAttackerMap.computeIfAbsent(pos, k -> new HashSet<>()).add(piece);
        }
    }

    /**
     * Recalculates the attacks for a piece whose attack line was affected by
     * another piece's move.
     * 
     * @param piece the piece whose attacks need recalculation
     */
    private void recalculatePieceAttacks(Piece piece) {
        Color pieceColor = piece.getColor();
        Set<Position> targetAttackSet = (pieceColor == Color.WHITE) ? whiteAttacks : blackAttacks;
        Map<Position, Set<Piece>> targetAttackerMap = (pieceColor == Color.WHITE) ? whiteAttackers : blackAttackers;

        // Remove old attacks for this piece
        Set<Position> oldAttacks = new HashSet<>();
        for (Position pos : targetAttackSet) {
            Set<Piece> attackers = targetAttackerMap.get(pos);
            if (attackers != null && attackers.contains(piece)) {
                oldAttacks.add(pos);
            }
        }

        for (Position pos : oldAttacks) {
            Set<Piece> attackers = targetAttackerMap.get(pos);
            if (attackers != null) {
                attackers.remove(piece);
                if (attackers.isEmpty()) {
                    targetAttackerMap.remove(pos);
                    targetAttackSet.remove(pos);
                }
            }
        }

        // Calculate new attacks with current board state
        Set<Position> newAttacks = getAttackSquares(piece);

        // Add new attacks
        for (Position pos : newAttacks) {
            targetAttackSet.add(pos);
            targetAttackerMap.computeIfAbsent(pos, k -> new HashSet<>()).add(piece);
        }
    }

    /**
     * Checks if a piece is a linear piece (Queen, Rook, or Bishop).
     * 
     * @param piece the piece to check
     * @return true if the piece is a Queen, Rook, or Bishop
     */
    private boolean isLinearPiece(Piece piece) {
        return piece instanceof Queen || piece instanceof Rook || piece instanceof Bishop;
    }

    /**
     * Checks if a position lies on any of the attack lines of a linear piece.
     * 
     * @param piece    the linear piece
     * @param position the position to check
     * @return true if the position is on one of the piece's attack lines
     */
    private boolean isPositionOnAttackLine(Piece piece, Position position) {
        if (!isLinearPiece(piece)) {
            return false;
        }

        Position piecePos = piece.getPosition();
        int dx = position.getX() - piecePos.getX();
        int dy = position.getY() - piecePos.getY();

        // Check if position is on the same line as the piece
        if (piece instanceof Rook) {
            // Rook moves in straight lines (horizontal or vertical)
            return (dx == 0 && dy != 0) || (dy == 0 && dx != 0);
        } else if (piece instanceof Bishop) {
            // Bishop moves diagonally
            return Math.abs(dx) == Math.abs(dy) && dx != 0;
        } else if (piece instanceof Queen) {
            // Queen combines rook and bishop movement
            return (dx == 0 && dy != 0) || (dy == 0 && dx != 0) || (Math.abs(dx) == Math.abs(dy) && dx != 0);
        }

        return false;
    }

    /**
     * Gets all squares attacked by a specific piece for check/threat detection.
     * 
     * This method calculates attack squares differently from legal moves:
     * - For pawns, ONLY includes diagonal attack squares (for threat detection)
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
            int direction = (piece.getColor() == Color.WHITE) ? 1 : -1;
            int newY = y + direction;

            // Pawns threaten diagonal squares (for check detection)
            if (newY >= 0 && newY < 8) {
                if (x - 1 >= 0) {
                    attacks.add(new Position(x - 1, newY));
                }
                if (x + 1 < 8) {
                    attacks.add(new Position(x + 1, newY));
                }
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
        if (piece instanceof Queen) {
            return new int[][] { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 }, { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 } };
        } else if (piece instanceof Rook) {
            return new int[][] { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
        } else if (piece instanceof Bishop) {
            return new int[][] { { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 } };
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

    /**
     * Gets all valid moves for a specific piece, accounting for piece-specific
     * movement rules.
     * This is different from getAttackSquares because pawns have different movement
     * vs attack patterns.
     * 
     * @param piece the piece to get valid moves for
     * @return Set of positions the piece can legally move to
     */
    public Set<Position> getValidMovesForPiece(Piece piece) {
        if (!isValid)
            generateAttackMaps();

        Set<Position> validMoves = new HashSet<>();

        if (piece instanceof Pawn) {
            // Pawns have special movement rules different from attack squares
            Position pos = piece.getPosition();
            int x = pos.getX();
            int y = pos.getY();
            int direction = (piece.getColor() == Color.WHITE) ? 1 : -1;
            int newY = y + direction;

            // Pawns can move diagonally ONLY if there's an enemy piece to capture
            if (newY >= 0 && newY < 8) {
                if (x - 1 >= 0) {
                    Position diagLeft = new Position(x - 1, newY);
                    Piece targetPiece = board.getPieceAt(diagLeft);
                    if (targetPiece != null && targetPiece.getColor() != piece.getColor()) {
                        validMoves.add(diagLeft);
                    }
                }
                if (x + 1 < 8) {
                    Position diagRight = new Position(x + 1, newY);
                    Piece targetPiece = board.getPieceAt(diagRight);
                    if (targetPiece != null && targetPiece.getColor() != piece.getColor()) {
                        validMoves.add(diagRight);
                    }
                }
            }

            // Pawns can move forward if the square is empty
            if (newY >= 0 && newY < 8) {
                Position forwardOne = new Position(x, newY);
                if (board.getPieceAt(forwardOne) == null) {
                    validMoves.add(forwardOne);

                    // Two squares forward on first move if both squares are empty
                    Pawn pawn = (Pawn) piece;
                    if (!pawn.getHasMoved()) {
                        int twoSquaresY = y + (direction * 2);
                        if (twoSquaresY >= 0 && twoSquaresY < 8) {
                            Position forwardTwo = new Position(x, twoSquaresY);
                            if (board.getPieceAt(forwardTwo) == null) {
                                validMoves.add(forwardTwo);
                            }
                        }
                    }
                }
            }
        } else {
            // For non-pawns, recalculate attack squares with current board state
            // and filter out friendly pieces
            Set<Position> attacks = getAttackSquares(piece);
            for (Position pos : attacks) {
                Piece targetPiece = board.getPieceAt(pos);
                // Can move if empty or enemy piece (not friendly)
                if (targetPiece == null || targetPiece.getColor() != piece.getColor()) {
                    validMoves.add(pos);
                }
            }
        }

        return validMoves;
    }

    /**
     * Validates a move and updates the attack map atomically.
     * 
     * This method combines move validation with attack map updates to avoid
     * redundant calculations. It temporarily applies the move to the attack map,
     * checks if the king would be in check, and either commits or rolls back
     * the changes. It also handles updating linear pieces that may be affected
     * by the move (blocked or unblocked), and properly simulates captures.
     * 
     * @param piece  the piece to move
     * @param oldPos the piece's current position
     * @param newPos the target position
     * @return true if the move is safe (doesn't leave king in check), false
     *         otherwise
     */
    public boolean validateAndUpdateMove(Piece piece, Position oldPos, Position newPos) {
        if (!isValid) {
            generateAttackMaps();
        }

        // Check if this move captures an enemy piece
        Piece capturedPiece = board.getPieceAt(newPos);
        boolean isCapture = capturedPiece != null && capturedPiece.getColor() != piece.getColor();

        // If capturing, temporarily remove the captured piece from its player
        Player capturedPieceOwner = null;
        if (isCapture) {
            capturedPieceOwner = board.getPlayer(capturedPiece.getColor());
            capturedPieceOwner.getCurrentPieces().remove(capturedPiece);
        }

        // Find all linear pieces that will be affected by this move
        List<Piece> affectedPieces = new ArrayList<>();
        Player whitePlayer = board.getPlayer(Color.WHITE);
        Player blackPlayer = board.getPlayer(Color.BLACK);

        for (Piece p : whitePlayer.getCurrentPieces()) {
            if (p != piece && isLinearPiece(p)) {
                if (isPositionOnAttackLine(p, oldPos) || isPositionOnAttackLine(p, newPos)) {
                    affectedPieces.add(p);
                }
            }
        }

        for (Piece p : blackPlayer.getCurrentPieces()) {
            if (p != piece && isLinearPiece(p)) {
                if (isPositionOnAttackLine(p, oldPos) || isPositionOnAttackLine(p, newPos)) {
                    affectedPieces.add(p);
                }
            }
        }

        // Store the complete old state for potential rollback
        Map<Piece, Set<Position>> oldAttacksMap = new HashMap<>();

        // Save the moving piece's old attacks
        piece.setPosition(oldPos);
        oldAttacksMap.put(piece, new HashSet<>(getAttackSquares(piece)));

        // If capturing, save and remove the captured piece's attacks
        if (isCapture) {
            oldAttacksMap.put(capturedPiece, new HashSet<>(getAttackSquares(capturedPiece)));
        }

        // Save affected pieces' old attacks
        for (Piece affectedPiece : affectedPieces) {
            oldAttacksMap.put(affectedPiece, new HashSet<>(getAttackSquares(affectedPiece)));
        }

        // Update the moving piece to new position
        piece.setPosition(newPos);

        // Calculate new attacks for all affected pieces (including the moved piece)
        Map<Piece, Set<Position>> newAttacksMap = new HashMap<>();
        newAttacksMap.put(piece, new HashSet<>(getAttackSquares(piece)));

        // Captured piece has no attacks after being captured
        if (isCapture) {
            newAttacksMap.put(capturedPiece, new HashSet<>());
        }

        for (Piece affectedPiece : affectedPieces) {
            newAttacksMap.put(affectedPiece, new HashSet<>(getAttackSquares(affectedPiece)));
        }

        // Apply updates to attack maps
        for (Map.Entry<Piece, Set<Position>> entry : oldAttacksMap.entrySet()) {
            Piece p = entry.getKey();
            Set<Position> oldAttacks = entry.getValue();
            Set<Position> newAttacks = newAttacksMap.get(p);

            Color pieceColor = p.getColor();
            Set<Position> targetAttackSet = (pieceColor == Color.WHITE) ? whiteAttacks : blackAttacks;
            Map<Position, Set<Piece>> targetAttackerMap = (pieceColor == Color.WHITE) ? whiteAttackers : blackAttackers;

            // Remove old attacks
            for (Position pos : oldAttacks) {
                Set<Piece> attackers = targetAttackerMap.get(pos);
                if (attackers != null) {
                    attackers.remove(p);
                    if (attackers.isEmpty()) {
                        targetAttackerMap.remove(pos);
                        targetAttackSet.remove(pos);
                    }
                }
            }

            // Add new attacks
            for (Position pos : newAttacks) {
                targetAttackSet.add(pos);
                targetAttackerMap.computeIfAbsent(pos, k -> new HashSet<>()).add(p);
            }
        }

        // Check if king is in check with the updated attack map
        Color pieceColor = piece.getColor();
        Player player = board.getPlayer(pieceColor);
        Position kingPos = player.getKingPosition();
        Color opponentColor = (pieceColor == Color.WHITE) ? Color.BLACK : Color.WHITE;
        boolean kingIsSafe = !isSquareAttackedBy(kingPos, opponentColor);

        if (kingIsSafe) {
            // Move is safe, keep the updated attack map
            // Restore the captured piece to the player list (will be removed later in
            // actual capture)
            if (isCapture) {
                capturedPieceOwner.getCurrentPieces().add(capturedPiece);
            }
            return true;
        } else {
            // Move leaves king in check, rollback all changes
            for (Map.Entry<Piece, Set<Position>> entry : oldAttacksMap.entrySet()) {
                Piece p = entry.getKey();
                Set<Position> oldAttacks = entry.getValue();
                Set<Position> newAttacks = newAttacksMap.get(p);

                Color pColor = p.getColor();
                Set<Position> targetAttackSet = (pColor == Color.WHITE) ? whiteAttacks : blackAttacks;
                Map<Position, Set<Piece>> targetAttackerMap = (pColor == Color.WHITE) ? whiteAttackers : blackAttackers;

                // Remove new attacks that were added
                for (Position pos : newAttacks) {
                    Set<Piece> attackers = targetAttackerMap.get(pos);
                    if (attackers != null) {
                        attackers.remove(p);
                        if (attackers.isEmpty()) {
                            targetAttackerMap.remove(pos);
                            targetAttackSet.remove(pos);
                        }
                    }
                }

                // Restore old attacks
                for (Position pos : oldAttacks) {
                    targetAttackSet.add(pos);
                    targetAttackerMap.computeIfAbsent(pos, k -> new HashSet<>()).add(p);
                }
            }

            // Restore captured piece to the player list
            if (isCapture) {
                capturedPieceOwner.getCurrentPieces().add(capturedPiece);
            }

            // Restore piece position
            piece.setPosition(oldPos);
            return false;
        }
    }
}