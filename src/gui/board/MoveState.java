package gui.board;

import piece.*;
import utils.Position;
import game.GUI;


public class MoveState {
    enum mouseEventType {
    DRAG,
    CLICK
    }

    private Piece selectedPiece;
    private Position sourcePos;
    private GUI gameState;
    private mouseEventType mouseType;
    

    public MoveState (Piece piece, Position position, mouseEventType mouseType, GUI gameState) {
        this.selectedPiece = piece;
        this.sourcePos = position;
        this.mouseType = mouseType;
        this.gameState = gameState;
    }

    public Piece getSelectedPiece() { return selectedPiece;}
    public Position getSourcePosition() { return sourcePos;}
    public mouseEventType getMouseEventType() { return mouseType;}
    public GUI getGameState() { return gameState;}
}
