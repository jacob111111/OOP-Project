package utils;

import board.Board;
import piece.*;
import player.Player;

/**
 * checkmate detection using pre-computed attack maps and optimized algorithms.
 * 
 * This class provides efficient checkmate detection by avoiding the cascading performance
 * issues that arise from repeatedly recalculating piece movements. It uses a combination
 * of pre-computed attack maps and smart algorithms to minimize computational overhead.
 * 
 * Checkmate Detection Algorithm:
 * 1. Quick check: Is the king actually in check? (Exit early if not)
 * 2. King escape analysis: Can the king move to any safe square?
 * 3. Blocking analysis: Can any piece block the attack?
 * 4. Capture analysis: Can any piece capture the attacking piece?
 * 
 * Performance Characteristics:
 * - Best case: O(1) for positions not in check
 * - Average case: O(8) for king escape analysis + O(n) for piece analysis
 * - Worst case: O(n×m) where n=pieces, m=moves (still much faster than going through all possibleMove lists everytime)
 * 
 * // After any move:
 * detector.updateAfterMove();
 */
public class CheckmateDetector {
    private Board board;
    private AttackMap attackMap;
    
    public CheckmateDetector(Board board) {
        this.board = board;
        this.attackMap = new AttackMap(board);
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
     * @param kingColor the color of the king to check for checkmate (WHITE or BLACK)
     * @return true if the specified color is in checkmate, false otherwise
     * @throws IllegalArgumentException if kingColor is null
     * @throws IllegalStateException if no king is found for the specified color
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
     * @param kingPos the current position of the king
     * @param kingColor the color of the king being analyzed
     * @return true if the king can move to at least one safe square, false if trapped
     * 
     * @implNote This method simulates king movement to account for pieces that
     *           would no longer be attacking after the king moves (e.g., discovered attacks)
     */
    private boolean canKingEscape(Position kingPos, Color kingColor) {
        int x = kingPos.getX();
        int y = kingPos.getY();
        
        // Check all 8 adjacent squares
        int[][] kingMoves = {{1,0},{-1,0},{0,1},{0,-1},{1,1},{1,-1},{-1,1},{-1,-1}};
        
        for (int[] move : kingMoves) {
            int newX = x + move[0];
            int newY = y + move[1];
            
            // Must be within board bounds
            if (newX < 0 || newX >= 8 || newY < 0 || newY >= 8) continue;
            
            Position newPos = new Position(newX, newY);
            Piece pieceAtSquare = board.getPieceAt(newPos);
            
            // Can't move to square occupied by own piece
            if (pieceAtSquare != null && pieceAtSquare.getColor() == kingColor) continue;
            
            // Check if this square would still be under attack
            // We need to simulate the king move to check this properly
            if (wouldSquareBeSafeAfterKingMove(newPos, kingPos, kingColor)) {
                return true; // Found a safe escape square
            }
        }
        
        return false; // No safe squares found
    }
    
    /**
     * Simulates moving the king and checks if the destination would be safe.
     * 
     * This method performs a sophisticated analysis that accounts for pieces that
     * would no longer be attacking the target square after the king moves. This is
     * crucial for handling discovered attacks and pins correctly.
     * 
     * @param newKingPos the potential new position for the king
     * @param currentKingPos the king's current position (to ignore during simulation)
     * @param kingColor the color of the king being moved
     * @return true if the destination square would be safe after the king moves
     * 
     * @implNote This method temporarily ignores the king's current position when
     *           calculating attack lines, simulating the board state after the move
     */
    private boolean wouldSquareBeSafeAfterKingMove(Position newKingPos, Position currentKingPos, Color kingColor) {
        Color opponentColor = (kingColor == Color.WHITE) ? Color.BLACK : Color.WHITE;
        Player opponent = board.getPlayer(opponentColor);
        
        // Check if any opponent piece can attack the new king position
        for (Piece piece : opponent.getCurrentPieces()) {
            if (canPieceAttackSquare(piece, newKingPos, currentKingPos)) {
                return false; // This square would still be under attack
            }
        }
        
        return true; // Square would be safe
    }
    
    /**
     * Checks if a piece can attack a specific square, considering king movement simulation.
     * 
     * This method handles different piece types appropriately:
     * - Knights and Kings: Use standard move patterns (no blocking)
     * - Pawns: Only diagonal attack patterns
     * - Linear pieces: Check line-of-sight with king position simulation
     * 
     * @param piece the piece whose attack capability is being checked
     * @param target the target square to check for attack
     * @param currentKingPos the current king position (ignored during line-of-sight calculation)
     * @return true if the piece can attack the target square, false otherwise
     */
    private boolean canPieceAttackSquare(Piece piece, Position target, Position currentKingPos) {
        if (piece instanceof Knight || piece instanceof King) {
            // Knights and kings don't care about blocking pieces
            return piece.getPossibleMoves().contains(target);
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
     * @param pawn the pawn piece to check
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
     * Determines if a linear piece (Queen, Rook, or Bishop) can attack a target square.
     * 
     * This method first validates that the target is on a valid attack line for
     * the piece type, then checks if the path is clear.
     * 
     * @param piece the linear piece (Queen, Rook, or Bishop)
     * @param target the target square
     * @param currentKingPos king position to ignore during path calculation
     * @return true if the piece can attack the target
     */
    private boolean canLinearPieceAttack(Piece piece, Position target, Position currentKingPos) {
        Position piecePos = piece.getPosition();
        
        // Check if target is on a valid line for this piece type
        if (!isOnValidLine(piece, piecePos, target)) return false;
        
        // Check if path is clear (ignoring the current king position)
        return isPathClear(piecePos, target, currentKingPos);
    }
    
    /**
     * Validates if a target position is on a valid attack line for a linear piece.
     * 
     * @param piece the piece (must be Queen, Rook, or Bishop)
     * @param from the piece's current position
     * @param to the target position
     * @return true if the target is on a valid attack line for this piece type
     */
    private boolean isOnValidLine(Piece piece, Position from, Position to) {
        int dx = to.getX() - from.getX();
        int dy = to.getY() - from.getY();
        
        if (piece instanceof Rook) {
            return (dx == 0) || (dy == 0); // Horizontal or vertical
        } else if (piece instanceof Bishop) {
            return Math.abs(dx) == Math.abs(dy); // Diagonal
        } else if (piece instanceof Queen) {
            return (dx == 0) || (dy == 0) || (Math.abs(dx) == Math.abs(dy)); // All directions
        }
        
        return false;
    }
    
    /**
     * Checks if the path between two positions is clear of pieces.
     * 
     * This method walks along the line from the starting position to the target,
     * checking each intermediate square for pieces. It can ignore a specific
     * position (typically the king's current position during simulation).
     * 
     * @param from the starting position (exclusive)
     * @param to the ending position (exclusive)
     * @param ignoreKingAt position to ignore during path checking (for king move simulation)
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
     * @return true if the check can be resolved by blocking or capturing, false otherwise
     * 
     * @implNote This method currently returns false as a placeholder. Implementing
     *           full blocking analysis requires significant additional complexity.
     * @todo Implement complete blocking and capture analysis
     */
    private boolean canBlockOrCaptureChecker(Color kingColor) {
        // TODO: Implement sophisticated blocking/capture detection
        // For now, return false to complete the checkmate detection
        // This would involve:
        // 1. Finding which piece(s) are giving check
        // 2. For each checking piece, finding squares that would block
        // 3. Checking if any friendly piece can move to those squares
        // 4. Checking if any friendly piece can capture the checker
        
        return false; // Simplified for now
    }
    
    /**
     * Updates the internal attack map after a move is made.
     * 
     * This method should be called after any board state change to ensure
     * that subsequent checkmate analysis uses accurate attack information.
     * 
     * @implNote This method delegates to the underlying AttackMap's invalidation,
     *           using lazy evaluation to defer recalculation until needed
     * @see AttackMap#invalidate()
     */
    public void updateAfterMove() {
        attackMap.invalidate();
    }
}