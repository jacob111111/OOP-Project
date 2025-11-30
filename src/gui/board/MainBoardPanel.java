package gui.board;

import java.awt.BorderLayout;
import javax.swing.JPanel;

import gui.chessFrame;
import gui.utils.UIPalette;
import gui.utils.UIStyle;

import game.GUI;

public class MainBoardPanel extends JPanel {
    private chessFrame parentFrame;
    private JPanel rowLabelsPanel;
    private JPanel columnLabelsPanel;
    private BoardPanel boardPanel;

    public MainBoardPanel(chessFrame parent) {
        this.parentFrame = parent;
        initializeComponents();
        updateStyle();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout(0, 0));

        columnLabelsPanel = LabelPanel.createColumnLabelsPanel();
        rowLabelsPanel = LabelPanel.createRowLabelsPanel();
        boardPanel = new BoardPanel(parentFrame.getPalette());

        add(columnLabelsPanel, BorderLayout.SOUTH);
        add(rowLabelsPanel, BorderLayout.WEST);
        add(boardPanel, BorderLayout.CENTER);
    }

    public void updateStyle() {
        UIStyle style = parentFrame.getStyle();
        UIPalette palette = parentFrame.getPalette();

        LabelPanel.setPalette(rowLabelsPanel, palette, true);
        LabelPanel.setPalette(columnLabelsPanel, palette, false);
        boardPanel.setPalette(palette);

        repaint();
        revalidate();
    }

    public void setGame(GUI game) {
        boardPanel.setGame(game);
        if (game != null) {
            updateDisplay();
        }
    }

    public void setPieceTheme(String pieceTheme) {
        boardPanel.setPieceTheme(pieceTheme);
    }

    public void updateDisplay() {
        if (boardPanel.instanceExists()) {
            boardPanel.drawPieces();
        } else {
            // Clear pieces when no game instance
            boardPanel.drawPieces();
        }
    }

    /**
     * Flips the board perspective and updates labels to stay fixed.
     * Pieces flip, but labels remain in their correct orientation.
     */
    public void flipBoard() {
        boardPanel.flipBoard();
        // Labels DON'T flip - they always show a-h left to right and 1-8 bottom to top
        // from current perspective
        // We keep them fixed regardless of board orientation
    }
}
