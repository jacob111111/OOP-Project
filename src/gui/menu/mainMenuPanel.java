package gui.menu;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.*;

import gui.chessFrame;
import gui.utils.UIPalette;
import gui.utils.UIStyle;


public class mainMenuPanel extends JPanel {
    private chessFrame parentFrame;
    private JButton AIButton, LANButton, settingsButton;
    private JLabel gameTitle;
    private JPanel centerPanel;

    public mainMenuPanel(chessFrame parent) {
        this.parentFrame = parent;
        initializeComponents();
        updateStyle();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
        
        gameTitle = new JLabel("CHESS", SwingConstants.CENTER);
        gameTitle.setPreferredSize(new Dimension(60, 50));
        
        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.PAGE_AXIS));

        AIButton = new JButton("1-Player");
        LANButton = new JButton("2-Player");
        settingsButton = new JButton("Settings");

        // Updated action listeners to work with new structure
        AIButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "AI mode not yet implemented!");
        });

        LANButton.addActionListener(e -> {
            parentFrame.startTwoPlayerGame();
        });

        settingsButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Settings available in side panel!");
        });

        styleMenuButton(AIButton);
        styleMenuButton(LANButton);
        styleMenuButton(settingsButton);

        centerPanel.add(AIButton, BorderLayout.CENTER);
        centerPanel.add(Box.createRigidArea(new Dimension(0,10)));
        centerPanel.add(LANButton, BorderLayout.CENTER);
        centerPanel.add(Box.createRigidArea(new Dimension(0,10)));
        centerPanel.add(settingsButton, BorderLayout.CENTER);

        add(gameTitle, BorderLayout.PAGE_START);
        add(centerPanel, BorderLayout.CENTER);
    }

    public void updateStyle() {
        UIStyle style = parentFrame.getStyle();
        UIPalette palette = parentFrame.getPalette();
        
        style.styleCellButton(AIButton, true, palette);
        style.styleCellButton(LANButton, true, palette);
        style.styleCellButton(settingsButton, true, palette);
        
        style.styleLabelPanel(this, palette, "Chess Game");
        style.styleLabelPanel(centerPanel, palette, "Menu Options");
        
        gameTitle.setFont(palette.font);
        gameTitle.setForeground(palette.labelForeground);
        
        repaint();
        revalidate();
    }

    private void styleMenuButton(JButton button) {
        button.setAlignmentX(CENTER_ALIGNMENT);
        button.setPreferredSize(new Dimension(200, 100));
    }
}
