package gui;

import javax.swing.JFrame;
import java.awt.BorderLayout;

import gui.board.MainBoardPanel;
import gui.menu.*;
import gui.utils.*;
import game.*;

public class chessFrame extends JFrame 
{
    private static final int FRAME_WIDTH = 1000; // Increased width for side menu
    private static final int FRAME_HEIGHT = 800;
    
    private UIPalette masterPalette;
    private UIStyle masterStyle;
    private String currentPieceTheme = "classic"; // Track piece theme separately

    private MainBoardPanel boardPanel;
    private sideMenuPanel menuPanel;

    private GUI currentGame;

    public chessFrame(UIPalette palette) 
    {
        this.masterPalette = palette;
        this.masterStyle = new UIStyle();
        setTitle("Chess Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Initialize panels
        boardPanel = new MainBoardPanel(this);
        menuPanel = new sideMenuPanel(this);

        // Add panels to frame
        add(boardPanel, BorderLayout.CENTER);
        add(menuPanel, BorderLayout.EAST);
        
        setVisible(true);
    }

    public chessFrame() {
        this(UIPalette.CLASSIC);
    }
    
    public UIPalette getPalette() { return masterPalette; }
    public UIStyle getStyle() { return masterStyle; }
    
    public void setPalette(UIPalette newPalette) {
        this.masterPalette = newPalette;
        updateAllPanels();
    }

    private void updateAllPanels() {
        menuPanel.updateStyle();
        boardPanel.updateStyle();
        revalidate();
        repaint();
    }

    // Add this public method to expose update functionality to the menu
    public void refreshDisplay() {
        updateAllPanels();
    }

    public GUI getCurrentGame() { return currentGame; }

    public void setGame(GUI game) {
        this.currentGame = game;
        if (game != null) {
            boardPanel.setGame(game);
            boardPanel.updateDisplay();
            menuPanel.setGameInProgress(true); // Disable game mode buttons
        } else {
            // Clear the board when game is null
            boardPanel.setGame(null);
            menuPanel.setGameInProgress(false); // Re-enable game mode buttons
        }
    }

    // Add method to clear the current game
    public void clearGame() {
        setGame(null);
    }

    // Method to start a new 2-player game
    public void startTwoPlayerGame() {
        GUI newGame = new GUI(true, utils.Color.WHITE);
        newGame.setParentFrame(this); // Set parent reference for clearing
        setGame(newGame);
    }

    // Method to change board theme (called from side menu)
    public void changeTheme(String themeName) {
        UIPalette newPalette = themeName.equals("Modern") ? UIPalette.MODERN : UIPalette.CLASSIC;
        setPalette(newPalette);
    }
    
    // Method to change piece theme (called from side menu)
    public void changePieceTheme(String pieceThemeName) {
        this.currentPieceTheme = pieceThemeName.toLowerCase();
        // Update the board to use new piece theme
        boardPanel.setPieceTheme(currentPieceTheme);
        if (currentGame != null) {
            boardPanel.updateDisplay();
        }
    }
    
    public String getCurrentPieceTheme() {
        return currentPieceTheme;
    }
}