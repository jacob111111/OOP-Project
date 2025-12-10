package piece;

import java.io.Serializable;
import java.util.Set;

import utils.Color;
import utils.Position;

/**
 * Abstract base class for all chess pieces.
 */
public abstract class Piece implements Serializable {
    private static final long serialVersionUID = 1L;

    protected Color color;
    protected Position position;

    /** Piece representation for board display */
    protected StringBuilder displaySymbol = new StringBuilder();

    /**
     * Creates a chess piece with color and position.
     * 
     * @param color    piece color (WHITE or BLACK)
     * @param position initial position on board
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
     * Gets the display symbol for board representation.
     * 
     * @return piece representation (e.g., "wK" for white king)
     */
    public String getDisplaySymbol() {
        return displaySymbol.toString();
    }

    /**
     * Gets the piece's class name.
     * 
     * @return piece name (e.g., "King", "Queen")
     */
    public String getName() {
        return this.getClass().getSimpleName();
    }

    /**
     * Moves piece to new position.
     * 
     * @param newPos target position
     * @return always true (validation handled elsewhere)
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

    /**
     * Gets all possible moves for this piece from its current position.
     * This method calculates where the piece can move based on its movement rules,
     * but does NOT validate check safety or turn ownership.
     * 
     * This is used primarily by AttackMap to determine which squares are under
     * attack.
     * For UI and move validation, use getLegalMoves() instead.
     * 
     * @param board The board to calculate moves on (needed to check for pieces
     *              blocking paths)
     * @return Set of all positions this piece can potentially move to (basic moves
     *         only)
     */
    public abstract Set<Position> getPossibleMoves(Object board);

    /**
     * Gets all legal moves for this piece, including special moves and rule
     * constraints.
     * 
     * This method builds on getPossibleMoves() and adds:
     * - Special moves (e.g., castling for King, en passant for Pawn)
     * - Rule-based constraints that require access to AttackMap
     * 
     * Default implementation just returns getPossibleMoves(). Pieces with special
     * moves (King, Pawn) should override this method.
     * 
     * @param board The board to calculate legal moves on
     * @return Set of all positions this piece can legally move to
     */
    public Set<Position> getLegalMoves(board.Board board) {
        return getPossibleMoves(board);
    }
}