package utils;

/**
 * Represents a position on the chess board using x,y coordinates.
 * 
 * This class encapsulates the coordinate system used throughout the chess game,
 * where positions are represented as integer coordinates on an 8x8 grid.
 * The class provides proper equals() and hashCode() implementations for
 * use in collections and comparison operations.
 * 
 */
public class Position {
    private int x;
    private int y;

    /**
     * Constructs a new Position with the specified coordinates.
     * 
     * @param x The x-coordinate (column) on the chess board
     * @param y The y-coordinate (row) on the chess board
     */
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Gets the x-coordinate of this position.
     * 
     * @return The x-coordinate (column)
     */
    public int getX() {return x; }
    
    /**
     * Gets the y-coordinate of this position.
     * 
     * @return The y-coordinate (row)
     */
    public int getY() {return y; }

    /**
     * Sets the x-coordinate of this position.
     * 
     * @param x The new x-coordinate (column)
     */
    public void setX(int x) {this.x = x; }
    
    /**
     * Sets the y-coordinate of this position.
     * 
     * @param y The new y-coordinate (row)
     */
    public void setY(int y) {this.y = y; }
    
    /**
     * Compares this position with another object for equality.
     * 
     * Two positions are considered equal if they have the same x and y coordinates.
     * 
     * @param obj The object to compare with this position
     * @return true if the positions are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Position position = (Position) obj;
        return x == position.x && y == position.y;
    }

    /**
     * Returns a hash code value for this position.
     * 
     * The hash code is calculated using both x and y coordinates
     * to ensure proper distribution in hash-based collections.
     * 
     * @return A hash code value for this position
     */
    @Override
    public int hashCode() {
        return x * 8 + y;
    }

    /**
     * Returns a string representation of this position.
     * 
     * @return A string in the format "(x,y)"
     */
    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

}
