package utils;

/**
 * Enumeration representing different game modes available in the chess application.
 * 
 * This enum is used to distinguish between different types of gameplay
 * and determine the appropriate game flow and interface.
 * 
 
 */
public enum GameType {
    /** Console-based local gameplay */
    CONSOLE,
    
    /** Player vs Player mode */
    PVP,
    
    /** Player vs Engine/AI mode */
    PVE
}
