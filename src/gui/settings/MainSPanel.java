package gui.settings;

import java.awt.BorderLayout;
import javax.swing.JPanel;

import gui.utils.UIPalette;

public class MainSPanel extends JPanel {

    public MainSPanel() {

    }

    public MainSPanel(UIPalette palette) {

    }

    public void setPalette(UIPalette palette) {
        repaint();
        revalidate();
    }
}
