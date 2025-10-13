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
        // First compare by y-coordinate (max to min)
        int yComparison = piece2.getPosition().getY() - piece1.getPosition().getY();
        if (yComparison != 0) {
            return yComparison;
        }
        // If y-coordinates are equal, compare by x-coordinate (min to max)
        return piece1.getPosition().getX() - piece2.getPosition().getX();
    });
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