package gui.menu;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.*;

import gui.ChessFrame;

/**
 * Dialog for setting up online multiplayer games.
 * 
 * This class handles all aspects of online game setup including:
 * - Choosing to host or join a game
 * - Host configuration (port and color selection)
 * - Join configuration (IP address and port input)
 * - Network connection management with cancellation support
 */
public class OnlineGameDialog {
    
    /**
     * Shows the online game mode selection dialog.
     * 
     * @param parent The parent component for the dialog
     * @param parentFrame The chess frame reference
     */
    public static void show(Component parent, ChessFrame parentFrame) {
        // Create custom dialog for mode selection
        JDialog modeDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(parent), "Online Game", true);
        modeDialog.setLayout(new BorderLayout());
        modeDialog.setSize(300, 180);
        modeDialog.setLocationRelativeTo(parent);

        // Create panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add instruction label
        JLabel instructionLabel = new JLabel("Choose your role:", SwingConstants.CENTER);
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        instructionLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Create buttons
        JButton hostButton = new JButton("Host Game");
        JButton joinButton = new JButton("Join Game");

        // Style buttons
        Dimension buttonSize = new Dimension(200, 40);
        hostButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        hostButton.setMaximumSize(buttonSize);
        joinButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        joinButton.setMaximumSize(buttonSize);

        // Host button action - show host configuration dialog
        hostButton.addActionListener(e -> {
            modeDialog.dispose();
            showHostConfigDialog(parent, parentFrame);
        });

        // Join button action - show join configuration dialog
        joinButton.addActionListener(e -> {
            modeDialog.dispose();
            showJoinConfigDialog(parent, parentFrame);
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
     * 
     * @param parent The parent component for the dialog
     * @param parentFrame The chess frame reference
     */
    private static void showHostConfigDialog(Component parent, ChessFrame parentFrame) {
        // Create custom dialog
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(parent), "Host Game", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(parent);

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
                startHostGame(parent, parentFrame, port, hostColor);

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
     * 
     * @param parent The parent component for the dialog
     * @param parentFrame The chess frame reference
     */
    private static void showJoinConfigDialog(Component parent, ChessFrame parentFrame) {
        // Create custom dialog
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(parent), "Join Game", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(parent);

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
                startClientGame(parent, parentFrame, ip, port);

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
     * @param parent The parent component for dialogs
     * @param parentFrame The chess frame reference
     * @param port The port to host the server on
     * @param hostColor The color the host wants to play as
     */
    private static void startHostGame(Component parent, ChessFrame parentFrame, int port, utils.Color hostColor) {
        // Disable board panel while searching
        parentFrame.setBoardEnabled(false);

        // Show waiting dialog with stop button
        JDialog waitDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(parent), "Hosting Game", false);
        waitDialog.setLayout(new BorderLayout());
        waitDialog.setSize(300, 130);
        waitDialog.setLocationRelativeTo(parent);

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

                    if (parent instanceof SideMenuPanel) {
                        ((SideMenuPanel) parent).setGameInProgress(true);
                    }
                    
                    // Show connection success message
                    finalGame.showConnectionMessage();
                    
                    JOptionPane.showMessageDialog(parent, "Opponent connected! Game started.", "Connected",
                            JOptionPane.INFORMATION_MESSAGE);
                });
            } catch (Exception e) {
                if (!cancelled[0]) {
                    SwingUtilities.invokeLater(() -> {
                        waitDialog.dispose();
                        parentFrame.setBoardEnabled(true);
                        JOptionPane.showMessageDialog(parent, "Failed to host game: " + e.getMessage(),
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
     * @param parent The parent component for dialogs
     * @param parentFrame The chess frame reference
     * @param serverIP The IP address of the host server
     * @param port The port the server is hosted on
     */
    private static void startClientGame(Component parent, ChessFrame parentFrame, String serverIP, int port) {
        // Disable board panel while searching
        parentFrame.setBoardEnabled(false);

        // Show connecting dialog with stop button
        JDialog waitDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(parent), "Joining Game", false);
        waitDialog.setLayout(new BorderLayout());
        waitDialog.setSize(300, 130);
        waitDialog.setLocationRelativeTo(parent);

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

                        if (parent instanceof SideMenuPanel) {
                            ((SideMenuPanel) parent).setGameInProgress(true);
                        }
                        
                        // Show connection success message
                        networkGame.showConnectionMessage();
                        
                        JOptionPane.showMessageDialog(parent, "Connected to server! Game started.", "Connected",
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
                        JOptionPane.showMessageDialog(parent, "Failed to connect: " + e.getMessage(), "Connection Error",
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
}
