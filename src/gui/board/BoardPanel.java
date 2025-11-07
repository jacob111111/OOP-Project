package gui.board;

import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import gui.utils.UIPalette;
import gui.utils.UIStyle;

import board.*;
import piece.*;

import game.GUI;
import utils.Position;

public class BoardPanel extends JPanel {
    private static final int ICON_SIZE = 50; // Adjust this value as needed
    private UIPalette palette;
    private UIStyle style = new UIStyle();
    private GUI instance;

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

    public BoardPanel() {
        this(UIPalette.CLASSIC);
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

    public void setGame(GUI game) {
        this.instance = game;
        drawPieces();
    }

    public boolean instanceExists() { 
        if(instance != null) { return true; }
        else { return false; }
    }

    public void drawPieces() {
        if (!instanceExists())
            return;

        Board board = instance.getBoard();
        if (board == null)
            return;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Position pos = new Position(col, row);
                Piece piece = board.getPieceAt(pos);
                int index = (row * 8) + col;
                JButton cellButton = (JButton) getComponent(index);
                if (piece != null) {
                    ImageIcon icon = getIcon(piece);
                    if (icon != null) {
                        cellButton.setIcon(icon);
                    } else {
                        cellButton.setText(piece.getDisplaySymbol());
                    }
                } else {
                    cellButton.setIcon(null);
                    cellButton.setText("");
                }
            }
        }
    }

    private ImageIcon getIcon(Piece piece) {
        try {
            String paletteName = palette == UIPalette.CLASSIC ? "classic" : "modern";
            String pieceColor = piece.getColor().toString().toLowerCase();
            String symbol = piece.getDisplaySymbol();
            String resourcePath = "/gui/images/" + paletteName + "/" + pieceColor + "/" + symbol + ".png";

            java.net.URL imgURL = getClass().getResource(resourcePath);
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);
                return scaleIcon(icon, ICON_SIZE);
            } else {
                System.err.println("Couldn't find icon: " + resourcePath);
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error loading icon for piece: " + piece.getDisplaySymbol());
            e.printStackTrace();
            return null;
        }
    }

    private ImageIcon scaleIcon(ImageIcon icon, int size) {
        java.awt.Image img = icon.getImage();
        java.awt.Image scaledImg = img.getScaledInstance(size, size, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImg);
    }
}
