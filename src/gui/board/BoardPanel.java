package gui.board;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import javax.swing.Timer;

import gui.utils.UIPalette;
import gui.utils.UIStyle;

import board.*;
import piece.*;

import game.GUI;
import utils.Position;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BoardPanel extends JPanel implements MouseListener, MouseMotionListener {
    private static final int ICON_SIZE = 50; // Adjust this value as needed
    private UIPalette palette;
    private UIStyle style = new UIStyle();
    private GUI instance;
    private String pieceTheme = "classic"; // Default piece theme

    private MoveState currentMove;

    private JButton selectedButton = null;
    private static final Border HIGHLIGHT_BORDER = BorderFactory.createLineBorder(Color.YELLOW, 3);
    private Map<JButton, Border> originalBorders = new HashMap<>();

    // Drag visual state
    private JLabel dragLabel = null;
    private ImageIcon dragIcon = null;
    private Point dragOffset = null;

    private JButton hoveredButton = null;
    private boolean whiteAtBottom = true; // true = white perspective, false = black perspective

    public BoardPanel(UIPalette palette) {
        this.palette = palette;
        setLayout(new GridLayout(8, 8));
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JButton cellButton = new JButton();
                boolean isLight = (row + col) % 2 == 0;
                style.styleCellButton(cellButton, isLight, palette);
                originalBorders.put(cellButton, cellButton.getBorder());
                cellButton.addMouseListener(this);
                cellButton.addMouseMotionListener(this);
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
                originalBorders.put(cellButton, cellButton.getBorder());
            }
        }
        repaint();
        revalidate();
    }

    public void setGame(GUI game) {
        this.instance = game;
        whiteAtBottom = true; // Reset board orientation for new game

        // Clear any ongoing move state from previous game
        currentMove = null;
        if (selectedButton != null) {
            selectedButton.setBorder(originalBorders.get(selectedButton));
            selectedButton = null;
        }

        // Clear drag state properly (removes from layered pane)
        removeDragVisual();

        drawPieces();
    }

    public boolean instanceExists() {
        if (instance != null) {
            return true;
        } else {
            return false;
        }
    }

    public void setPieceTheme(String newPieceTheme) {
        this.pieceTheme = newPieceTheme.toLowerCase();
        drawPieces(); // Refresh pieces with new theme
    }

    /**
     * Flips the board perspective and redraws.
     * Toggles between white at bottom (true) and black at bottom (false).
     */
    public void flipBoard() {
        whiteAtBottom = !whiteAtBottom;
        drawPieces();
    }

    /**
     * Converts display row to board row based on current perspective.
     * 
     * @param displayRow The row index in the GUI (0-7, top to bottom)
     * @return The board row coordinate
     */
    private int displayRowToBoardRow(int displayRow) {
        return whiteAtBottom ? (7 - displayRow) : displayRow;
    }

    public void drawPieces() {
        if (!instanceExists()) {
            // Clear all pieces when no game instance
            clearAllPieces();
            return;
        }

        Board board = instance.getBoard();
        if (board == null) {
            clearAllPieces();
            return;
        }

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Position pos = new Position(col, displayRowToBoardRow(row));
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
            String pieceColor = piece.getColor().toString().toLowerCase();
            String symbol = piece.getDisplaySymbol();
            String resourcePath = "/gui/images/" + pieceTheme + "/" + pieceColor + "/" + symbol + ".png";

            java.net.URL imgURL = getClass().getResource(resourcePath);
            if (imgURL != null) {
                java.awt.image.BufferedImage bImg = javax.imageio.ImageIO.read(imgURL);
                return scaleIcon(new ImageIcon(bImg), ICON_SIZE);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    private ImageIcon scaleIcon(ImageIcon icon, int size) {
        java.awt.Image img = icon.getImage();
        java.awt.Image scaledImg = img.getScaledInstance(size, size, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImg);
    }

    // Helper method for position conversion
    private Position buttonToPosition(JButton button) {
        int index = java.util.Arrays.asList(getComponents()).indexOf(button);
        int row = index / 8;
        int col = index % 8;
        return new Position(col, displayRowToBoardRow(row));
    }

    // Helper to highlight or unhighlight a cell
    private void highlightCell(JButton button, boolean highlight) {
        if (button == null)
            return;
        if (highlight) {
            button.setBorder(HIGHLIGHT_BORDER);
        } else {
            Border originalBorder = originalBorders.get(button);
            if (originalBorder != null) {
                button.setBorder(originalBorder);
            }
        }
    }

    // Helper to clear highlight from previously selected button
    private void clearHighlights() {
        if (selectedButton != null) {
            highlightCell(selectedButton, false);
            selectedButton = null;
        }
    }

    // Mouse event implementations
    @Override
    public void mousePressed(MouseEvent e) {
        // Clear any hover highlight since we're now interacting via click/drag
        if (hoveredButton != null) {
            highlightCell(hoveredButton, false);
            hoveredButton = null;
        }

        JButton button = (JButton) e.getSource();
        Position pos = buttonToPosition(button);
        Piece piece = instance.getBoard().getPieceAt(pos);

        if (piece != null) {
            // DEBUG: Print piece type, possible moves, and attack map data
            System.out.println("=== PIECE SELECTED ===");
            System.out.println("Piece type: " + piece.getClass().getSimpleName());
            System.out.println("Color: " + piece.getColor());
            System.out.println("Position: " + pos);
            System.out.println("Display symbol: " + piece.getDisplaySymbol());
            Set<Position> possibleMoves = piece.getPossibleMoves(instance.getBoard());
            System.out.println("Possible moves (" + possibleMoves.size() + "): " + possibleMoves);

            // Show attack map information
            utils.AttackMap attackMap = instance.getBoard().getAttackMap();
            utils.Color opponentColor = (piece.getColor() == utils.Color.WHITE) ? utils.Color.BLACK : utils.Color.WHITE;
            System.out.println("\nAttack Map Info:");
            System.out
                    .println("  This square attacked by opponent? " + attackMap.isSquareAttackedBy(pos, opponentColor));
            if (attackMap.isSquareAttackedBy(pos, opponentColor)) {
                System.out.println("  Attacking pieces: " + attackMap.getPiecesAttacking(pos, opponentColor));
            }
            System.out.println("======================");

            currentMove = new MoveState(piece, pos, MoveState.mouseEventType.DRAG, instance);

            // Prepare drag icon
            dragIcon = getIcon(piece);
            // DEBUGING mouse dragging
            // System.out.println("dragIcon in mousePressed: " + (dragIcon != null));
            if (dragIcon != null) {
                Image img = dragIcon.getImage();
                Image transparentImg = createTransparentImage(img, 0.7f, ICON_SIZE, ICON_SIZE);
                dragIcon = new ImageIcon(transparentImg);

                // Create drag label
                dragLabel = new JLabel(dragIcon);
                dragLabel.setSize(ICON_SIZE, ICON_SIZE);

                /**
                 * DEBUGING mouse dragging
                 * dragLabel.setOpaque(true);
                 * dragLabel.setBackground(Color.RED);
                 * 
                 * System.out.println("dragLabel created: " + (dragLabel != null) + ", size: " +
                 * dragLabel.getSize());
                 */

                // Calculate offset from mouse to icon origin
                Point buttonLoc = button.getLocationOnScreen();
                int offsetX = e.getXOnScreen() - buttonLoc.x;
                int offsetY = e.getYOnScreen() - buttonLoc.y;
                dragOffset = new Point(offsetX, offsetY);

                // Add drag label to glass pane
                JLayeredPane layeredPane = getRootPane().getLayeredPane();
                layeredPane.add(dragLabel, JLayeredPane.DRAG_LAYER);
                /**
                 * DEBUGING mouse dragging
                 * System.out.println("dragLabel added to layeredPane: " +
                 * (dragLabel.getParent() == layeredPane));
                 * System.out.println("layeredPane size: " + layeredPane.getSize());
                 * System.out.println("dragLabel location (before update): " +
                 * dragLabel.getLocation());
                 */
                updateDragLabelLocation(e);
            } else {
                System.out.println("Drag icon is not found, defaulting to symbol");
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (dragLabel != null) {
            updateDragLabelLocation(e);
        }
        // Highlight the button being hovered over during drag
        Point panelPoint = SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), this);
        Component comp = getComponentAt(panelPoint);
        JButton button = (comp instanceof JButton) ? (JButton) comp : null;

        if (hoveredButton != null && hoveredButton != button) {
            highlightCell(hoveredButton, false);
            hoveredButton = null;
        }
        if (button != null && button != hoveredButton) {
            highlightCell(button, true);
            hoveredButton = button;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (currentMove == null)
            return;

        // Convert mouse point to BoardPanel coordinates (relative to this panel)
        Point panelPoint = SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), this);
        Component comp = getComponentAt(panelPoint);
        JButton button = (comp instanceof JButton) ? (JButton) comp : null;

        if (button == null) {
            removeDragVisual();
            currentMove = null;
            return;
        }

        Position destPos = buttonToPosition(button);

        // Execute move through GUI (handles validation, BE update, FE update)
        if (!destPos.equals(currentMove.getSourcePosition())) {
            // Call GUI.executeTurn to validate and execute the move
            boolean moveSuccessful = instance.executeTurn(
                    currentMove.getSourcePosition(),
                    destPos,
                    currentMove.getSelectedPiece());

            if (moveSuccessful) {
                // Only flip board for local games, NOT network games
                // Network games maintain fixed perspective per player
                if (!(instance instanceof game.Network)) {
                    // Delay board flip by 150ms for smoother visual transition
                    Timer timer = new Timer(150, evt -> flipBoard());
                    timer.setRepeats(false);
                    timer.start();
                }
            }
        }

        removeDragVisual();
        currentMove = null;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        JButton button = (JButton) e.getSource();
        Position pos = buttonToPosition(button);

        if (currentMove == null) {
            // First click - select piece
            Piece piece = instance.getBoard().getPieceAt(pos);
            if (piece != null) {
                clearHighlights();
                currentMove = new MoveState(piece, pos, MoveState.mouseEventType.CLICK, instance);
                selectedButton = button;
                highlightCell(selectedButton, true);
            }
        } else {
            // Second click - execute move through GUI
            if (!pos.equals(currentMove.getSourcePosition())) {
                // Call GUI.executeTurn to validate and execute the move
                boolean moveSuccessful = instance.executeTurn(
                        currentMove.getSourcePosition(),
                        pos,
                        currentMove.getSelectedPiece());

                if (moveSuccessful) {
                    // Delay clearing highlights by 150ms for smoother visual transition
                    Timer timer = new Timer(150, evt -> {
                        clearHighlights();
                        drawPieces();
                    });
                    timer.setRepeats(false);
                    timer.start();
                    currentMove = null;
                    return;
                }
            }
            clearHighlights();
            currentMove = null;
        }
    }

    // Helper to update drag label position
    private void updateDragLabelLocation(MouseEvent e) {
        if (dragLabel == null || dragOffset == null)
            return;
        JLayeredPane layeredPane = getRootPane().getLayeredPane();
        Point mouse = SwingUtilities.convertPoint((JButton) e.getSource(), e.getPoint(), layeredPane);
        int x = mouse.x - dragOffset.x;
        int y = mouse.y - dragOffset.y;
        dragLabel.setLocation(x, y);
        layeredPane.repaint();
    }

    // Helper to create a transparent image
    private Image createTransparentImage(Image img, float alpha, int width, int height) {
        Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        java.awt.image.BufferedImage bImg = new java.awt.image.BufferedImage(width, height,
                java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bImg.createGraphics();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2.drawImage(scaledImg, 0, 0, null);
        g2.dispose();
        return bImg;
    }

    // Helper to remove drag visual
    private void removeDragVisual() {
        if (dragLabel != null) {
            JLayeredPane layeredPane = getRootPane().getLayeredPane();
            layeredPane.remove(dragLabel);
            layeredPane.repaint();
            dragLabel = null;
            dragIcon = null;
            dragOffset = null;
        }
    }

    // Required but unused interface methods
    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (hoveredButton != null) {
            highlightCell(hoveredButton, false);
            hoveredButton = null;
        }
        // Clear hover info when mouse leaves the board
        gui.chessFrame frame = (gui.chessFrame) SwingUtilities.getWindowAncestor(this);
        if (frame != null) {
            frame.clearHoverInfo();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // Highlight the button being hovered over
        // Convert coordinates from the source button to this panel's coordinate space
        Point panelPoint = SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), this);
        Component comp = getComponentAt(panelPoint);
        JButton button = (comp instanceof JButton) ? (JButton) comp : null;

        if (hoveredButton != null && hoveredButton != button) {
            // Remove highlight from previously hovered button
            highlightCell(hoveredButton, false);
            hoveredButton = null;
        }
        if (button != null) {
            if (button != hoveredButton) {
                highlightCell(button, true);
                hoveredButton = button;
            }

            // Update hover info with piece information
            if (instance != null && instance.getBoard() != null) {
                Position pos = buttonToPosition(button);
                Piece piece = instance.getBoard().getPieceAt(pos);
                gui.chessFrame frame = (gui.chessFrame) SwingUtilities.getWindowAncestor(this);
                if (frame != null) {
                    if (piece != null) {
                        String pieceName = piece.getName();
                        String pieceColor = (piece.getColor() == utils.Color.WHITE) ? "White" : "Black";
                        frame.updateHoverInfo(pieceName, pieceColor);
                    } else {
                        // Clear hover info when hovering over empty cell
                        frame.clearHoverInfo();
                    }
                }
            }
        }
    }

    /**
     * Clears all pieces from the board display.
     */
    private void clearAllPieces() {
        for (int i = 0; i < getComponentCount(); i++) {
            if (getComponent(i) instanceof JButton) {
                JButton cellButton = (JButton) getComponent(i);
                cellButton.setIcon(null);
                cellButton.setText("");
            }
        }
    }
}
