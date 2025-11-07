package gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Color;
import java.util.Scanner;

import gui.board.MainBoardPanel;
import gui.menu.*;
import gui.utils.*;
import game.*;

public class chessFrame extends JFrame 
{
    private static final int FRAME_WIDTH = 800;
    private static final int FRAME_HEIGHT = 800;
    
    private CardLayout cardLayout;
    private JPanel cards;
    
    private gameState currentgameState;

    private UIPalette masterPalette;
    private UIStyle masterStyle;

    private mainMenuPanel menuPanel;
    private MainBoardPanel boardPanel;
    private settingsPanel settingsPanel;

    private GUI currentGame;

    public chessFrame(UIPalette palette) 
    {
        this.masterPalette = palette;
        this.masterStyle = new UIStyle();
        setTitle("Chess Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        menuPanel = new mainMenuPanel(this);
        boardPanel = new MainBoardPanel(this);
        settingsPanel = new settingsPanel(this);

        cards.add(menuPanel, gameState.MENU.toString());
        cards.add(boardPanel, gameState.GAME.toString());
        cards.add(settingsPanel, gameState.SETTINGS.toString());
        
        currentgameState = gameState.MENU;
        add(cards);
        setVisible(true);
    }

    public chessFrame() {
        this(UIPalette.CLASSIC);
    }
    
    public gameState getGameState() {return currentgameState;}
    public UIPalette getPalette() { return masterPalette; }
    public UIStyle getStyle() { return masterStyle; }
    
    public void setPalette(UIPalette newPalette) {
        this.masterPalette = newPalette;
        updateAllPanels();
    }

    private void updateAllPanels() {
        menuPanel.updateStyle();
        boardPanel.updateStyle();
        settingsPanel.updateStyle();
        revalidate();
        repaint();
    }

    public void switchToState(gameState newgameState) {
        cardLayout.show(cards, newgameState.toString());
        this.currentgameState = newgameState;
    }
    public void switchToMenu() { switchToState(gameState.MENU); }

    public void switchToGame() { 
        if (currentGame != null) {
            boardPanel.updateDisplay();
        }
        switchToState(gameState.GAME); 
    }

    public void switchToSettings() { switchToState(gameState.SETTINGS); }

    public GUI getCurrentGame() { return currentGame; }

    public void setGame(GUI game) {
        this.currentGame = game; // Store game instance
        boardPanel.setGame(game); // Connect instance to board card
    }
}
