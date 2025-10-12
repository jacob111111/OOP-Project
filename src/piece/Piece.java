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
    protected String name = "";
    final private int ID;

    public Piece(Color color, Position position, int ID) {
        this.color = color;
        this.position = position;
        this.ID = ID;
        this.possibleMoves = new ArrayList<Position>();
    }

    //getters
    public Color getColor() {return color; }
    public Position getPosition() {return position; }
    public int getID() {return ID; }
    public String getName(){return name; }

    //setters
    public void setPosition(int x, int y) {
        this.position.setX(x);
        this.position.setY(y);
    }


    /**
     * on move() - check if new position is inpossible, True, update position and recalculate possible moves, False, do nothing
     * on findPossibleMoves() - calculate possible moves based on current position and store in possibleMoves variable
     */
    public boolean move(Position newPos) {
        if(possibleMoves.contains(newPos)) {
            setPosition(newPos.getX(), newPos.getY());
            return true;
        }
        return false;
    }

    public abstract void findPossibleMoves();
}