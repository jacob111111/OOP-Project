package gui.menu;

import javax.swing.JPanel;

import gui.utils.UIPalette;
import gui.utils.UIStyle;

public class LabelPanel {
    
    public static JPanel createColumnLabelsPanel() {
        JPanel columnLabelPanel = new JPanel();

        return columnLabelPanel;
    }

    public static JPanel createRowLabelsPanel() {
        JPanel rowLabelsPanel = new JPanel();

        return rowLabelsPanel;
    }

    public static void setPalette(JPanel panel, UIPalette palette, boolean isRowPanel) {
        UIStyle style = new UIStyle();

        panel.repaint();
        panel.revalidate();
    }
}
