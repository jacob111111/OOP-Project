package gui.menu;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.*;

import gui.ChessFrame;

/**
 * Dialog for configuring AI game settings (player color and AI difficulty).
 * 
 * This dialog allows the user to select their color and the AI difficulty level
 * before starting a game against the computer.
 */
public class AIDifficultyDialog {
    
    /**
     * Shows the AI difficulty configuration dialog and starts an AI game.
     * 
     * @param parent The parent component for the dialog
     * @param parentFrame The chess frame reference for game management
     */
    public static void show(Component parent, ChessFrame parentFrame) {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(parent), "AI Game Setup", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(300, 220);
        dialog.setLocationRelativeTo(parent);

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
            startAIGame(parent, parentFrame, playerColor, difficulty);
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(startButton);
        buttonPanel.add(cancelButton);

        dialog.add(settingsPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    /**
     * Starts a new game against the AI with the specified settings.
     * 
     * @param parent The parent component for dialogs
     * @param parentFrame The chess frame reference
     * @param playerColor The color the human player will use
     * @param difficulty The AI difficulty level (1=Easy, 2=Medium, 3=Hard)
     */
    private static void startAIGame(Component parent, ChessFrame parentFrame, 
                                    utils.Color playerColor, int difficulty) {
        if (parentFrame.getCurrentGame() != null) {
            int result = JOptionPane.showConfirmDialog(
                    parent,
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
        
        // Update game state in SideMenuPanel
        if (parent instanceof SideMenuPanel) {
            SideMenuPanel sideMenu = (SideMenuPanel) parent;
            sideMenu.setGameInProgress(true);
            
            String difficultyName = (difficulty == 1) ? "Easy" : (difficulty == 3 ? "Hard" : "Medium");
            String humanColorName = (humanColor == utils.Color.WHITE) ? "White" : "Black";
            sideMenu.displayMessage("Game Start: Playing as " + humanColorName + " against " + difficultyName + " AI", "info");
        }

        // Initialize turn display to WHITE (always starts first)
        parentFrame.updateTurnDisplay(utils.Color.WHITE);

        // Trigger AI's first move if it's playing as white
        aiGame.triggerAIFirstMoveIfNeeded();
    }
}
