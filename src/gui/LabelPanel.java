package gui;

import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class LabelPanel {
    
    public static JPanel createColumnLabelsPanel() {
        JPanel columnLabelPanel = new JPanel();
        columnLabelPanel.setLayout(new GridLayout(1, 8));
        for (int i = 0; i < 8; i++) {
            columnLabelPanel.add(new JLabel(String.valueOf((char)('a' + i)), JLabel.CENTER));
        }
        return columnLabelPanel;
    }

    public static JPanel createRowLabelsPanel() {
        JPanel rowLabelsPanel = new JPanel();
        rowLabelsPanel.setLayout(new GridLayout(8, 1));
        for (int i = 0; i < 8; i++) {
            rowLabelsPanel.add(new JLabel(String.valueOf(i + 1), JLabel.CENTER));
        }
        rowLabelsPanel.setPreferredSize(new java.awt.Dimension(50, 0));
        rowLabelsPanel.setMinimumSize(new java.awt.Dimension(40, 0));
        return rowLabelsPanel;
    }

    public static void setPalette(JPanel panel, UIPalette palette, boolean isRowPanel) {
        UIStyle style = new UIStyle();
        String title = isRowPanel ? "Row Labels" : "Column Labels";
        style.styleLabelPanel(panel, palette, title);

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
