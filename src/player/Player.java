package player;

import java.util.ArrayList;

import piece.*;
import utils.Color;
import utils.Position;

/**
 * Represents a chess player (human or AI) and manages their pieces.
 * 
 * The Player class is responsible for:
 * - Managing the player's collection of chess pieces
 * - Handling piece movement requests and validation
 * - Initializing starting piece positions
 * - Tracking the king's position for game logic
 * 
 * This class serves as the base class for both human players and AI players,
 * providing common functionality for piece management and movement.
 * 
 */
public class Player {
    /** The color this player controls (WHITE or BLACK) */
    protected Color color;
    
    /** List of all pieces currently owned by this player */
    protected ArrayList<Piece> currentPieces;

    /**
     * Creates a new player with the specified color.
     * 
     * Initializes the player's piece collection and sets up all starting
     * pieces in their proper positions according to chess rules.
     * 
     * @param color the color this player will control (WHITE or BLACK)
     */
    public Player(Color color) {
        this.currentPieces = new ArrayList<Piece>();
        this.color = color;
        initializePieces();
    }
    
    /**
     * Gets the color this player controls.
     * 
     * @return The player's color (WHITE or BLACK)
     */
    public Color getColor() { return color; }
    
    /**
     * Gets the list of pieces currently owned by this player.
     * 
     * @return ArrayList of all pieces belonging to this player
     */
    public ArrayList<Piece> getCurrentPieces(){ return currentPieces; }
    
    /**
     * Finds and returns the position of this player's king.
     * 
     * Searches through the player's pieces to locate the king, which is
     * essential for check and checkmate calculations.
     * 
     * @return The position of the king, or null if not found (should never happen)
     */
    public Position getKingPosition() {
        for(Piece piece : currentPieces) {
            if(piece instanceof King) {
                return piece.getPosition();
            }
        }
        return null; // should never happen in a valid game
    }
    
    /**
     * Attempts to move a piece to the specified position.
     * 
     * Validates that the target position is in the piece's list of possible moves
     * before executing the move. This provides the basic move validation logic.
     * 
     * @param possibleMove the target position for the move
     * @param pieceToMove the piece that should be moved
     * @return true if the move was successful, false if invalid
     */
    public boolean movePiece(Position possibleMove, Piece pieceToMove){
        if(pieceToMove.getPossibleMoves().contains(possibleMove)){
            pieceToMove.move(possibleMove);
            return true;
        }
        return false;
    }

    /**
     * Sorts the current pieces by position for efficient searching.
     * 
     * Sorts pieces first by row (y-coordinate), then by column (x-coordinate).
     * This ordering enables binary search operations for piece lookup.
     */
    public void sortCurrentPieces() {
        currentPieces.sort((piece1, piece2) -> {
            Position pos1 = piece1.getPosition();
            Position pos2 = piece2.getPosition();
            // Sort by row first, then column for efficient searching
            int rowCompare = pos1.getY() - pos2.getY();
            return rowCompare != 0 ? rowCompare : pos1.getX() - pos2.getX();
        });
    }

    /**
     * Finds a piece at the specified target position using binary search.
     * 
     * This method sorts the pieces first, then uses binary search for O(log n)
     * lookup performance. More efficient than linear search for large piece counts.
     * 
     * @param target the position to search for a piece
     * @return The piece at the target position, or null if no piece is found
     */
    public Piece findPieceAt(Position target) {
        sortCurrentPieces();
        int left = 0, right = currentPieces.size() - 1;
        
        while (left <= right) {
            int mid = (left + right) / 2;
            Position midPos = currentPieces.get(mid).getPosition();
            
            int comparison = comparePositions(midPos, target);
            if (comparison == 0) {
                return currentPieces.get(mid);
            } else if (comparison < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return null;
    }
    
    /**
     * Compares two positions for sorting purposes.
     * 
     * @param pos1 first position to compare
     * @param pos2 second position to compare
     * @return negative if pos1 < pos2, positive if pos1 > pos2, 0 if equal
     */
    private int comparePositions(Position pos1, Position pos2) {
        int rowCompare = pos1.getY() - pos2.getY();
        return rowCompare != 0 ? rowCompare : pos1.getX() - pos2.getX();
    }

    /**
     * Initializes all chess pieces in their starting positions.
     * 
     * Creates and positions all pieces according to standard chess setup:
     * - Back rank pieces (rooks, knights, bishops, queen, king)
     * - Eight pawns in front of the back rank
     * 
     * White pieces start on ranks 1-2, black pieces on ranks 7-8.
     */
    private void initializePieces() {
        int colorOffset = (color == Color.WHITE) ? 0 : 7;
        int pawnRow = (color == Color.WHITE) ? 1 : 6;
        
        currentPieces.add(new King(color, new Position(4, colorOffset)));
        currentPieces.add(new Queen(color, new Position(3, colorOffset)));

        currentPieces.add(new Rook(color, new Position(0, colorOffset)));
        currentPieces.add(new Rook(color, new Position(7, colorOffset)));

        currentPieces.add(new Bishop(color, new Position(2, colorOffset)));
        currentPieces.add(new Bishop(color, new Position(5, colorOffset)));

        currentPieces.add(new Knight(color, new Position(1, colorOffset)));
        currentPieces.add(new Knight(color, new Position(6, colorOffset)));

        for(int i = 0; i < 8; i++) {
            currentPieces.add(new Pawn(color, new Position(i, pawnRow)));
        }
    }
}