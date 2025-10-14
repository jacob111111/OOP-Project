package player;
// Will have Human and AI players
/**
 * Player base class.
 * TODO: Add class description and usage details.
 */

import java.util.ArrayList;

import piece.*;
import utils.Color;
import utils.Position;

public abstract class Player {
    protected Color color;
    protected ArrayList<Piece> currentPieces;

    public Player(Color color) {
        this.currentPieces = new ArrayList<Piece>();
        this.color = color;
        initializePieces();
    }
    
    //getters
    public Color getColor() { return color; }
    public ArrayList<Piece> getCurrentPieces(){ return currentPieces; }
    
    public Position getKingPosition() {
        for(Piece piece : currentPieces) {
            if(piece instanceof King) {
                return piece.getPosition();
            }
        }
        return null; // should never happen
    }
    
    //setters
    // Allows the player to input a move and attempts to execute it on the board
    public boolean movePiece(Position possibleMove, Piece pieceToMove){
        if(pieceToMove.getPossibleMoves().contains(possibleMove)){ // move can be made
            pieceToMove.move(possibleMove);
            return true;
        }
        return false;
    }

    //helpers
    public void sortCurrentPieces() {
        currentPieces.sort((piece1, piece2) -> {
            Position pos1 = piece1.getPosition();
            Position pos2 = piece2.getPosition();
            // Sort by row first, then column for efficient searching
            int rowCompare = pos1.getY() - pos2.getY();
            return rowCompare != 0 ? rowCompare : pos1.getX() - pos2.getX();
        });
    }
    

    /* JACOB LOOK AT THIS OMG ITS GONNA EXPLODE WAHHHHHHH
     * Because currentPieces is sorted by row and column, 
     * would keeping a tally of how many pieces are in each row allow for a faster
     *  lookup in findPieceat 
     * For ex: If we know ther are 4 things in row 1, 
     * and we need something in row 2, can immediatlly skip to the 3rd index
     */
    // Binary search for piece at position - O(log n) instead of O(n)
    public Piece findPieceAt(Position target) {
        sortCurrentPieces(); // Ensure list is sorted
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
    
    private int comparePositions(Position pos1, Position pos2) {
        int rowCompare = pos1.getY() - pos2.getY();
        return rowCompare != 0 ? rowCompare : pos1.getX() - pos2.getX();
    }

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