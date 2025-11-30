package player;

import java.io.Serializable;
import java.util.ArrayList;

import piece.*;
import utils.Color;
import utils.Position;

/**
 * Represents a chess player (human or AI) and manages their pieces.
 * 
 * The Player class is responsible for:
 * - Managing the player's collection of chess pieces
 * - Handling piece movement requests and validation
 * - Initializing starting piece positions
 * - Tracking the king's position for game logic
 * 
 * This class serves as the base class for both human players and AI players,
 * providing common functionality for piece management and movement.
 * 
 */
public class Player implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /** The color this player controls (WHITE or BLACK) */
    protected Color color;
    
    /** List of all pieces currently owned by this player */
    protected ArrayList<Piece> currentPieces;


    /**
     * Creates a new player with the specified color.
     * 
     * Initializes the player's piece collection and sets up all starting
     * pieces in their proper positions according to chess rules.
     * 
     * @param color the color this player will control (WHITE or BLACK)
     */
    public Player(Color color) {
        this.currentPieces = new ArrayList<Piece>();
        this.color = color;
        initializePieces();
    }
    
    /**
     * Gets the color this player controls.
     * 
     * @return The player's color (WHITE or BLACK)
     */
    public Color getColor() { return color; }
    
    /**
     * Gets the list of pieces currently owned by this player.
     * 
     * @return ArrayList of all pieces belonging to this player
     */
    public ArrayList<Piece> getCurrentPieces(){ return currentPieces; }
    
    /**
     * Finds and returns the position of this player's king.
     * 
     * Searches through the player's pieces to locate the king, which is
     * essential for check and checkmate calculations.
     * 
     * @return The position of the king, or null if not found (should never happen)
     */
    public Position getKingPosition() {
        for(Piece piece : currentPieces) {
            if(piece instanceof King) {
                return piece.getPosition();
            }
        }
        return null; // should never happen in a valid game
    }
    
    /**
     * Initializes all chess pieces in their starting positions.
     * 
     * Creates and positions all pieces according to standard chess setup:
     * - Back rank pieces (rooks, knights, bishops, queen, king)
     * - Eight pawns in front of the back rank
     * 
     * White pieces start on ranks 1-2, black pieces on ranks 7-8.
     */
    private void initializePieces() {
        int colorOffset = (color == Color.WHITE) ? 0 : 7;
        int pawnRow = (color == Color.WHITE) ? 1 : 6;
        
        currentPieces.add(new King(color, new Position(4, colorOffset)));
        currentPieces.add(new Queen(color, new Position(3, colorOffset)));

        currentPieces.add(new Rook(color, new Position(0, colorOffset)));
        currentPieces.add(new Rook(color, new Position(7, colorOffset)));

        currentPieces.add(new Bishop(color, new Position(2, colorOffset)));
        currentPieces.add(new Bishop(color, new Position(5, colorOffset)));

        currentPieces.add(new Knight(color, new Position(1, colorOffset)));
        currentPieces.add(new Knight(color, new Position(6, colorOffset)));

        for(int i = 0; i < 8; i++) {
            currentPieces.add(new Pawn(color, new Position(i, pawnRow)));
        }
    }
}