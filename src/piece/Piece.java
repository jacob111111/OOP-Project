package piece;

import java.io.Serializable;
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
public abstract class Piece implements Serializable {
    private static final long serialVersionUID = 1L;
    
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

    /**
     * Gets the color of this piece.
     * 
     * @return The color of this piece (WHITE or BLACK)
     */
    public Color getColor() {return color; }
    
    /**
     * Gets the current position of this piece on the board.
     * 
     * @return The current position of this piece
     */
    public Position getPosition() {return position; }
    
    /**
     * Gets the list of all possible moves for this piece.
     * 
     * @return ArrayList containing all valid positions this piece can move to
     */
    public ArrayList<Position> getPossibleMoves() { return possibleMoves; }
    
    /**
     * Gets the display symbol for this piece used in board representation.
     * 
     * @return String representation of this piece (e.g., "wK" for white king)
     */
    public String getDisplaySymbol(){return displaySymbol.toString(); }
    
    /**
     * Gets the simple class name of this piece.
     * 
     * @return The name of this piece's class (e.g., "King", "Queen", "Pawn")
     */
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
     * 
     * This method bypasses the normal move validation and should be used
     * carefully, typically only by the board management system.
     * 
     * @param newPos The new position to set for this piece
     */
    public void setPosition(Position newPos) {
        this.position = newPos;
        findPossibleMoves();
    }

    /**
     * Abstract method to calculate all possible moves for this piece.
     * 
     * Each piece type must implement this method to define its specific
     * movement rules. The method should populate the possibleMoves list
     * with all valid positions the piece can move to from its current position.
     */
    public abstract void findPossibleMoves();
}