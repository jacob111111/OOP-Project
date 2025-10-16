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
    /** The color of this chess piece (WHITE or BLACK) */
    protected Color color;
    
    /** The current position of this piece on the chess board */
    protected Position position;
    
    /** List of all valid positions this piece can move to */
    protected ArrayList<Position> possibleMoves;
    
    /** String representation of this piece for board display */
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

    /**
     * Gets the color of this piece.
     * 
     * @return The color of the piece (WHITE or BLACK)
     */
    public Color getColor() {return color; }
    
    /**
     * Gets the current position of this piece on the board.
     * 
     * @return The current position of the piece
     */
    public Position getPosition() {return position; }
    
    /**
     * Gets the list of all valid moves for this piece from its current position.
     * 
     * @return ArrayList of Position objects representing valid moves
     */
    public ArrayList<Position> getPossibleMoves() { return possibleMoves; }
    
    /**
     * Gets the string representation of this piece for display purposes.
     * 
     * @return A string representation of the piece (e.g., "wK" for white king)
     */
    public String getDisplaySymbol(){return displaySymbol.toString(); }
    
    /**
     * Gets the class name of this piece type.
     * 
     * @return The simple class name (e.g., "King", "Queen", "Pawn")
     */
    public String getName() { return this.getClass().getSimpleName(); }
    
    /**
     * Gets a unique identifier for this piece instance.
     * 
     * @return The hash code of this piece object
     */
    public int getID() { return this.hashCode(); }

    /**
     * Attempts to move this piece to a new position.
     * 
     * Validates that the target position is in the list of possible moves
     * before executing the move. Recalculates possible moves after a successful move.
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
     * This method is used by the board management system and bypasses
     * the normal move validation. Should be used carefully.
     * 
     * @param newPos The new position for this piece
     */
    public void setPosition(Position newPos) {
        this.position = newPos;
        findPossibleMoves(); // Recalculate possible moves after moving
    }

    /**
     * Calculates and updates the possible moves for this piece.
     * 
     * This abstract method must be implemented by each piece subclass
     * to define the specific movement rules for that piece type.
     * The implementation should update the possibleMoves list.
     */
    public abstract void findPossibleMoves();
}