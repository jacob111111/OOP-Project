package gui.menu;

import java.awt.FlowLayout;
import javax.swing.*;

import gui.ChessFrame;
import gui.utils.UIPalette;

/**
 * Panel for theme selection settings.
 * 
 * This panel provides controls for selecting the board theme and piece theme.
 */
public class ThemeSettingsPanel extends JPanel {
    private JLabel themeLabel, pieceThemeLabel;
    private JComboBox<String> themeSelector, pieceThemeSelector;
    private ChessFrame parentFrame;

    /**
     * Creates a new theme settings panel.
     * 
     * @param parentFrame The parent chess frame for theme change callbacks
     */
    public ThemeSettingsPanel(ChessFrame parentFrame) {
        this.parentFrame = parentFrame;
        initializeComponents();
    }

    /**
     * Initializes the theme selection UI components.
     */
    private void initializeComponents() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5),
            BorderFactory.createLineBorder(java.awt.Color.GRAY, 1)
        ));

        // Board theme selection
        JPanel boardThemePanel = new JPanel(new FlowLayout());
        themeLabel = new JLabel("Board Theme:");
        themeSelector = new JComboBox<>(new String[] { "Classic", "Modern" });
        themeSelector.addActionListener(e -> {
            String selectedTheme = (String) themeSelector.getSelectedItem();
            parentFrame.changeTheme(selectedTheme);
        });

        boardThemePanel.add(themeLabel);
        boardThemePanel.add(themeSelector);

        // Piece theme selection
        JPanel pieceThemePanel = new JPanel(new FlowLayout());
        pieceThemeLabel = new JLabel("Piece Theme:");
        pieceThemeSelector = new JComboBox<>(new String[] { "Classic", "Modern" });
        pieceThemeSelector.addActionListener(e -> {
            String selectedPieceTheme = (String) pieceThemeSelector.getSelectedItem();
            parentFrame.changePieceTheme(selectedPieceTheme);
        });

        pieceThemePanel.add(pieceThemeLabel);
        pieceThemePanel.add(pieceThemeSelector);

        // Add both theme panels
        add(boardThemePanel);
        add(pieceThemePanel);
    }

    /**
     * Updates the visual styling using the current palette.
     * 
     * @param palette The UI palette to apply
     */
    public void updateStyle(UIPalette palette) {
        setBackground(palette.labelBackground);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5),
            BorderFactory.createLineBorder(palette.labelForeground, 1)
        ));

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
    }
}
