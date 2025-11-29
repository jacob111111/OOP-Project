package gui;

import javax.swing.JFrame;
import java.awt.BorderLayout;

import gui.board.MainBoardPanel;
import gui.menu.*;
import gui.utils.*;
import game.*;

/**
 * Main application window for the chess game GUI.
 * 
 * This class serves as the primary container for all GUI components,
 * managing the board display, side menu, and overall application state.
 * It handles theme management, game initialization, and coordinates
 * communication between different UI components.
 */
public class chessFrame extends JFrame {
    private static final int FRAME_WIDTH = 1000; // Increased width for side menu
    private static final int FRAME_HEIGHT = 800;

    private UIPalette masterPalette;
    private UIStyle masterStyle;
    private String currentPieceTheme = "classic"; // Track piece theme separately

    private MainBoardPanel boardPanel;
    private sideMenuPanel menuPanel;

    private GUI currentGame;

    /**
     * Creates a new chess frame with the specified UI palette.
     * 
     * Initializes the main window with board and menu panels, sets up
     * the layout, and makes the window visible. Uses the provided palette
     * for initial styling.
     * 
     * @param palette The UI palette to use for initial styling
     */
    public chessFrame(UIPalette palette) {
        this.masterPalette = palette;
        this.masterStyle = new UIStyle();
        setTitle("Chess Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Initialize panels
        boardPanel = new MainBoardPanel(this);
        menuPanel = new sideMenuPanel(this);

        // Add panels to frame
        add(boardPanel, BorderLayout.CENTER);
        add(menuPanel, BorderLayout.EAST);

        setVisible(true);
    }

    /**
     * Creates a new chess frame with the default classic palette.
     * 
     * Convenience constructor that uses the classic color scheme
     * for users who don't specify a particular theme preference.
     */
    public chessFrame() {
        this(UIPalette.CLASSIC);
    }

    public UIPalette getPalette() {
        return masterPalette;
    }

    /**
     * Gets the UI style utility instance.
     * 
     * @return The UIStyle instance used for component styling
     */
    public UIStyle getStyle() {
        return masterStyle;
    }

    /**
     * Sets a new UI palette and updates all components.
     * 
     * Changes the current color scheme and refreshes all UI components
     * to use the new palette. This allows for runtime theme switching.
     * 
     * @param newPalette The new palette to apply to the interface
     */
    public void setPalette(UIPalette newPalette) {
        this.masterPalette = newPalette;
        updateAllPanels();
    }

    /**
     * Updates styling for all panels using the current palette.
     * 
     * Forces a refresh of all UI components to ensure they reflect
     * the current theme settings. Called automatically when themes change.
     */
    private void updateAllPanels() {
        menuPanel.updateStyle();
        boardPanel.updateStyle();
        revalidate();
        repaint();
    }

    // Add this public method to expose update functionality to the menu
    public void refreshDisplay() {
        updateAllPanels();
    }

    /**
     * Gets the currently active game instance.
     * 
     * @return The current GUI game instance, or null if no game is active
     */
    public GUI getCurrentGame() {
        return currentGame;
    }

    /**
     * Sets the active game instance and updates the display.
     * 
     * Associates a game instance with this frame and updates the board
     * to reflect the game state. Also updates menu button states.
     * 
     * @param game The game instance to set as active, or null to clear
     */
    public void setGame(GUI game) {
        this.currentGame = game;
        if (game != null) {
            boardPanel.setGame(game);
            boardPanel.updateDisplay();
            menuPanel.setGameInProgress(true); // Disable game mode buttons
        } else {
            // Clear the board when game is null
            boardPanel.setGame(null);
            menuPanel.setGameInProgress(false); // Re-enable game mode buttons
        }
    }

    // Add method to clear the current game
    public void clearGame() {
        setGame(null);
    }

    // Method to start a new 2-player game
    public void startTwoPlayerGame() {
        GUI newGame = new GUI(true, utils.Color.WHITE);
        newGame.setParentFrame(this); // Set parent reference for clearing
        setGame(newGame);
    }

    // Method to change board theme (called from side menu)
    public void changeTheme(String themeName) {
        UIPalette newPalette = themeName.equals("Modern") ? UIPalette.MODERN : UIPalette.CLASSIC;
        setPalette(newPalette);
    }

    // Method to change piece theme (called from side menu)
    public void changePieceTheme(String pieceThemeName) {
        this.currentPieceTheme = pieceThemeName.toLowerCase();
        // Update the board to use new piece theme
        boardPanel.setPieceTheme(currentPieceTheme);
        if (currentGame != null) {
            boardPanel.updateDisplay();
        }
    }

    public String getCurrentPieceTheme() {
        return currentPieceTheme;
    }

    /**
     * Displays a message in the side menu message board.
     * 
     * @param message     The message to display
     * @param messageType The type of message ("error", "warning", "info")
     */
    public void displayMessage(String message, String messageType) {
        if (menuPanel != null) {
            menuPanel.displayMessage(message, messageType);
        }
    }
}