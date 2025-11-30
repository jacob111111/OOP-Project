package gui.board;

import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import gui.utils.UIPalette;
import gui.utils.UIStyle;

public class LabelPanel {

    public static JPanel createColumnLabelsPanel() {
        JPanel columnLabelPanel = new JPanel();
        columnLabelPanel.setLayout(new GridLayout(1, 8));
        for (int i = 0; i < 8; i++) {
            columnLabelPanel.add(new JLabel(String.valueOf((char) ('a' + i)), JLabel.CENTER));
        }
        columnLabelPanel.setPreferredSize(new java.awt.Dimension(0, 50));
        columnLabelPanel.setMinimumSize(new java.awt.Dimension(0, 40));
        return columnLabelPanel;
    }

    /**
     * Updates column labels based on board perspective.
     * 
     * @param columnLabelsPanel The column labels panel to update
     * @param whiteAtBottom     true if white is at bottom (a-h), false if black at
     *                          bottom (h-a)
     */
    public static void updateColumnLabels(JPanel columnLabelsPanel, boolean whiteAtBottom) {
        for (int i = 0; i < 8; i++) {
            JLabel label = (JLabel) columnLabelsPanel.getComponent(i);
            // When white at bottom: left to right is a-h
            // When black at bottom: left to right is h-a
            char columnLetter = whiteAtBottom ? (char) ('a' + i) : (char) ('h' - i);
            label.setText(String.valueOf(columnLetter));
        }
        columnLabelsPanel.repaint();
    }

    public static JPanel createRowLabelsPanel() {
        JPanel rowLabelsPanel = new JPanel();
        rowLabelsPanel.setLayout(new GridLayout(8, 1));
        // Chess rows are numbered 1-8 from bottom to top, so display 8-1 from top to
        // bottom
        for (int i = 0; i < 8; i++) {
            rowLabelsPanel.add(new JLabel(String.valueOf(8 - i), JLabel.CENTER));
        }
        rowLabelsPanel.setPreferredSize(new java.awt.Dimension(50, 0));
        rowLabelsPanel.setMinimumSize(new java.awt.Dimension(40, 0));
        return rowLabelsPanel;
    }

    /**
     * Updates row labels based on board perspective.
     * 
     * @param rowLabelsPanel The row labels panel to update
     * @param whiteAtBottom  true if white is at bottom (rows 1-8), false if black
     *                       at bottom (rows 8-1)
     */
    public static void updateRowLabels(JPanel rowLabelsPanel, boolean whiteAtBottom) {
        for (int i = 0; i < 8; i++) {
            JLabel label = (JLabel) rowLabelsPanel.getComponent(i);
            // When white at bottom: top row is 8, bottom row is 1
            // When black at bottom: top row is 1, bottom row is 8
            int rowNumber = whiteAtBottom ? (8 - i) : (i + 1);
            label.setText(String.valueOf(rowNumber));
        }
        rowLabelsPanel.repaint();
    }

    public static void setPalette(JPanel panel, UIPalette palette, boolean isRowPanel) {
        UIStyle style = new UIStyle();
        style.styleLabelPanel(panel, palette, "");

        for (int i = 0; i < panel.getComponentCount(); i++) {
            if (panel.getComponent(i) instanceof JLabel) {
                JLabel label = (JLabel) panel.getComponent(i);
                label.setFont(palette.font);
                label.setForeground(palette.labelForeground);
            }
        }
        panel.repaint();
        panel.revalidate();
    }
}
