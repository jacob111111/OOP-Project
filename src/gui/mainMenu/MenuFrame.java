package gui.mainMenu;

import javax.swing.JFrame;

import gui.MainPanel;
import gui.UIPalette;

public class MenuFrame extends JFrame {
    private static final int FRAME_WIDTH = 400;
    private static final int FRAME_HEIGHT = 400;
    
    public MenuFrame(UIPalette palette) {
        setTitle("Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setLocationRelativeTo(null);
        add(new MainPanel(palette));
        setVisible(true);
    }
}
