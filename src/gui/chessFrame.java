package gui;

import javax.swing.JFrame;

public class chessFrame extends JFrame 
{
    private static final int FRAME_WIDTH = 800;
    private static final int FRAME_HEIGHT = 800;

    public chessFrame(UIPalette palette) 
    {
        setTitle("Chess Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setLocationRelativeTo(null);
        add(new MainPanel(palette));
        setVisible(true);
    }

    public chessFrame() {
        this(UIPalette.CLASSIC);
    }
}
