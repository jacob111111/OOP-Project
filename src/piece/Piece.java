package piece;

import java.io.Serializable;

import utils.Color;
import utils.Position;

/**
 * Abstract base class for all chess pieces.
 * 
 * This class provides the common functionality and properties shared by all
 * chess pieces
 * 
 */
public abstract class Piece implements Serializable {
    private static final long serialVersionUID = 1L;

    protected Color color;
    protected Position position;

    /** Piece representation for board display */
    protected StringBuilder displaySymbol = new StringBuilder();

    /**
     * Constructs a new chess piece with the specified color and position.
     * 
     * Initializes the piece's color, position, and display symbol. 
     * The display symbol starts with 'w' for white pieces or 'b' for black pieces.
     * 
     * @param color    The color of the piece (WHITE or BLACK)
     * @param position The initial position of the piece on the board
     */
    public Piece(Color color, Position position) {
        this.color = color;
        this.position = position;
        if (color == Color.WHITE) {
            this.displaySymbol.append("w");
        } else {
            this.displaySymbol.append("b");
        }
    }

    /**
     * Gets the color of this piece.
     * 
     * @return The color of this piece (WHITE or BLACK)
     */
    public Color getColor() {
        return color;
    }

    /**
     * Gets the current position of this piece on the board.
     * 
     * @return The current position of this piece
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Gets the display symbol for this piece used in board representation.
     * 
     * @return String representation of this piece (e.g., "wK" for white king)
     */
    public String getDisplaySymbol() {
        return displaySymbol.toString();
    }

    /**
     * Gets the simple class name of this piece.
     * 
     * @return The name of this piece's class (e.g., "King", "Queen", "Pawn")
     */
    public String getName() {
        return this.getClass().getSimpleName();
    }

    /**
     * Moves piece to a new position.
     * 
     * @param newPos The target position to move to
     * @return always returns true (validation handled by Board/AttackMap)
     */
    public boolean move(Position newPos) {
        this.position = newPos;
        return true;
    }

    /**
     * Sets the position of this piece directly.
     * 
     * @param newPos The new position to set for this piece
     */
    public void setPosition(Position newPos) {
        this.position = newPos;
    }
}