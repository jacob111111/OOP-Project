package gui.menu;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;

import gui.chessFrame;
import gui.utils.UIPalette;
import gui.utils.UIStyle;

public class settingsPanel extends JPanel {
    private chessFrame parentFrame;
    private JPanel palettePanel;
    private JComboBox<String> paletteSelector;
    private JButton exitButton;

    public settingsPanel(chessFrame parent) {
        this.parentFrame = parent;
        initializeComponents();
        updateStyle();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
        
        palettePanel = new JPanel();
        palettePanel.setLayout(new FlowLayout());
        
        paletteSelector = new JComboBox<>(new String[]{"Classic", "Modern"});
        palettePanel.add(new JLabel("Theme: "));
        palettePanel.add(paletteSelector);
        
        exitButton = new JButton("Back to Menu");
        exitButton.addActionListener(e -> parentFrame.switchToMenu());
        
        add(palettePanel, BorderLayout.NORTH);
        add(exitButton, BorderLayout.SOUTH);
    }

    public void updateStyle() {
        UIStyle style = parentFrame.getStyle();
        UIPalette palette = parentFrame.getPalette();
        
        style.styleLabelPanel(this, palette, "Settings");
        style.styleLabelPanel(palettePanel, palette, "Theme Settings");
        
        paletteSelector.setFont(palette.font);
        paletteSelector.setForeground(palette.labelForeground);
        paletteSelector.setBackground(palette.labelBackground);
        
        style.styleCellButton(exitButton, true, palette);
        
        repaint();
        revalidate();
    }
}
