package utils;

/**
 * Enumeration representing the colors available in chess.
 * 
 * This enum is used throughout the application to represent piece colors,
 * player colors, and turn management in the chess game.
 */
public enum Color {
    /** White pieces/player - traditionally moves first in chess */
    WHITE,
    
    /** Black pieces/player - traditionally moves second in chess */
    BLACK,
    
    /** Random color selection for automated color assignment */
    RANDOM
}
