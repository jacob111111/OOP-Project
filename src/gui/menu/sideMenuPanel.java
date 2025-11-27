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
    private JButton newGameButton, saveGameButton, loadGameButton;
    private JLabel gameTitle, themeLabel, pieceThemeLabel;
    private JComboBox<String> themeSelector, pieceThemeSelector;
    private JPanel themePanel, gameControlPanel;

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
        
        // Game control panel for new/save/load
        gameControlPanel = new JPanel();
        gameControlPanel.setLayout(new BoxLayout(gameControlPanel, BoxLayout.Y_AXIS));
        
        
        // Game control buttons
        newGameButton = new JButton("New Game");
        saveGameButton = new JButton("Save Game");
        loadGameButton = new JButton("Load Game");
        
        
        // Add action listeners for game controls
        newGameButton.addActionListener(e -> handleNewGame());
        saveGameButton.addActionListener(e -> handleSaveGame());
        loadGameButton.addActionListener(e -> handleLoadGame());
        
        // Style buttons
        styleMenuButton(newGameButton);
        styleMenuButton(saveGameButton);
        styleMenuButton(loadGameButton);
        
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
        
        // Add components to main panel
        add(gameTitle, BorderLayout.NORTH);
        add(gameControlPanel, BorderLayout.CENTER);
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
                "This will override the current game. Are you sure?",
                "New Game",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
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
     * Creates a dialog with three buttons: 1-Player (AI), Co-op, and Online.
     * Each button triggers relevant game initialization logic.
     */
    private void showGameModeSelection() {
        // Create custom dialog
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "New Game", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);
        
        // Create panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create buttons
        JButton aiButton = new JButton("1-Player (AI)");
        JButton coopButton = new JButton("Co-op");
        JButton onlineButton = new JButton("Online");
        
        // Style buttons
        Dimension buttonSize = new Dimension(200, 40);
        aiButton.setAlignmentX(CENTER_ALIGNMENT);
        aiButton.setMaximumSize(buttonSize);
        coopButton.setAlignmentX(CENTER_ALIGNMENT);
        coopButton.setMaximumSize(buttonSize);
        onlineButton.setAlignmentX(CENTER_ALIGNMENT);
        onlineButton.setMaximumSize(buttonSize);
        
        // AI button action
        aiButton.addActionListener(e -> {
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "AI mode not yet implemented!", "Not Implemented", JOptionPane.INFORMATION_MESSAGE);
        });
        
        // Co-op button action
        coopButton.addActionListener(e -> {
            dialog.dispose();
            // Check if game is currently active
            if (parentFrame.getCurrentGame() != null) {
                int result = JOptionPane.showConfirmDialog(
                    this,
                    "This will override the current game. Are you sure?",
                    "Warning",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );
                
                if (result == JOptionPane.YES_OPTION) {
                    parentFrame.startTwoPlayerGame();
                    setGameInProgress(true);
                }
            } else {
                // No game active, create new game
                parentFrame.startTwoPlayerGame();
                setGameInProgress(true);
            }
        });
        
        // Online button action
        onlineButton.addActionListener(e -> {
            dialog.dispose();
            showOnlineGameDialog();
        });
        
        // Add buttons to panel
        buttonPanel.add(aiButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(coopButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(onlineButton);
        
        dialog.add(buttonPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    /**
     * Displays the online game dialog with IP address and port input fields.
     * Shows Host and Join buttons for network gameplay setup.
     */
    private void showOnlineGameDialog() {
        // Create custom dialog
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Online Game", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(350, 250);
        dialog.setLocationRelativeTo(this);
        
        // Create panel for input fields
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
        // IP Address field
        JPanel ipPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel ipLabel = new JLabel("IP Address:");
        ipLabel.setPreferredSize(new Dimension(80, 25));
        JTextField ipField = new JTextField(15);
        ipPanel.add(ipLabel);
        ipPanel.add(ipField);
        
        // Port field
        JPanel portPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel portLabel = new JLabel("Port:");
        portLabel.setPreferredSize(new Dimension(80, 25));
        JTextField portField = new JTextField(15);
        portField.setText("8080"); // Default port
        portPanel.add(portLabel);
        portPanel.add(portField);
        
        // Color selection (for host)
        JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel colorLabel = new JLabel("Your Color:");
        colorLabel.setPreferredSize(new Dimension(80, 25));
        JComboBox<String> colorSelector = new JComboBox<>(new String[]{"White", "Black"});
        colorPanel.add(colorLabel);
        colorPanel.add(colorSelector);
        
        inputPanel.add(ipPanel);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        inputPanel.add(portPanel);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        inputPanel.add(colorPanel);
        
        // Create panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton hostButton = new JButton("Host");
        JButton joinButton = new JButton("Join");
        
        // Host button action
        hostButton.addActionListener(e -> {
            String portText = portField.getText().trim();
            if (portText.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Enter a port number", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                int port = Integer.parseInt(portText);
                if (port < 1024 || port > 65535) {
                    JOptionPane.showMessageDialog(dialog, "Port must be between 1024 and 65535!", "Invalid Port", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Get selected color
                String selectedColor = (String) colorSelector.getSelectedItem();
                utils.Color hostColor = selectedColor.equals("White") ? utils.Color.WHITE : utils.Color.BLACK;
                
                dialog.dispose();
                startHostGame(port, hostColor);
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Port must be a valid number!", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Join button action
        joinButton.addActionListener(e -> {
            String ip = ipField.getText().trim();
            String portText = portField.getText().trim();
            
            if (ip.isEmpty() || portText.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter both IP address and port!", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                int port = Integer.parseInt(portText);
                if (port < 1024 || port > 65535) {
                    JOptionPane.showMessageDialog(dialog, "Port must be between 1024 and 65535!", "Invalid Port", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                dialog.dispose();
                startClientGame(ip, port);
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Port must be a valid number!", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        buttonPanel.add(hostButton);
        buttonPanel.add(joinButton);
        
        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    /**
     * Starts a new network game as the host.
     * 
     * @param port The port to host the server on
     * @param hostColor The color the host wants to play as
     */
    private void startHostGame(int port, utils.Color hostColor) {
        JOptionPane.showMessageDialog(this, 
            "Hosting on port " + port + " as " + (hostColor == utils.Color.WHITE ? "White" : "Black") + "\nWaiting for opponent...", 
            "Hosting Game", 
            JOptionPane.INFORMATION_MESSAGE);
        
        // TODO: Create Network game instance as host
        // game.Network networkGame = new game.Network(true, hostColor, port);
        // networkGame.setParentFrame(parentFrame);
        // parentFrame.setGame(networkGame);
        // setGameInProgress(true);
    }
    
    /**
     * Starts a new network game as the client.
     * 
     * @param serverIP The IP address of the host server
     * @param port The port the server is hosted on
     */
    private void startClientGame(String serverIP, int port) {
        JOptionPane.showMessageDialog(this, 
            "Connecting to " + serverIP + ":" + port + "...", 
            "Joining Game", 
            JOptionPane.INFORMATION_MESSAGE);
        
        // TODO: Create Network game instance as client
        // Client's color will be assigned by server (opposite of host)
        // For now, pass WHITE as placeholder - will be updated by server
        // game.Network networkGame = new game.Network(true, utils.Color.WHITE, serverIP, port);
        // networkGame.setParentFrame(parentFrame);
        // parentFrame.setGame(networkGame);
        // setGameInProgress(true);
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
        // Game control buttons
        saveGameButton.setEnabled(gameInProgress); // Only enable save when there's a game
        loadGameButton.setEnabled(true); // Always allow loading
        newGameButton.setEnabled(true); // Always allow new game
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
        style.styleCellButton(newGameButton, true, palette);
        style.styleCellButton(saveGameButton, true, palette);
        style.styleCellButton(loadGameButton, true, palette);
        
        // Style panels
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
