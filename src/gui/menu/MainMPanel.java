package gui.menu;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import gui.utils.UIPalette;
import gui.utils.UIStyle;

public class MainMPanel extends JPanel {
    private UIPalette palette;
    private UIStyle style = new UIStyle();

    // Need to apply palletes
    public MainMPanel(UIPalette palette) {
        this.palette = palette;
        setLayout(new BorderLayout());
        JLabel gameTitle = new JLabel("CHESS");
        gameTitle.setPreferredSize(new java.awt.Dimension(200,10));
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.PAGE_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JButton AIButton = new JButton("1-Player");
        JButton LANButton = new JButton("2-Player");
        JButton settingsButton = new JButton("Settings");

        centerPanel.add(AIButton);
        centerPanel.add(LANButton);
        centerPanel.add(settingsButton);

        add(gameTitle, BorderLayout.PAGE_START);
        add(centerPanel, BorderLayout.CENTER);
    }

    public MainMPanel() {
        this(UIPalette.CLASSIC);
    }
    public void setPalette(UIPalette palette) {
        repaint();
        revalidate();
    }
}
