package utils;

/**
 * Enumeration representing different game modes available in the chess application.
 * 
 * This enum is used to distinguish between different types of gameplay
 * and determine the appropriate game flow and interface.
 */
public enum GameType {
    /** Console-based local gameplay between two human players */
    CONSOLE,
    
    /** Player vs Player mode with graphical interface */
    PVP,
    
    /** Player vs Engine/AI mode for single-player gameplay */
    PVE
}
