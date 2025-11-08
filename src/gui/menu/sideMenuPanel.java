package gui.menu;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import gui.chessFrame;
import gui.utils.UIPalette;
import gui.utils.UIStyle;

/**
 * Side panel containing game controls, mode selection, and settings.
 * 
 * This panel provides the main interface for starting games, managing
 * game state (save/load), and configuring application settings. It remains
 * visible alongside the chess board during gameplay.
 */
public class sideMenuPanel extends JPanel {
    private chessFrame parentFrame;
    private JButton aiButton, twoPlayerButton, newGameButton, saveGameButton, loadGameButton;
    private JLabel gameTitle, themeLabel, pieceThemeLabel;
    private JComboBox<String> themeSelector, pieceThemeSelector;
    private JPanel buttonPanel, themePanel, gameControlPanel;

    /**
     * Creates a new side menu panel with the specified parent frame.
     * 
     * @param parent The parent chess frame that contains this panel
     */
    public sideMenuPanel(chessFrame parent) {
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
        
        // Button panel for game modes
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        
        // Game control panel for new/save/load
        gameControlPanel = new JPanel();
        gameControlPanel.setLayout(new BoxLayout(gameControlPanel, BoxLayout.Y_AXIS));
        
        // Game mode buttons
        aiButton = new JButton("1-Player (AI)");
        twoPlayerButton = new JButton("2-Player");
        
        // Game control buttons
        newGameButton = new JButton("New Game");
        saveGameButton = new JButton("Save Game");
        loadGameButton = new JButton("Load Game");
        
        // Add action listeners for game modes
        aiButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "AI mode not yet implemented!");
        });
        
        twoPlayerButton.addActionListener(e -> {
            parentFrame.startTwoPlayerGame();
            setGameInProgress(true);
        });
        
        // Add action listeners for game controls
        newGameButton.addActionListener(e -> handleNewGame());
        saveGameButton.addActionListener(e -> handleSaveGame());
        loadGameButton.addActionListener(e -> handleLoadGame());
        
        // Style buttons
        styleMenuButton(aiButton);
        styleMenuButton(twoPlayerButton);
        styleMenuButton(newGameButton);
        styleMenuButton(saveGameButton);
        styleMenuButton(loadGameButton);
        
        // Add game mode buttons to panel
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(aiButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(twoPlayerButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Add game control buttons to panel
        gameControlPanel.add(newGameButton);
        gameControlPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        gameControlPanel.add(saveGameButton);
        gameControlPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        gameControlPanel.add(loadGameButton);
        gameControlPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Theme selection panel
        themePanel = new JPanel();
        themePanel.setLayout(new BoxLayout(themePanel, BoxLayout.Y_AXIS));
        
        // Board theme selection
        JPanel boardThemePanel = new JPanel(new FlowLayout());
        themeLabel = new JLabel("Board Theme:");
        themeSelector = new JComboBox<>(new String[]{"Classic", "Modern"});
        themeSelector.addActionListener(e -> {
            String selectedTheme = (String) themeSelector.getSelectedItem();
            parentFrame.changeTheme(selectedTheme);
        });
        
        boardThemePanel.add(themeLabel);
        boardThemePanel.add(themeSelector);
        
        // Piece theme selection
        JPanel pieceThemePanel = new JPanel(new FlowLayout());
        pieceThemeLabel = new JLabel("Piece Theme:");
        pieceThemeSelector = new JComboBox<>(new String[]{"Classic", "Modern"});
        pieceThemeSelector.addActionListener(e -> {
            String selectedPieceTheme = (String) pieceThemeSelector.getSelectedItem();
            parentFrame.changePieceTheme(selectedPieceTheme);
        });
        
        pieceThemePanel.add(pieceThemeLabel);
        pieceThemePanel.add(pieceThemeSelector);
        
        // Add both theme panels to main theme panel
        themePanel.add(boardThemePanel);
        themePanel.add(pieceThemePanel);
        
        // Combine button panels
        JPanel allButtonsPanel = new JPanel();
        allButtonsPanel.setLayout(new BoxLayout(allButtonsPanel, BoxLayout.Y_AXIS));
        allButtonsPanel.add(buttonPanel);
        allButtonsPanel.add(gameControlPanel);
        
        // Add components to main panel
        add(gameTitle, BorderLayout.NORTH);
        add(allButtonsPanel, BorderLayout.CENTER);
        add(themePanel, BorderLayout.SOUTH);
        
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
                "This will reset the current game. Are you sure?",
                "New Game",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
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
     * 
     * Presents options for 2-player or AI mode and starts the selected
     * game type. Currently AI mode shows a not-implemented message.
     */
    private void showGameModeSelection() {
        String[] options = {"2-Player", "1-Player (AI)", "Cancel"};
        int choice = JOptionPane.showOptionDialog(
            this,
            "Select game mode:",
            "New Game",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
        
        switch (choice) {
            case 0: // 2-Player
                parentFrame.startTwoPlayerGame();
                setGameInProgress(true);
                break;
            case 1: // 1-Player (AI)
                JOptionPane.showMessageDialog(this, "AI mode not yet implemented!");
                break;
            case 2: // Cancel
            default:
                // Do nothing
                break;
        }
    }
    
    /**
     * Handles saving the current game state to a file.
     * 
     * Opens a file chooser dialog and serializes the current game
     * object to the selected file. Shows error messages if save fails.
     */
    private void handleSaveGame() {
        if (parentFrame.getCurrentGame() == null) {
            JOptionPane.showMessageDialog(this, "No game to save!", "Save Game", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Game");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Chess Game Files (*.chess)", "chess"));
        fileChooser.setSelectedFile(new File("game.chess"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().endsWith(".chess")) {
                file = new File(file.getAbsolutePath() + ".chess");
            }
            
            try {
                saveGameToFile(file);
                JOptionPane.showMessageDialog(this, "Game saved successfully!", "Save Game", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Failed to save game: " + e.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Handles loading a game state from a file.
     * 
     * Opens a file chooser dialog and deserializes a game object
     * from the selected file. Replaces current game if one exists.
     */
    private void handleLoadGame() {
        if (parentFrame.getCurrentGame() != null) {
            int result = JOptionPane.showConfirmDialog(
                this,
                "This will replace the current game. Are you sure?",
                "Load Game",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (result != JOptionPane.YES_OPTION) {
                return;
            }
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Game");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Chess Game Files (*.chess)", "chess"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            
            try {
                loadGameFromFile(file);
                setGameInProgress(true);
                JOptionPane.showMessageDialog(this, "Game loaded successfully!", "Load Game", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(this, "Failed to load game: " + e.getMessage(), "Load Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Serializes and saves the current game to the specified file.
     * 
     * @param file The file to save the game state to
     * @throws IOException if file writing fails
     */
    private void saveGameToFile(File file) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            // Save the current game state
            oos.writeObject(parentFrame.getCurrentGame());
        }
    }
    
    /**
     * Deserializes and loads a game from the specified file.
     * 
     * @param file The file to load the game state from
     * @throws IOException if file reading fails
     * @throws ClassNotFoundException if game class cannot be found
     */
    private void loadGameFromFile(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            // Load the game state
            game.GUI loadedGame = (game.GUI) ois.readObject();
            parentFrame.setGame(loadedGame);
        }
    }

    /**
     * Enables or disables buttons based on game state
     */
    public void setGameInProgress(boolean gameInProgress) {
        // Game mode buttons - disable when game is active
        aiButton.setEnabled(!gameInProgress);
        twoPlayerButton.setEnabled(!gameInProgress);
        
        // Game control buttons
        saveGameButton.setEnabled(gameInProgress); // Only enable save when there's a game
        loadGameButton.setEnabled(true); // Always allow loading
        newGameButton.setEnabled(true); // Always allow new game
        
        // Update button text to show state
        if (gameInProgress) {
            aiButton.setText("1-Player (Game Active)");
            twoPlayerButton.setText("2-Player (Game Active)");
        } else {
            aiButton.setText("1-Player (AI)");
            twoPlayerButton.setText("2-Player");
        }
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
        
        // Style the main panel
        style.styleLabelPanel(this, palette, "Game Menu");
        
        // Style all buttons
        style.styleCellButton(aiButton, true, palette);
        style.styleCellButton(twoPlayerButton, true, palette);
        style.styleCellButton(newGameButton, true, palette);
        style.styleCellButton(saveGameButton, true, palette);
        style.styleCellButton(loadGameButton, true, palette);
        
        // Style panels
        style.styleLabelPanel(buttonPanel, palette, "Game Modes");
        style.styleLabelPanel(gameControlPanel, palette, "Game Controls");
        style.styleLabelPanel(themePanel, palette, "Settings");
        
        // Style labels and components
        gameTitle.setFont(palette.font);
        gameTitle.setForeground(palette.labelForeground);
        
        themeLabel.setFont(palette.font);
        themeLabel.setForeground(palette.labelForeground);
        
        pieceThemeLabel.setFont(palette.font);
        pieceThemeLabel.setForeground(palette.labelForeground);
        
        themeSelector.setFont(palette.font);
        themeSelector.setForeground(palette.labelForeground);
        themeSelector.setBackground(palette.labelBackground);
        
        pieceThemeSelector.setFont(palette.font);
        pieceThemeSelector.setForeground(palette.labelForeground);
        pieceThemeSelector.setBackground(palette.labelBackground);
        
        repaint();
        revalidate();
    }

    /**
     * Applies consistent styling to menu buttons.
     * 
     * Sets standard dimensions and alignment properties for buttons
     * used in the menu interface.
     * 
     * @param button The button to apply menu styling to
     */
    private void styleMenuButton(JButton button) {
        button.setAlignmentX(CENTER_ALIGNMENT);
        button.setPreferredSize(new Dimension(200, 35)); // Slightly smaller for more buttons
        button.setMaximumSize(new Dimension(200, 35));
    }
}
