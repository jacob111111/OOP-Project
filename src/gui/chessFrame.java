package gui;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import gui.board.MainBoardPanel;
import gui.menu.*;
import gui.utils.*;
import game.*;

/**
 * Main chess game window managing board display and side menu.
 */
public class ChessFrame extends JFrame {
    private static final int FRAME_WIDTH = 1000; // Increased width for side menu
    private static final int FRAME_HEIGHT = 800;

    private UIPalette masterPalette;
    private UIStyle masterStyle;
    private String currentPieceTheme = "classic"; // Track piece theme separately

    private MainBoardPanel boardPanel;
    private SideMenuPanel menuPanel;

    private GUI currentGame;

    /**
     * Creates a chess frame with specified UI palette.
     * 
     * @param palette UI palette for styling
     */
    public ChessFrame(UIPalette palette) {
        this.masterPalette = palette;
        this.masterStyle = new UIStyle();
        setTitle("Chess Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Initialize panels
        boardPanel = new MainBoardPanel(this);
        menuPanel = new SideMenuPanel(this);

        // Add panels to frame
        add(boardPanel, BorderLayout.CENTER);
        add(menuPanel, BorderLayout.EAST);

        // Add window listener to cleanup AI engine on close
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cleanupAIEngine();
            }
        });

        setVisible(true);
    }

    /**
     * Creates a chess frame with default classic palette.
     */
    public ChessFrame() {
        this(UIPalette.CLASSIC);
    }

    public UIPalette getPalette() {
        return masterPalette;
    }

    /**
     * Gets the UI style utility instance.
     * 
     * @return The UIStyle instance used for component styling
     */
    public UIStyle getStyle() {
        return masterStyle;
    }

    /**
     * Sets new UI palette and updates all components.
     * 
     * @param newPalette new palette to apply
     */
    public void setPalette(UIPalette newPalette) {
        this.masterPalette = newPalette;
        updateAllPanels();
    }

    /**
     * Refreshes all panels with current palette.
     */
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

    /**
     * Gets the currently active game instance.
     * 
     * @return The current GUI game instance, or null if no game is active
     */
    public GUI getCurrentGame() {
        return currentGame;
    }

    /**
     * Sets active game and updates display.
     * 
     * @param game game instance or null to clear
     */
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
        // Initialize turn display to WHITE (always starts first)
        updateTurnDisplay(utils.Color.WHITE);
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

    /**
     * Displays message in side menu message board.
     * 
     * @param message     message text
     * @param messageType type ("error", "warning", "info")
     */
    public void displayMessage(String message, String messageType) {
        if (menuPanel != null) {
            menuPanel.displayMessage(message, messageType);
        }
    }

    /**
     * Updates the hover info label in the side menu.
     * 
     * @param pieceName  The name of the piece being hovered over
     * @param pieceColor The color of the piece
     */
    public void updateHoverInfo(String pieceName, String pieceColor) {
        if (menuPanel != null) {
            menuPanel.updateHoverInfo(pieceName, pieceColor);
        }
    }

    /**
     * Clears the hover info label in the side menu.
     */
    public void clearHoverInfo() {
        if (menuPanel != null) {
            menuPanel.clearHoverInfo();
        }
    }

    /**
     * Updates the turn indicator in the side menu.
     * 
     * @param currentTurn The color of the player whose turn it is
     */
    public void updateTurnDisplay(utils.Color currentTurn) {
        if (menuPanel != null) {
            menuPanel.updateTurnIndicator(currentTurn);
        }
    }

    /**
     * Enables or disables the board panel for user interaction.
     * Used to prevent moves while searching for online games.
     * 
     * @param enabled true to enable board interaction, false to disable
     */
    public void setBoardEnabled(boolean enabled) {
        if (boardPanel != null) {
            boardPanel.setEnabled(enabled);
            // Recursively enable/disable all child components
            java.awt.Component[] components = boardPanel.getComponents();
            for (java.awt.Component comp : components) {
                comp.setEnabled(enabled);
            }
        }
    }

    /**
     * Flips the board perspective for network games.
     * Used to show black's perspective for the client player.
     */
    public void flipBoard() {
        if (boardPanel != null) {
            boardPanel.flipBoard();
        }
    }

    /**
     * Cleans up AI engine resources when closing the application.
     * Called automatically when the window is closing.
     */
    private void cleanupAIEngine() {
        if (currentGame != null) {
            board.Board gameBoard = currentGame.getBoard();
            if (gameBoard != null) {
                player.Player white = gameBoard.getPlayer(utils.Color.WHITE);
                player.Player black = gameBoard.getPlayer(utils.Color.BLACK);

                if (white instanceof player.AI) {
                    ((player.AI) white).shutdown();
                }
                if (black instanceof player.AI) {
                    ((player.AI) black).shutdown();
                }
            }
        }
    }
}