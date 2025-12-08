package gui.menu;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.*;

import gui.ChessFrame;

/**
 * Dialog for selecting the game mode (1-Player AI, Co-op, or Online).
 * 
 * This dialog presents three options for starting a new game and handles
 * the confirmation logic for overriding an existing game.
 */
public class GameModeDialog {
    
    /**
     * Game mode options available to the player.
     */
    public enum GameMode {
        AI,
        COOP,
        ONLINE,
        CANCELLED
    }
    
    /**
     * Shows the game mode selection dialog.
     * 
     * @param parent The parent component for the dialog
     * @param parentFrame The chess frame reference
     * @param onAISelected Callback when AI mode is selected
     * @param onOnlineSelected Callback when Online mode is selected
     */
    public static void show(Component parent, ChessFrame parentFrame, 
                           Runnable onAISelected, Runnable onOnlineSelected) {
        // Create custom dialog
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(parent), "New Game", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(parent);

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
        aiButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        aiButton.setMaximumSize(buttonSize);
        coopButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        coopButton.setMaximumSize(buttonSize);
        onlineButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        onlineButton.setMaximumSize(buttonSize);

        // AI button action
        aiButton.addActionListener(e -> {
            dialog.dispose();
            onAISelected.run();
        });

        // Co-op button action
        coopButton.addActionListener(e -> {
            dialog.dispose();
            handleCoopSelection(parent, parentFrame);
        });

        // Online button action
        onlineButton.addActionListener(e -> {
            dialog.dispose();
            onOnlineSelected.run();
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
     * Handles Co-op mode selection with confirmation if game exists.
     * 
     * @param parent The parent component for dialogs
     * @param parentFrame The chess frame reference
     */
    private static void handleCoopSelection(Component parent, ChessFrame parentFrame) {
        // Check if game is currently active
        if (parentFrame.getCurrentGame() != null) {
            int result = JOptionPane.showConfirmDialog(
                    parent,
                    "This will override the current game. Are you sure?",
                    "Warning",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (result == JOptionPane.YES_OPTION) {
                parentFrame.startTwoPlayerGame();
                // Notify parent to update game state
                if (parent instanceof SideMenuPanel) {
                    ((SideMenuPanel) parent).setGameInProgress(true);
                }
            }
        } else {
            // No game active, create new game
            parentFrame.startTwoPlayerGame();
            // Notify parent to update game state
            if (parent instanceof SideMenuPanel) {
                ((SideMenuPanel) parent).setGameInProgress(true);
            }
        }
    }
}
