package gui.utils;

/**
 * Enumeration representing different states of the GUI application.
 * 
 * Used to track the current screen or mode the application is in,
 * allowing for proper state management and UI updates.
 */
public enum gameState {
    /** Main menu state - showing game mode selection */
    MENU,
    
    /** Settings state - showing configuration options */
    SETTINGS,
    
    /** Active game state - chess game is in progress */
    GAME
}