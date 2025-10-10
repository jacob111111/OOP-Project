package player;
// Will have Human and AI players
/**
 * Player base class.
 * TODO: Add class description and usage details.
 */

import java.util.ArrayList;
import java.util.List;

import piece.Piece;
import utils.Color;

public abstract class Player {
    private Color color;
    private List<Piece> pieces;

    public Player() { // Pieces are only initialized when color is set
        this.pieces = new ArrayList<Piece>();
        this.color = null;
    }
    
    public void setColor(Color color) { 
        this.color = color; 
        initializePiecePositions(); 
    }

    public Color getColor() { return color; }

    public abstract boolean makeMove(int newX, int newY);
    //public abstract Position[] possibleMoves();

    private void initializePiecePositions() {

    }
}