package gui;

import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class BoardPanel extends JPanel {
    private UIPalette palette;
    private UIStyle style = new UIStyle();

    public BoardPanel() {
        this(UIPalette.CLASSIC);
    }

    public BoardPanel(UIPalette palette) {
        this.palette = palette;
        setLayout(new GridLayout(8, 8));
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JButton cellButton = new JButton();
                boolean isLight = (row + col) % 2 == 0;
                style.styleCellButton(cellButton, isLight, palette);
                add(cellButton);
            }
        }
    }

    public void setPalette(UIPalette newPalette) {
        this.palette = newPalette;
        for (int i = 0; i < getComponentCount(); i++) {
            if (getComponent(i) instanceof JButton) {
                JButton cellButton = (JButton) getComponent(i);
                int row = i / 8;
                int col = i % 8;
                boolean isLight = (row + col) % 2 == 0;
                style.styleCellButton(cellButton, isLight, newPalette);
            }
        }
        repaint();
        revalidate();
    }
}
