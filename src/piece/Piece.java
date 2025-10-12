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
    final private int ID;

    public Piece(Color color, Position position, int ID) {
        this.color = color;
        this.position = position;
        this.ID = ID;
        if(color == Color.WHITE) {
            this.displaySymbol.append("w");
        } else {
            this.displaySymbol.append("b");
        }
        // In all other pieces, run super constructor first, then append specific symbol
        this.possibleMoves = new ArrayList<Position>();
        findPossibleMoves();
    }

    //getters
    public Color getColor() {return color; }
    public Position getPosition() {return position; }
    public int getID() {return ID; }
    public ArrayList<Position> getPossibleMoves() { return possibleMoves; }
    public String getDisplaySymbol(){return displaySymbol.toString(); }

    //setters
    public void setPosition(Position newPos) {
        this.position = newPos;
    }


    /**
     * on move() - check if new position is inpossible, True, update position and recalculate possible moves, False, do nothing
     * on findPossibleMoves() - calculate possible moves based on current position and store in possibleMoves variable
     */
    public boolean move(Position newPos) {
        if(possibleMoves.contains(newPos)) {
            setPosition(newPos);
            return true;
        }
        return false;
    }

    public abstract void findPossibleMoves();
}