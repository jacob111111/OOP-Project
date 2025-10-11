package piece;

import utils.Color;
import utils.Position;

/**
 * Pieces base class.
 * TODO: Add class description and usage details.
 */
public abstract class Piece {
    private Color color;
    private Position position;
    final private int ID;

    public Piece(Color color, Position position, int ID) {
        this.color = color;
        this.position = position;
        this.ID = ID;
    }

    public Color getColor() {return color; }
    public Position getPosition() {return position; }
    public int getID() {return ID; }

    public void setPosition(int x, int y) {
        this.position.setX(x);
        this.position.setY(y);
    }

    public abstract boolean canMove(int newX, int newY);
}