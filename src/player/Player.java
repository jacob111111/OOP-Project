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
    private Color color;
    private ArrayList<Piece> pieces;

    public Player() { // Pieces are only initialized when color is set
        this.pieces = new ArrayList<Piece>();
        this.color = null;
    }
    
    public void setColor(Color color) { 
        this.color = color; 
        initializePieces(); 
    }

    public Color getColor() { return color; }

    public abstract boolean makeMove(int newX, int newY);
    //public abstract Position[] possibleMoves();

    private void initializePieces() {
        int ID = 0;
        if(color == Color.BLACK) {ID += 16; } 

        int colorOffset = (color == Color.WHITE) ? 0 : 7;
        int pawnRow = (color == Color.WHITE) ? 1 : 6;
        
        pieces.add(new King(color, new Position(4, colorOffset), ID++));
        pieces.add(new Queen(color, new Position(3, colorOffset), ID++));

        pieces.add(new Rook(color, new Position(0, colorOffset), ID++));
        pieces.add(new Rook(color, new Position(7, colorOffset), ID++));

        pieces.add(new Bishop(color, new Position(2, colorOffset), ID++));
        pieces.add(new Bishop(color, new Position(5, colorOffset), ID++));

        pieces.add(new Knight(color, new Position(1, colorOffset), ID++));
        pieces.add(new Knight(color, new Position(6, colorOffset), ID++));

        for(int i = 0; i < 8; i++) {
            pieces.add(new Pawn(color, new Position(i, pawnRow), ID++));
        }
    }
}