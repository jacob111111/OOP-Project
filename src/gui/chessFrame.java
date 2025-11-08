package gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;

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
public class chessFrame extends JFrame 
{
    private static final int FRAME_WIDTH = 800;
    private static final int FRAME_HEIGHT = 800;
    
    private CardLayout cardLayout;
    private JPanel cards;
    
    private gameState currentgameState;

    private UIPalette masterPalette;
    private UIStyle masterStyle;

    private mainMenuPanel menuPanel;
    private MainBoardPanel boardPanel;
    private settingsPanel settingsPanel;

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
    public chessFrame(UIPalette palette) 
    {
        this.masterPalette = palette;
        this.masterStyle = new UIStyle();
        setTitle("Chess Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        menuPanel = new mainMenuPanel(this);
        boardPanel = new MainBoardPanel(this);
        settingsPanel = new settingsPanel(this);

        cards.add(menuPanel, gameState.MENU.toString());
        cards.add(boardPanel, gameState.GAME.toString());
        cards.add(settingsPanel, gameState.SETTINGS.toString());
        
        currentgameState = gameState.MENU;
        add(cards);
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
    
    public gameState getGameState() {return currentgameState;}
    public UIPalette getPalette() { return masterPalette; }
    
    /**
     * Gets the UI style utility instance.
     * 
     * @return The UIStyle instance used for component styling
     */
    public UIStyle getStyle() { return masterStyle; }
    
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
        settingsPanel.updateStyle();
        revalidate();
        repaint();
    }

    public void switchToState(gameState newgameState) {
        cardLayout.show(cards, newgameState.toString());
        this.currentgameState = newgameState;
    }
    public void switchToMenu() { switchToState(gameState.MENU); }

    public void switchToGame() { 
        if (currentGame != null) {
            boardPanel.updateDisplay();
        }
        switchToState(gameState.GAME); 
    }

    public void switchToSettings() { switchToState(gameState.SETTINGS); }

    /**
     * Gets the currently active game instance.
     * 
     * @return The current GUI game instance, or null if no game is active
     */
    public GUI getCurrentGame() { return currentGame; }

    /**
     * Sets the active game instance and updates the display.
     * 
     * Associates a game instance with this frame and updates the board
     * to reflect the game state. Also updates menu button states.
     * 
     * @param game The game instance to set as active, or null to clear
     */
    public void setGame(GUI game) {
        this.currentGame = game; // Store game instance
        boardPanel.setGame(game); // Connect instance to board card
    }
}