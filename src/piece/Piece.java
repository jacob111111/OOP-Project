package piece;

import java.util.ArrayList;

import utils.Color;
import utils.Position;

/**
 * Pieces base class.
 * TODO: Add class description and usage details.
 */
public abstract class Piece {
    protected Color color;
    protected Position position;
    protected ArrayList<Position> possibleMoves;
    private StringBuilder displaySymbol = new StringBuilder();

    public Piece(Color color, Position position) {  // In all other pieces, run super constructor first, then append specific symbol
        this.color = color;
        this.position = position;
        if(color == Color.WHITE) {
            this.displaySymbol.append("w");
        } else {
            this.displaySymbol.append("b");
        }
        this.possibleMoves = new ArrayList<Position>();
        findPossibleMoves();
    }

    //getters
    public Color getColor() {return color; }
    public Position getPosition() {return position; }
    public ArrayList<Position> getPossibleMoves() { return possibleMoves; }
    public String getDisplaySymbol(){return displaySymbol.toString(); }
    public String getName() { return this.getClass().getSimpleName(); }
    public int getID() { return this.hashCode(); }

    //setters
    public boolean move(Position newPos) {
        if(possibleMoves.contains(newPos)) {
            this.position = newPos;
            findPossibleMoves(); // Recalculate possible moves after moving
            return true;
        }
        return false;
    }
    
    // Direct position setter for board management (bypasses move validation)
    public void setPosition(Position newPos) {
        this.position = newPos;
        findPossibleMoves(); // Recalculate possible moves after moving
    }

    public abstract void findPossibleMoves();
}