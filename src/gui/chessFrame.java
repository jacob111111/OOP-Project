package gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;

import gui.board.MainBPanel;
import gui.menu.MainMPanel;
import gui.settings.MainSPanel;
import gui.utils.UIPalette;
import gui.utils.state;

public class chessFrame extends JFrame 
{
    private static final int FRAME_WIDTH = 800;
    private static final int FRAME_HEIGHT = 800;
    
    private CardLayout cardLayout;
    private JPanel cards;

    public chessFrame(UIPalette palette) 
    {
        setTitle("Chess Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setLocationRelativeTo(null);
        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        JPanel menu = new MainMPanel(palette);
        JPanel board = new MainSPanel(palette);
        JPanel settings = new MainBPanel(palette);

        cards.add(menu, state.MENU.toString());
        cards.add(board, state.GAME.toString());
        cards.add(settings, state.SETTINGS.toString());
        
        add(cards);
        setVisible(true);
    }

    public chessFrame() {
        this(UIPalette.CLASSIC);
    }
    
    public void switchToState(state newState) {
        cardLayout.show(cards, newState.toString());
    }
    
    public void switchToMenu() { switchToState(state.MENU); }
    public void switchToGame() { switchToState(state.GAME); }
    public void switchToSettings() { switchToState(state.SETTINGS); }
}
