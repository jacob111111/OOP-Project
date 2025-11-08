package gui.board;

import piece.*;
import utils.Position;
import game.GUI;

/**
 * Encapsulates the state of a chess move in progress within the GUI.
 * 
 * This class tracks information about a move that the user has initiated
 * but not yet completed, including the piece being moved, its source position,
 * the type of mouse interaction, and the current game state. It enables
 * proper handling of both click-based and drag-based move input methods.
 */
public class MoveState {
    /**
     * Enumeration of supported mouse interaction types for piece movement.
     */
    enum mouseEventType {
        /** Move initiated by dragging a piece */
        DRAG,
        
        /** Move initiated by clicking on pieces */
        CLICK
    }

    private Piece selectedPiece;
    private Position sourcePos;
    private GUI gameState;
    private mouseEventType mouseType;
    
    /**
     * Creates a new move state with the specified parameters.
     * 
     * @param piece The chess piece being moved
     * @param position The original position of the piece
     * @param mouseType The type of mouse interaction (DRAG or CLICK)
     * @param gameState The current game instance
     */
    public MoveState (Piece piece, Position position, mouseEventType mouseType, GUI gameState) {
        this.selectedPiece = piece;
        this.sourcePos = position;
        this.mouseType = mouseType;
        this.gameState = gameState;
    }

    /**
     * Gets the piece currently selected for movement.
     * 
     * @return The piece being moved
     */
    public Piece getSelectedPiece() { return selectedPiece;}
    
    /**
     * Gets the source position where the move originated.
     * 
     * @return The original position of the piece
     */
    public Position getSourcePosition() { return sourcePos;}
    
    /**
     * Gets the type of mouse interaction used for this move.
     * 
     * @return The mouse event type (DRAG or CLICK)
     */
    public mouseEventType getMouseEventType() { return mouseType;}
    
    /**
     * Gets the current game state associated with this move.
     * 
     * @return The GUI game instance
     */
    public GUI getGameState() { return gameState;}
}
