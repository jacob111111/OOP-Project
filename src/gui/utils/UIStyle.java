package gui.utils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class UIStyle {
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
