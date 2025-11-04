package utils;

import java.util.HashSet;
import java.util.Set;
import board.Board;
import piece.*;
import player.Player;

/**
 * Efficiently calculates and caches which squares are under attack by each color.
 * 
 * This class provides a significant performance optimization for check and checkmate detection
 * by pre-computing all attacked squares once per board state, then providing O(1) lookup
 * for attack queries. This avoids the expensive cascading recalculations that would occur
 * if piece movements were computed repeatedly during game analysis.
 * 
 * Performance Benefits:
 * - O(1) attack square lookups after O(n) preprocessing
 * - Avoids repeated piece.findPossibleMoves() calls
 * - Eliminates cascading performance issues during complex game analysis
 * - Supports fast check/checkmate detection algorithms
 * 
 * Usage Pattern:
 * 
 * AttackMap attackMap = new AttackMap(board);
 * boolean isUnderAttack = attackMap.isSquareAttackedBy(position, Color.WHITE);
 * boolean kingInCheck = attackMap.isKingInCheck(Color.BLACK);
 * 
 * // After board changes:
 * attackMap.invalidate(); // Forces recalculation on next query
 * 
 */
public class AttackMap {
    private Board board;
    private Set<Position> whiteAttacks = new HashSet<>();
    private Set<Position> blackAttacks = new HashSet<>();
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
     * 
     * Time Complexity: O(n × m) where n = number of pieces, m = average attack squares per piece
     * Space Complexity: O(k) where k = total number of attacked squares (max 128 for both colors)
     * 
     * @implNote This method clears existing attack maps and rebuilds them from scratch.
     *           For incremental updates, consider implementing a more sophisticated
     *           invalidation strategy.
     */
    private void generateAttackMaps() {
        whiteAttacks.clear();
        blackAttacks.clear();
        
        // Generate white attacks
        Player whitePlayer = board.getPlayer(Color.WHITE);
        for (Piece piece : whitePlayer.getCurrentPieces()) {
            Set<Position> attacks = getAttackSquares(piece);
            whiteAttacks.addAll(attacks);
        }
        
        // Generate black attacks
        Player blackPlayer = board.getPlayer(Color.BLACK);
        for (Piece piece : blackPlayer.getCurrentPieces()) {
            Set<Position> attacks = getAttackSquares(piece);
            blackAttacks.addAll(attacks);
        }
        
        isValid = true;
    }
    
    /**
     * Gets all squares attacked by a specific piece.
     * 
     * This method calculates attack squares differently from legal moves:
     * - Includes squares occupied by own pieces (for king safety analysis)
     * - For pawns, only includes diagonal attack squares (not forward movement)
     * - For linear pieces, stops at the first piece encountered but includes that square
     * - For knights and kings, includes all reachable squares regardless of occupation
     * 
     * @param piece the piece whose attack squares should be calculated
     * @return a Set of Position objects representing squares under attack by this piece
     * @throws NullPointerException if piece is null
     * @implNote This method does not validate that the piece is actually on the board
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
                if (x - 1 >= 0) attacks.add(new Position(x - 1, newY));
                if (x + 1 < 8) attacks.add(new Position(x + 1, newY));
            }
        } else if (piece instanceof Knight) {
            // Knight L-shaped attacks
            int[][] knightMoves = {{2,1},{2,-1},{-2,1},{-2,-1},{1,2},{1,-2},{-1,2},{-1,-2}};
            for (int[] move : knightMoves) {
                int newX = x + move[0];
                int newY = y + move[1];
                if (newX >= 0 && newX < 8 && newY >= 0 && newY < 8) {
                    attacks.add(new Position(newX, newY));
                }
            }
        } else if (piece instanceof King) {
            // King attacks one square in all directions
            int[][] kingMoves = {{1,0},{-1,0},{0,1},{0,-1},{1,1},{1,-1},{-1,1},{-1,-1}};
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
                    
                    if (newX < 0 || newX >= 8 || newY < 0 || newY >= 8) break;
                    
                    attacks.add(new Position(newX, newY));
                    
                    // Stop if we hit any piece (but include the attack on that square)
                    if (board.getPieceAt(new Position(newX, newY)) != null) break;
                }
            }
        }
        
        return attacks;
    }
    
    /**
     * Gets the movement direction vectors for linear pieces.
     * 
     * @param piece the piece whose directions are needed (Queen, Rook, or Bishop)
     * @return array of direction vectors as [dx, dy] pairs
     */
    private int[][] getDirections(Piece piece) {
        if (piece instanceof Rook) {
            return new int[][]{{1,0},{-1,0},{0,1},{0,-1}};
        } else if (piece instanceof Bishop) {
            return new int[][]{{1,1},{1,-1},{-1,1},{-1,-1}};
        } else if (piece instanceof Queen) {
            return new int[][]{{1,0},{-1,0},{0,1},{0,-1},{1,1},{1,-1},{-1,1},{-1,-1}};
        }
        return new int[0][0];
    }
    
    /**
     * Fast O(1) check if a square is under attack by the specified color.
     * 
     * This is the primary interface for attack queries and provides significant
     * performance benefits over traditional piece-by-piece checking.
     * 
     * @param pos the position to check for attacks
     * @param color the color whose attacks to check for (WHITE or BLACK)
     * @return true if the specified position is under attack by the specified color
     * @throws NullPointerException if pos or color is null
     * @implNote If the attack maps are invalid, this method will automatically
     *           regenerate them before performing the lookup
     */
    public boolean isSquareAttackedBy(Position pos, Color color) {
        if (!isValid) generateAttackMaps();
        return (color == Color.WHITE) ? whiteAttacks.contains(pos) : blackAttacks.contains(pos);
    }
    
    /**
     * Fast check if the king of specified color is in check.
     * 
     * This convenience method combines king position lookup with attack detection
     * to provide a simple interface for check detection.
     * 
     * @param kingColor the color of the king to check (WHITE or BLACK)
     * @return true if the king of the specified color is currently in check
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
     * Invalidates the attack maps when the board state changes.
     * 
     * This method should be called after any operation that changes piece positions:
     * - Piece moves
     * - Piece captures
     * - Piece promotions
     * - Castling
     * - En passant captures
     * 
     * The maps will be automatically regenerated on the next query.
     * 
     * @implNote This is a lazy invalidation strategy - the actual recalculation
     *           is deferred until the next attack query is made
     */
    public void invalidate() {
        isValid = false;
    }
}