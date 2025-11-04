package piece;

import java.util.ArrayList;

import utils.Color;
import utils.Position;

/**
 * Abstract base class for all chess pieces.
 * 
 * This class provides the common functionality and properties shared by all chess pieces,
 * including color, position, movement tracking, and display representation. Each specific
 * piece type extends this class and implements its own movement rules through the
 * findPossibleMoves() method.
 * 
 * The class follows the Template Method pattern where the structure of piece behavior
 * is defined here, but specific movement calculations are delegated to subclasses.
 * 
 */
public abstract class Piece {
    protected Color color;
    protected Position position;
    protected ArrayList<Position> possibleMoves;
    
    /** Piece representation for board display */
    protected StringBuilder displaySymbol = new StringBuilder();
    /**
     * Constructs a new chess piece with the specified color and position.
     * 
     * Initializes the piece's color, position, and display symbol. The display symbol
     * starts with 'w' for white pieces or 'b' for black pieces. Subclasses should
     * call this constructor first, then append their specific piece symbol.
     * 
     * @param color The color of the piece (WHITE or BLACK)
     * @param position The initial position of the piece on the board
     */
    public Piece(Color color, Position position) {
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

    public Color getColor() {return color; }
    public Position getPosition() {return position; }
    public ArrayList<Position> getPossibleMoves() { return possibleMoves; }
    public String getDisplaySymbol(){return displaySymbol.toString(); }
    public String getName() { return this.getClass().getSimpleName(); }

    /**
     * Attempts to move piece to a new position.
     * 
     * Ensures that new position is in the list of possible moves
     * before executing the move. Recalculates possible moves if successful.
     * 
     * @param newPos The target position to move to
     * @return true if the move was successful, false if invalid
     */
    public boolean move(Position newPos) {
        if(possibleMoves.contains(newPos)) {
            this.position = newPos;
            findPossibleMoves(); // Recalculate possible moves after moving
            return true;
        }
        return false;
    }
    
    /**
     * Sets the position of this piece directly without move validation.
     * @param newPos
     */
    public void setPosition(Position newPos) {
        this.position = newPos;
        findPossibleMoves();
    }

    /**
     * Lists all possible moves piece can make
     */
    public abstract void findPossibleMoves();
}