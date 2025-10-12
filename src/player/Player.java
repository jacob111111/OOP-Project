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

    public Player() { // Initial piece positions are only initialized when color is set
        this.currentPieces = new ArrayList<Piece>();
        this.color = null;
    }
    
    public void setColor(Color color) { 
        this.color = color; 
        initializePieces(); 
    }
    //getters
    public Color getColor() { return color; }
    public ArrayList<Piece> getCurrentPieces(){ return currentPieces; }

    //setters
    // Allows the player to input a move and attempts to execute it on the board
    public abstract boolean makeMove(Position newPosition, int pieceID);

    public void sortCurrentPieces() {
        piece -> piece.getPosition().getY();
        (piece1, piece2) -> {
            // comparison logic here
        }
    }
/**
    * Piece ID's are constant across all instances of players.
 * WHITE: No Offset    BLACK: Offset by 16
 * King: 0             King: 16
 * Queen: 1            Queen: 17
 * Rook: 2-3           Rook: 18-19
 * Bishop: 4-5         Bishop: 20-21
 * Knight: 6-7         Knight: 22-23
 * Pawn: 8-15          Pawn: 24-31
 * Pawn promotion ID will be offset again
 */
    private void initializePieces() {
        int ID = 0;
        if(color == Color.BLACK) {ID += 16; } 

        int colorOffset = (color == Color.WHITE) ? 0 : 7;
        int pawnRow = (color == Color.WHITE) ? 1 : 6;
        
        currentPieces.add(new King(color, new Position(4, colorOffset), ID++));
        currentPieces.add(new Queen(color, new Position(3, colorOffset), ID++));

        currentPieces.add(new Rook(color, new Position(0, colorOffset), ID++));
        currentPieces.add(new Rook(color, new Position(7, colorOffset), ID++));

        currentPieces.add(new Bishop(color, new Position(2, colorOffset), ID++));
        currentPieces.add(new Bishop(color, new Position(5, colorOffset), ID++));

        currentPieces.add(new Knight(color, new Position(1, colorOffset), ID++));
        currentPieces.add(new Knight(color, new Position(6, colorOffset), ID++));

        for(int i = 0; i < 8; i++) {
            currentPieces.add(new Pawn(color, new Position(i, pawnRow), ID++));
        }
    }
}