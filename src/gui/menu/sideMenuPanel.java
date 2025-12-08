package gui.menu;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.*;

import gui.ChessFrame;
import gui.utils.UIPalette;
import gui.utils.UIStyle;

/**
 * Side panel containing game controls, mode selection, and settings.
 * 
 * This panel provides the main interface for starting games, managing
 * game state (save/load), and configuring application settings. It remains
 * visible alongside the chess board during gameplay.
 */
public class SideMenuPanel extends JPanel {
    private ChessFrame parentFrame;
    private JLabel gameTitle;
    private GameControlPanel gameControlPanel;
    private ThemeSettingsPanel themeSettingsPanel;
    private MessageBoardPanel messageBoardPanel;
    private GameInfoPanel gameInfoPanel;

    /**
     * Creates a new side menu panel with the specified parent frame.
     * 
     * @param parent The parent chess frame that contains this panel
     */
    public SideMenuPanel(ChessFrame parent) {
        this.parentFrame = parent;
        initializeComponents();
        updateStyle();
    }

    /**
     * Initializes all UI components and sets up event handlers.
     * 
     * Creates buttons for game modes, game controls, and settings panels.
     * Configures layouts and adds action listeners for user interactions.
     */
    private void initializeComponents() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(220, 0)); // Slightly wider for new buttons

        // Game title
        gameTitle = new JLabel("CHESS", SwingConstants.CENTER);
        gameTitle.setPreferredSize(new Dimension(220, 50));

        // Game control panel
        gameControlPanel = new GameControlPanel(
            this::handleNewGame,
            this::handleSaveGame,
            this::handleLoadGame
        );

        // Theme settings panel
        themeSettingsPanel = new ThemeSettingsPanel(parentFrame);

        // Message board panel
        messageBoardPanel = new MessageBoardPanel();

        // Game info panel
        gameInfoPanel = new GameInfoPanel(messageBoardPanel);

        // Create container for game controls and game info
        JPanel centerContainer = new JPanel();
        centerContainer.setLayout(new BoxLayout(centerContainer, BoxLayout.Y_AXIS));
        centerContainer.add(gameControlPanel);
        centerContainer.add(Box.createRigidArea(new Dimension(0, 10)));
        centerContainer.add(gameInfoPanel);

        // Add components to main panel
        add(gameTitle, BorderLayout.NORTH);
        add(centerContainer, BorderLayout.CENTER);
        add(themeSettingsPanel, BorderLayout.SOUTH);

        // Set initial button states
        setGameInProgress(false);
    }

    /**
     * Handles the creation of a new game.
     * 
     * If a game is currently in progress, prompts the user for confirmation
     * before resetting. Otherwise, shows game mode selection dialog.
     */
    private void handleNewGame() {
        if (parentFrame.getCurrentGame() != null) {
            // Game is in progress, confirm reset
            int result = JOptionPane.showConfirmDialog(
                    this,
                    "This will override the current game. Are you sure?",
                    "New Game",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (result == JOptionPane.YES_OPTION) {
                showGameModeSelection();
            }
        } else {
            // No game in progress, show mode selection
            showGameModeSelection();
        }
    }

    /**
     * Displays game mode selection dialog for starting a new game.
     * Delegates to GameModeDialog.
     */
    private void showGameModeSelection() {
        GameModeDialog.show(
            this, 
            parentFrame,
            this::showAIDifficultyDialog,
            this::showOnlineGameDialog
        );
    }

    /**
     * Shows AI difficulty selection dialog.
     * Delegates to AIDifficultyDialog.
     */
    private void showAIDifficultyDialog() {
        AIDifficultyDialog.show(this, parentFrame);
    }

    /**
     * Shows the online game dialog.
     * Delegates to OnlineGameDialog.
     */
    private void showOnlineGameDialog() {
        OnlineGameDialog.show(this, parentFrame);
    }

    /**
     * Handles saving the current game state to a file.
     * Delegates to SaveLoadManager.
     */
    private void handleSaveGame() {
        SaveLoadManager.handleSaveGame(this, parentFrame.getCurrentGame());
    }

    /**
     * Handles loading a game state from a file.
     * Delegates to SaveLoadManager.
     */
    private void handleLoadGame() {
        game.GUI loadedGame = SaveLoadManager.handleLoadGame(this, parentFrame.getCurrentGame());
        if (loadedGame != null) {
            parentFrame.setGame(loadedGame);
            setGameInProgress(true);
        }
    }

    /**
     * Enables or disables buttons based on game state
     */
    public void setGameInProgress(boolean gameInProgress) {
        // Delegate to game control panel
        gameControlPanel.setGameInProgress(gameInProgress);
        
        // Turn indicator visibility
        gameInfoPanel.setGameInProgress(gameInProgress);
    }

    /**
     * Displays a message in the message board.
     * Delegates to MessageBoardPanel.
     * 
     * @param message     The message to display
     * @param messageType The type of message ("error" or "info")
     */
    public void displayMessage(String message, String messageType) {
        messageBoardPanel.displayMessage(message, messageType);
    }

    /**
     * Clears all messages from the message board.
     * Delegates to MessageBoardPanel.
     */
    public void clearMessages() {
        messageBoardPanel.clearMessages();
    }

    /**
     * Updates the hover info label to show the currently hovered piece.
     * Delegates to GameInfoPanel.
     * 
     * @param pieceName  The name of the piece being hovered over
     * @param pieceColor The color of the piece
     */
    public void updateHoverInfo(String pieceName, String pieceColor) {
        gameInfoPanel.updateHoverInfo(pieceName, pieceColor);
    }

    /**
     * Clears the hover info label.
     * Delegates to GameInfoPanel.
     */
    public void clearHoverInfo() {
        gameInfoPanel.clearHoverInfo();
    }

    /**
     * Updates the turn indicator to show whose turn it is.
     * Delegates to GameInfoPanel.
     * 
     * @param currentTurn The color of the player whose turn it is
     */
    public void updateTurnIndicator(utils.Color currentTurn) {
        gameInfoPanel.updateTurnIndicator(currentTurn);
    }

    /**
     * Updates the visual styling of all components using current palette.
     * 
     * Applies the current UI palette and styling to all buttons, panels,
     * and labels in the menu. Should be called when theme changes.
     */
    public void updateStyle() {
        UIStyle style = parentFrame.getStyle();
        UIPalette palette = parentFrame.getPalette();

        // Style the main panel (no border, no label)
        setBackground(palette.labelBackground);

        // Style game control panel
        gameControlPanel.updateStyle(style, palette);

        // Style panels
        gameInfoPanel.setBackground(palette.labelBackground);

        // Style labels and components
        gameTitle.setFont(palette.font);
        gameTitle.setForeground(palette.labelForeground);

        // Style theme settings panel
        themeSettingsPanel.updateStyle(palette);

        // Style game info panel
        gameInfoPanel.updateStyle(palette);

        // Style message board panel
        messageBoardPanel.updateStyle(palette);

        repaint();
        revalidate();
    }
}
