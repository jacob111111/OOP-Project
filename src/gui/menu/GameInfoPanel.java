package gui.menu;

import java.awt.Dimension;
import javax.swing.*;

import gui.utils.UIPalette;

/**
 * Panel that displays game information including turn indicator and hover info.
 * 
 * This panel shows the current player's turn and information about pieces
 * being hovered over on the board.
 */
public class GameInfoPanel extends JPanel {
    private JLabel hoverInfoLabel;
    private JLabel turnIndicatorLabel;
    private MessageBoardPanel messageBoardPanel;

    /**
     * Creates a new game info panel with the provided message board.
     * 
     * @param messageBoardPanel The message board to include in this panel
     */
    public GameInfoPanel(MessageBoardPanel messageBoardPanel) {
        this.messageBoardPanel = messageBoardPanel;
        initializeComponents();
    }

    /**
     * Initializes the UI components.
     */
    private void initializeComponents() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Turn indicator label (hidden by default, maintains spacing)
        turnIndicatorLabel = new JLabel("Current Player: WHITE", SwingConstants.CENTER);
        turnIndicatorLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 18));
        turnIndicatorLabel.setForeground(java.awt.Color.WHITE);
        turnIndicatorLabel.setAlignmentX(CENTER_ALIGNMENT);
        turnIndicatorLabel.setVisible(false); // Hidden until game starts
        turnIndicatorLabel.setPreferredSize(new Dimension(280, 35));
        turnIndicatorLabel.setMaximumSize(new Dimension(280, 35));

        // Hover info label
        hoverInfoLabel = new JLabel(" ", SwingConstants.CENTER);
        hoverInfoLabel.setAlignmentX(CENTER_ALIGNMENT);
        hoverInfoLabel.setPreferredSize(new Dimension(200, 25));
        hoverInfoLabel.setMaximumSize(new Dimension(200, 25));

        // Add components
        add(turnIndicatorLabel);
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(hoverInfoLabel);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(messageBoardPanel);
    }

    /**
     * Updates the hover info label to show the currently hovered piece.
     * 
     * @param pieceName  The name of the piece being hovered over (e.g., "King",
     *                   "Queen")
     * @param pieceColor The color of the piece ("White" or "Black")
     */
    public void updateHoverInfo(String pieceName, String pieceColor) {
        if (pieceName != null && pieceColor != null) {
            hoverInfoLabel.setText("Hovering: " + pieceColor + " " + pieceName);
        } else {
            hoverInfoLabel.setText(" ");
        }
    }

    /**
     * Clears the hover info label.
     */
    public void clearHoverInfo() {
        hoverInfoLabel.setText(" ");
    }

    /**
     * Updates the turn indicator to show whose turn it is.
     * 
     * @param currentTurn The color of the player whose turn it is
     */
    public void updateTurnIndicator(utils.Color currentTurn) {
        if (turnIndicatorLabel != null) {
            String turnText = (currentTurn == utils.Color.WHITE) ? "Current Player: WHITE" : "Current Player: BLACK";
            java.awt.Color textColor = (currentTurn == utils.Color.WHITE) ? java.awt.Color.WHITE : java.awt.Color.BLACK;

            turnIndicatorLabel.setText(turnText);
            turnIndicatorLabel.setForeground(textColor);
            turnIndicatorLabel.setVisible(true); // Show when game is active
        }
    }

    /**
     * Sets the visibility of the turn indicator based on game state.
     * 
     * @param gameInProgress Whether a game is currently in progress
     */
    public void setGameInProgress(boolean gameInProgress) {
        if (turnIndicatorLabel != null) {
            turnIndicatorLabel.setVisible(gameInProgress);
        }
    }

    /**
     * Updates the visual styling using the current palette.
     * 
     * @param palette The UI palette to apply
     */
    public void updateStyle(UIPalette palette) {
        setBackground(palette.labelBackground);

        // Style turn indicator and hover info labels
        turnIndicatorLabel.setFont(palette.font.deriveFont(java.awt.Font.BOLD, 16f));

        hoverInfoLabel.setFont(palette.font);
        hoverInfoLabel.setForeground(palette.labelForeground);
    }
}
