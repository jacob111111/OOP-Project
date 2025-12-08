package gui.menu;

import java.awt.Dimension;
import javax.swing.*;

import gui.utils.UIPalette;
import gui.utils.UIStyle;

/**
 * Panel containing game control buttons (New Game, Save Game, Load Game).
 * 
 * This panel provides the main controls for starting new games and
 * managing game state through save/load operations.
 */
public class GameControlPanel extends JPanel {
    private JButton newGameButton, saveGameButton, loadGameButton;
    private Runnable onNewGame, onSaveGame, onLoadGame;

    /**
     * Creates a new game control panel with action callbacks.
     * 
     * @param onNewGame Callback when New Game is clicked
     * @param onSaveGame Callback when Save Game is clicked
     * @param onLoadGame Callback when Load Game is clicked
     */
    public GameControlPanel(Runnable onNewGame, Runnable onSaveGame, Runnable onLoadGame) {
        this.onNewGame = onNewGame;
        this.onSaveGame = onSaveGame;
        this.onLoadGame = onLoadGame;
        initializeComponents();
    }

    /**
     * Initializes the game control buttons and layout.
     */
    private void initializeComponents() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Game control buttons
        newGameButton = new JButton("New Game");
        saveGameButton = new JButton("Save Game");
        loadGameButton = new JButton("Load Game");

        // Add action listeners
        newGameButton.addActionListener(e -> onNewGame.run());
        saveGameButton.addActionListener(e -> onSaveGame.run());
        loadGameButton.addActionListener(e -> onLoadGame.run());

        // Style buttons
        styleMenuButton(newGameButton);
        styleMenuButton(saveGameButton);
        styleMenuButton(loadGameButton);

        // Add buttons to panel
        add(newGameButton);
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(saveGameButton);
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(loadGameButton);
        add(Box.createRigidArea(new Dimension(0, 10)));

        // Set initial button states
        setGameInProgress(false);
    }

    /**
     * Enables or disables buttons based on game state.
     * 
     * @param gameInProgress Whether a game is currently in progress
     */
    public void setGameInProgress(boolean gameInProgress) {
        saveGameButton.setEnabled(gameInProgress); // Only enable save when there's a game
        loadGameButton.setEnabled(true); // Always allow loading
        newGameButton.setEnabled(true); // Always allow new game
    }

    /**
     * Updates the visual styling using the current palette and style.
     * 
     * @param style The UI style to apply
     * @param palette The UI palette to apply
     */
    public void updateStyle(UIStyle style, UIPalette palette) {
        setBackground(palette.labelBackground);
        
        // Style all buttons
        style.styleCellButton(newGameButton, true, palette);
        style.styleCellButton(saveGameButton, true, palette);
        style.styleCellButton(loadGameButton, true, palette);
    }

    /**
     * Applies consistent styling to menu buttons.
     * 
     * @param button The button to apply menu styling to
     */
    private void styleMenuButton(JButton button) {
        button.setAlignmentX(CENTER_ALIGNMENT);
        button.setPreferredSize(new Dimension(200, 35));
        button.setMaximumSize(new Dimension(200, 35));
    }
}
