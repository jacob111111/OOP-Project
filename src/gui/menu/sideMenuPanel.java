package gui.menu;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.*;

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
    private JButton newGameButton, saveGameButton, loadGameButton;
    private JLabel gameTitle;
    private ThemeSettingsPanel themeSettingsPanel;
    private JPanel gameControlPanel;
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
            showAIDifficultyDialog();
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
                        JOptionPane.WARNING_MESSAGE);

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
     * Displays the online game mode selection dialog.
     * First asks user to choose between hosting or joining a game.
     * Shows AI difficulty selection dialog.
     */
    private void showAIDifficultyDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "AI Game Setup", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(300, 220);
        dialog.setLocationRelativeTo(this);

        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        settingsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel colorLabel = new JLabel("Your Color:");
        colorLabel.setPreferredSize(new Dimension(100, 25));
        JComboBox<String> colorSelector = new JComboBox<>(new String[] { "White", "Black", "Random" });
        colorPanel.add(colorLabel);
        colorPanel.add(colorSelector);

        JPanel difficultyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel difficultyLabel = new JLabel("AI Difficulty:");
        difficultyLabel.setPreferredSize(new Dimension(100, 25));
        JComboBox<String> difficultySelector = new JComboBox<>(new String[] { "Easy", "Medium", "Hard" });
        difficultySelector.setSelectedIndex(1);
        difficultyPanel.add(difficultyLabel);
        difficultyPanel.add(difficultySelector);

        settingsPanel.add(colorPanel);
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        settingsPanel.add(difficultyPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton startButton = new JButton("Start Game");
        JButton cancelButton = new JButton("Cancel");

        startButton.addActionListener(e -> {
            String selectedColor = (String) colorSelector.getSelectedItem();
            String selectedDifficulty = (String) difficultySelector.getSelectedItem();

            utils.Color playerColor;
            if (selectedColor.equals("White")) {
                playerColor = utils.Color.WHITE;
            } else if (selectedColor.equals("Black")) {
                playerColor = utils.Color.BLACK;
            } else {
                playerColor = utils.Color.RANDOM;
            }

            int difficulty = selectedDifficulty.equals("Easy") ? 1
                    : (selectedDifficulty.equals("Hard") ? 3 : 2);

            dialog.dispose();
            startAIGame(playerColor, difficulty);
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(startButton);
        buttonPanel.add(cancelButton);

        dialog.add(settingsPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    /**
     * Starts a new game against the AI.
     */
    private void startAIGame(utils.Color playerColor, int difficulty) {
        if (parentFrame.getCurrentGame() != null) {
            int result = JOptionPane.showConfirmDialog(
                    this,
                    "This will override the current game. Are you sure?",
                    "Warning",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (result != JOptionPane.YES_OPTION) {
                return;
            }
        }

        game.GUI aiGame = new game.GUI(false, playerColor);
        aiGame.setParentFrame(parentFrame);

        board.Board board = aiGame.getBoard();

        // Determine actual colors after Board resolves RANDOM
        player.Player whitePlayer = board.getPlayer(utils.Color.WHITE);
        player.Player blackPlayer = board.getPlayer(utils.Color.BLACK);

        // Find which player is AI and set difficulty
        final player.AI aiPlayer;
        final utils.Color humanColor;

        if (whitePlayer instanceof player.AI) {
            aiPlayer = (player.AI) whitePlayer;
            humanColor = utils.Color.BLACK;
        } else if (blackPlayer instanceof player.AI) {
            aiPlayer = (player.AI) blackPlayer;
            humanColor = utils.Color.WHITE;
        } else {
            aiPlayer = null;
            humanColor = null;
        }

        if (aiPlayer != null) {
            aiPlayer.setDifficulty(difficulty);
        }

        parentFrame.setGame(aiGame);
        setGameInProgress(true);

        String difficultyName = (difficulty == 1) ? "Easy" : (difficulty == 3 ? "Hard" : "Medium");
        String humanColorName = (humanColor == utils.Color.WHITE) ? "White" : "Black";
        displayMessage("Game Start: Playing as " + humanColorName + " against " + difficultyName + " AI", "info");

        // Initialize turn display to WHITE (always starts first)
        parentFrame.updateTurnDisplay(utils.Color.WHITE);

        // Trigger AI's first move if it's playing as white
        aiGame.triggerAIFirstMoveIfNeeded();
    }

    /**
     * Displays the online game dialog with IP address and port input fields.
     * Shows Host and Join buttons for network gameplay setup.
     */
    private void showOnlineGameDialog() {
        // Create custom dialog for mode selection
        JDialog modeDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Online Game", true);
        modeDialog.setLayout(new BorderLayout());
        modeDialog.setSize(300, 180);
        modeDialog.setLocationRelativeTo(this);

        // Create panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add instruction label
        JLabel instructionLabel = new JLabel("Choose your role:", SwingConstants.CENTER);
        instructionLabel.setAlignmentX(CENTER_ALIGNMENT);
        instructionLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Create buttons
        JButton hostButton = new JButton("Host Game");
        JButton joinButton = new JButton("Join Game");

        // Style buttons
        Dimension buttonSize = new Dimension(200, 40);
        hostButton.setAlignmentX(CENTER_ALIGNMENT);
        hostButton.setMaximumSize(buttonSize);
        joinButton.setAlignmentX(CENTER_ALIGNMENT);
        joinButton.setMaximumSize(buttonSize);

        // Host button action - show host configuration dialog
        hostButton.addActionListener(e -> {
            modeDialog.dispose();
            showHostConfigDialog();
        });

        // Join button action - show join configuration dialog
        joinButton.addActionListener(e -> {
            modeDialog.dispose();
            showJoinConfigDialog();
        });

        // Add components to panel
        buttonPanel.add(instructionLabel);
        buttonPanel.add(hostButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(joinButton);

        modeDialog.add(buttonPanel, BorderLayout.CENTER);
        modeDialog.setVisible(true);
    }

    /**
     * Displays the host configuration dialog with port and color selection.
     */
    private void showHostConfigDialog() {
        // Create custom dialog
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Host Game", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);

        // Create panel for input fields
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        // Port field
        JPanel portPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel portLabel = new JLabel("Port:");
        portLabel.setPreferredSize(new Dimension(80, 25));
        JTextField portField = new JTextField(15);
        portField.setText("8080"); // Default port
        portPanel.add(portLabel);
        portPanel.add(portField);

        // Color selection
        JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel colorLabel = new JLabel("Your Color:");
        colorLabel.setPreferredSize(new Dimension(80, 25));
        JComboBox<String> colorSelector = new JComboBox<>(new String[] { "White", "Black" });
        colorPanel.add(colorLabel);
        colorPanel.add(colorSelector);

        inputPanel.add(portPanel);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        inputPanel.add(colorPanel);

        // Create panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton startButton = new JButton("Start Hosting");
        JButton cancelButton = new JButton("Cancel");

        // Start button action
        startButton.addActionListener(e -> {
            String portText = portField.getText().trim();
            if (portText.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Enter a port number", "Invalid Input",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                int port = Integer.parseInt(portText);
                if (port < 1024 || port > 65535) {
                    JOptionPane.showMessageDialog(dialog, "Port must be between 1024 and 65535!", "Invalid Port",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Get selected color
                String selectedColor = (String) colorSelector.getSelectedItem();
                utils.Color hostColor = selectedColor.equals("White") ? utils.Color.WHITE : utils.Color.BLACK;

                dialog.dispose();
                startHostGame(port, hostColor);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Port must be a valid number!", "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // Cancel button action
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(startButton);
        buttonPanel.add(cancelButton);

        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    /**
     * Displays the join configuration dialog with IP address and port input.
     */
    private void showJoinConfigDialog() {
        // Create custom dialog
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Join Game", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(300, 200);
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
        JTextField portField = new JTextField(12);
        portField.setText("8080"); // Default port
        portPanel.add(portLabel);
        portPanel.add(portField);

        inputPanel.add(ipPanel);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        inputPanel.add(portPanel);

        // Create panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton connectButton = new JButton("Connect");
        JButton cancelButton = new JButton("Cancel");

        // Connect button action
        connectButton.addActionListener(e -> {
            String ip = ipField.getText().trim();
            String portText = portField.getText().trim();

            if (ip.isEmpty() || portText.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter both IP address and port!", "Invalid Input",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                int port = Integer.parseInt(portText);
                if (port < 1024 || port > 65535) {
                    JOptionPane.showMessageDialog(dialog, "Port must be between 1024 and 65535!", "Invalid Port",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                dialog.dispose();
                startClientGame(ip, port);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Port must be a valid number!", "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // Cancel button action
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(connectButton);
        buttonPanel.add(cancelButton);

        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    /**
     * Starts a new network game as the host.
     * 
     * @param port      The port to host the server on
     * @param hostColor The color the host wants to play as
     */
    private void startHostGame(int port, utils.Color hostColor) {
        // Disable board panel while searching
        parentFrame.setBoardEnabled(false);

        // Show waiting dialog with stop button
        JDialog waitDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Hosting Game", false);
        waitDialog.setLayout(new BorderLayout());
        waitDialog.setSize(300, 130);
        waitDialog.setLocationRelativeTo(this);

        JLabel waitLabel = new JLabel("Hosting on port " + port + "... Waiting for opponent...", SwingConstants.CENTER);
        waitDialog.add(waitLabel, BorderLayout.CENTER);

        // Add stop button
        JButton stopButton = new JButton("Stop Searching");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(stopButton);
        waitDialog.add(buttonPanel, BorderLayout.SOUTH);

        waitDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        // Track if cancelled and store server reference for cleanup
        final boolean[] cancelled = { false };
        final player.Server[] serverRef = { null };

        // Create Network game instance as host on a background thread
        Thread hostThread = new Thread(() -> {
            game.Network networkGame = null;
            try {
                // Create server first so we can cancel it
                serverRef[0] = new player.Server(hostColor, port);

                // Check if already cancelled
                if (cancelled[0]) {
                    serverRef[0].close();
                    return;
                }

                // This will block until client connects (or socket is closed)
                serverRef[0].acceptClient();

                // Check if cancelled after accepting
                if (cancelled[0]) {
                    serverRef[0].close();
                    return;
                }

                // Create the network game with the connected server
                networkGame = new game.Network(serverRef[0], hostColor);

                // Connection successful
                final game.Network finalGame = networkGame;
                SwingUtilities.invokeLater(() -> {
                    waitDialog.dispose();
                    parentFrame.setBoardEnabled(true);
                    finalGame.setParentFrame(parentFrame);
                    parentFrame.setGame(finalGame);

                    // Flip board if host is playing as black
                    if (hostColor == utils.Color.BLACK) {
                        parentFrame.flipBoard();
                    }

                    // Initialize turn display to WHITE (always starts first)
                    parentFrame.updateTurnDisplay(utils.Color.WHITE);

                    setGameInProgress(true);
                    
                    // Show connection success message
                    finalGame.showConnectionMessage();
                    
                    JOptionPane.showMessageDialog(this, "Opponent connected! Game started.", "Connected",
                            JOptionPane.INFORMATION_MESSAGE);
                });
            } catch (Exception e) {
                if (!cancelled[0]) {
                    SwingUtilities.invokeLater(() -> {
                        waitDialog.dispose();
                        parentFrame.setBoardEnabled(true);
                        JOptionPane.showMessageDialog(this, "Failed to host game: " + e.getMessage(),
                                "Connection Error", JOptionPane.ERROR_MESSAGE);
                    });
                }
                // Clean up on error
                if (serverRef[0] != null) {
                    serverRef[0].close();
                }
            }
        }, "HostGameThread");

        // Stop button action
        stopButton.addActionListener(e -> {
            cancelled[0] = true;
            waitDialog.dispose();
            parentFrame.setBoardEnabled(true);
            // Close the server socket to unblock acceptClient()
            if (serverRef[0] != null) {
                serverRef[0].close();
            }
            hostThread.interrupt();
        });

        hostThread.start();
        waitDialog.setVisible(true);
    }

    /**
     * Starts a new network game as the client.
     * 
     * @param serverIP The IP address of the host server
     * @param port     The port the server is hosted on
     */
    private void startClientGame(String serverIP, int port) {
        // Disable board panel while searching
        parentFrame.setBoardEnabled(false);

        // Show connecting dialog with stop button
        JDialog waitDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Joining Game", false);
        waitDialog.setLayout(new BorderLayout());
        waitDialog.setSize(300, 130);
        waitDialog.setLocationRelativeTo(this);

        JLabel waitLabel = new JLabel("Connecting to " + serverIP + ":" + port + "...", SwingConstants.CENTER);
        waitDialog.add(waitLabel, BorderLayout.CENTER);

        // Add stop button
        JButton stopButton = new JButton("Stop Searching");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(stopButton);
        waitDialog.add(buttonPanel, BorderLayout.SOUTH);

        waitDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        // Track if cancelled
        final boolean[] cancelled = { false };

        // Create Network game instance as client on a background thread
        Thread clientThread = new Thread(() -> {
            try {
                // Client's color will be assigned by server
                game.Network networkGame = new game.Network(true, serverIP, port);

                // Check if cancelled before proceeding
                if (!cancelled[0]) {
                    // Update GUI on Swing thread after connection
                    SwingUtilities.invokeLater(() -> {
                        waitDialog.dispose();
                        parentFrame.setBoardEnabled(true);
                        networkGame.setParentFrame(parentFrame);
                        parentFrame.setGame(networkGame);

                        // Flip board if client is playing as black
                        if (networkGame.getMyColor() == utils.Color.BLACK) {
                            parentFrame.flipBoard();
                        }

                        // Initialize turn display to WHITE (always starts first)
                        parentFrame.updateTurnDisplay(utils.Color.WHITE);

                        setGameInProgress(true);
                        
                        // Show connection success message
                        networkGame.showConnectionMessage();
                        
                        JOptionPane.showMessageDialog(this, "Connected to server! Game started.", "Connected",
                                JOptionPane.INFORMATION_MESSAGE);
                    });
                } else {
                    // Was cancelled, clean up
                    networkGame.stopNetworkListener();
                }
            } catch (Exception e) {
                if (!cancelled[0]) {
                    SwingUtilities.invokeLater(() -> {
                        waitDialog.dispose();
                        parentFrame.setBoardEnabled(true);
                        JOptionPane.showMessageDialog(this, "Failed to connect: " + e.getMessage(), "Connection Error",
                                JOptionPane.ERROR_MESSAGE);
                    });
                }
            }
        }, "ClientGameThread");

        // Stop button action
        stopButton.addActionListener(e -> {
            cancelled[0] = true;
            waitDialog.dispose();
            parentFrame.setBoardEnabled(true);
            clientThread.interrupt();
        });

        clientThread.start();
        waitDialog.setVisible(true);
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
        // Game control buttons
        saveGameButton.setEnabled(gameInProgress); // Only enable save when there's a game
        loadGameButton.setEnabled(true); // Always allow loading
        newGameButton.setEnabled(true); // Always allow new game
        
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

        // Style all buttons
        style.styleCellButton(newGameButton, true, palette);
        style.styleCellButton(saveGameButton, true, palette);
        style.styleCellButton(loadGameButton, true, palette);

        // Style panels
        gameControlPanel.setBackground(palette.labelBackground);
        
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
