package gui.utils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Utility class for applying consistent styling across the chess game GUI.
 * 
 * This class provides methods to apply standardized visual styling to various
 * UI components using a specified color palette. It ensures visual consistency
 * throughout the application by centralizing styling logic.
 */
public class UIStyle {
    
    /**
     * Applies consistent styling to chess board cell buttons.
     * 
     * Configures the appearance of individual board square buttons including
     * background colors, borders, fonts, and layout properties. The styling
     * differentiates between light and dark squares using the provided palette.
     * 
     * @param cellButton The button representing a chess board square
     * @param isLight true for light squares, false for dark squares
     * @param palette The color palette to use for styling
     */
    public void styleCellButton(JButton cellButton, boolean isLight, UIPalette palette) {
        cellButton.setBackground(isLight ? palette.boardCellLight : palette.boardCellDark);
        cellButton.setForeground(palette.labelForeground);
        cellButton.setFont(palette.font);

        cellButton.setBorder(BorderFactory.createLineBorder(palette.borderColor, 2));
        cellButton.setMargin(new Insets(2, 2, 2, 2));
        cellButton.setFocusPainted(false);

        cellButton.setHorizontalTextPosition(SwingConstants.CENTER);
        cellButton.setVerticalTextPosition(SwingConstants.BOTTOM);
    }

    /**
     * Applies consistent styling to label panels with titled borders.
     * 
     * Configures panel appearance including background colors, fonts, borders,
     * and adds a titled border with the specified title text. Used for grouping
     * related UI elements with clear visual separation.
     * 
     * @param panel The panel to style
     * @param palette The color palette to use for styling
     * @param title The title text to display on the border
     */
    public void styleLabelPanel(JPanel panel, UIPalette palette, String title) {
        panel.setBackground(palette.labelBackground);
        panel.setForeground(palette.labelForeground);
        panel.setFont(palette.font);
        panel.setOpaque(true);

        Border lineBorder = BorderFactory.createLineBorder(palette.borderColor, 2);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(lineBorder, title);
        panel.setBorder(titledBorder);
    }
}
