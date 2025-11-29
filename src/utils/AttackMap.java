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
     * Validates a move by simulating it and checking if the king would be in check.
     * 
     * This simplified version temporarily moves the piece, removes captured pieces,
     * regenerates the attack map, and checks for king safety. If unsafe, everything
     * is rolled back. Much simpler than incremental updates!
     * 
     * @param piece  the piece to move
     * @param oldPos the piece's current position
     * @param newPos the target position
     * @return true if the move is safe (doesn't leave king in check), false
     *         otherwise
     */
    public boolean validateAndUpdateMove(Piece piece, Position oldPos, Position newPos) {
        // Check if this move captures an enemy piece
        Piece capturedPiece = board.getPieceAt(newPos);
        boolean isCapture = capturedPiece != null && capturedPiece.getColor() != piece.getColor();

        // Temporarily remove captured piece from its player if this is a capture
        Player capturedPieceOwner = null;
        if (isCapture) {
            capturedPieceOwner = board.getPlayer(capturedPiece.getColor());
            capturedPieceOwner.getCurrentPieces().remove(capturedPiece);
        }

        // Move the piece to the new position
        piece.setPosition(newPos);

        // Regenerate attack map with the new board state
        generateAttackMaps();

        // Check if king is safe after the move
        Color pieceColor = piece.getColor();
        Player player = board.getPlayer(pieceColor);
        Position kingPos = player.getKingPosition();
        Color opponentColor = (pieceColor == Color.WHITE) ? Color.BLACK : Color.WHITE;
        boolean kingIsSafe = !isSquareAttackedBy(kingPos, opponentColor);

        if (kingIsSafe) {
            // Move is safe - restore captured piece to player list (will be removed in
            // actual capture)
            if (isCapture) {
                capturedPieceOwner.getCurrentPieces().add(capturedPiece);
            }
            return true;
        } else {
            // Move leaves king in check - rollback everything
            piece.setPosition(oldPos);

            // Restore captured piece to player list
            if (isCapture) {
                capturedPieceOwner.getCurrentPieces().add(capturedPiece);
            }

            // Regenerate attack map to restore original state
            generateAttackMaps();
            return false;
        }
    }
}