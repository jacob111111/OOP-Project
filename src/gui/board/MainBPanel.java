package gui.board;

import java.awt.BorderLayout;
import javax.swing.JPanel;

import gui.utils.UIPalette;

public class MainBPanel extends JPanel {
    private JPanel rowLabelsPanel;
    private JPanel columnLabelsPanel;

    public MainBPanel(UIPalette palette) {
        setLayout(new BorderLayout());
        columnLabelsPanel = LabelPanel.createColumnLabelsPanel();
        add(columnLabelsPanel, BorderLayout.SOUTH);

        rowLabelsPanel = LabelPanel.createRowLabelsPanel();
        rowLabelsPanel.setPreferredSize(new java.awt.Dimension(15,0));
        rowLabelsPanel.setMinimumSize(new java.awt.Dimension(10,0));
        add(rowLabelsPanel, BorderLayout.WEST);

        add(new BoardPanel(palette), BorderLayout.CENTER);
    }
    
    public MainBPanel() {
        this(UIPalette.CLASSIC);
    }

    public void setPalette(UIPalette palette) {
        LabelPanel.setPalette(rowLabelsPanel, palette, true);
        LabelPanel.setPalette(columnLabelsPanel, palette, false);
        repaint();
        revalidate();
    }
}
