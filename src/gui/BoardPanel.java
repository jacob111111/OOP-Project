package gui;

import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import board.*;
import piece.*;
import utils.Position;

public class BoardPanel extends JPanel {
    private UIPalette palette;
    private UIStyle style = new UIStyle();

    public BoardPanel() {
        this(UIPalette.CLASSIC);
    }

    public BoardPanel(UIPalette palette) {
        this.palette = palette;
        setLayout(new GridLayout(8, 8));
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JButton cellButton = new JButton();
                boolean isLight = (row + col) % 2 == 0;
                style.styleCellButton(cellButton, isLight, palette);
                add(cellButton);
            }
        }
    }

    public void setPalette(UIPalette newPalette) {
        this.palette = newPalette;
        for (int i = 0; i < getComponentCount(); i++) {
            if (getComponent(i) instanceof JButton) {
                JButton cellButton = (JButton) getComponent(i);
                int row = i / 8;
                int col = i % 8;
                boolean isLight = (row + col) % 2 == 0;
                style.styleCellButton(cellButton, isLight, newPalette);
            }
        }
        repaint();
        revalidate();
    }

    public void drawPieces(Board board) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Position pos = new Position(col, row);
                Piece piece = board.getPieceAt(pos);
                int index = (row * 8) + col;
                JButton cellButton = (JButton) getComponent(index);
                if (piece != null) {
                    cellButton.setIcon(getIcon(piece));
                } else {
                    cellButton.setIcon(null);
                }
            }
        }
    }

    private ImageIcon getIcon(Piece piece) {
        // Example: "/gui/images/classic/wK.png"
        String paletteName = palette == UIPalette.CLASSIC ? "classic" : "modern";
        String symbol = piece.getDisplaySymbol(); // e.g., "wK"
        String imagePath = "/gui/images/" + paletteName + "/" + symbol + ".png";
        return new ImageIcon(getClass().getResource(imagePath));
    }
}
