package board;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;

import piece.*;
import player.*;
import utils.Color;
import utils.Position;

/**
 * Represents the chess board and manages piece positions and game state.
 * 
 * The Board class is responsible for:
 * - Managing piece positions using an efficient position index for O(1) lookups
 * - Tracking captured pieces for both players
 * - Handling piece movement and capture mechanics
 * - Displaying the board state to players
 * - Managing player instances (human players and AI)
 * 
 * The board uses a coordinate system where positions are represented as
 * (x,y) coordinates with (0,0) at a1 and (7,7) at h8 in standard chess notation.
 * 
 */
public class Board {
    /** The white player instance */
    protected Player white, black;
    
    /** Dictionary tracking pieces captured by white player */
    private Dictionary<Piece, Integer> whiteHasCaptured = new Hashtable<>();
    
    /** Dictionary tracking pieces captured by black player */
    private Dictionary<Piece, Integer> blackHasCaptured = new Hashtable<>();
    
    /** Flag indicating if the game is in checkmate */
    protected boolean checkMate;
    
    /** Hash map for O(1) piece position lookups */
    private Map<Position, Piece> positionIndex = new HashMap<>();
    
    /**
     * Constructs a new chess board with players based on game mode.
     * 
     * Creates appropriate player instances based on whether this is a
     * Player vs Player game or Player vs AI game. For PvP mode, both
     * players are human. For PvE mode, one player is human and the other is AI.
     * 
     * @param isPvP true for Player vs Player mode, false for Player vs AI
     * @param P1Color the color that Player 1 will control (WHITE, BLACK, or RANDOM)
     */
    public Board(boolean isPvP, Color P1Color){
        if(isPvP){
            this.white = new Player(Color.WHITE);
            this.black = new Player(Color.BLACK);
            this.checkMate = false;
        }else{
            Random rand = new Random();
            if(P1Color == Color.RANDOM){ //coin flip decides users (p1's) color
                if(rand.nextBoolean()){
                    P1Color = Color.WHITE;
                }else{
                    P1Color = Color.BLACK;
                }
            }
            if(P1Color == Color.WHITE){ 
                //user chooses white
                this.white = new Player(Color.WHITE);
                this.black = new AI(Color.BLACK);
            }else{  
                //user chooses black
                this.white = new AI(Color.WHITE);
                this.black = new Player(Color.BLACK);
            }
        }
        
        // Initialize position index for O(1) lookups
        // Add all white pieces to index
        for (Piece piece : white.getCurrentPieces()) {
            positionIndex.put(piece.getPosition(), piece);
        }
        // Add all black pieces to index
        for (Piece piece : black.getCurrentPieces()) {
            positionIndex.put(piece.getPosition(), piece);
        }
    }

    
    /**
     * Gets the dictionary of pieces captured by the specified color.
     * 
     * @param colorOfPiece the color whose captures to retrieve (WHITE or BLACK)
     * @return Dictionary containing captured pieces and their IDs
     */
    public Dictionary<Piece, Integer> getCaptures(Color colorOfPiece){ 
        return colorOfPiece == Color.WHITE ? whiteHasCaptured : blackHasCaptured; 
    }
    
    /**
     * Gets the player instance for the specified color.
     * 
     * @param colorOfPiece the color of the player to retrieve (WHITE or BLACK)
     * @return The Player or AI instance for that color
     */
    public Player getPlayer(Color colorOfPiece){ 
        return colorOfPiece == Color.WHITE ? white : black; 
    }
    
    /**
     * Gets the piece at the specified position, if any.
     * 
     * Uses the position index for O(1) lookup performance.
     * 
     * @param pos the position to check
     * @return The piece at that position, or null if empty
     */
    public Piece getPieceAt(Position pos) {
        return positionIndex.get(pos);
    }
    
    /**
     * Checks if the specified square is empty.
     * 
     * @param pos the position to check
     * @return true if the square is empty, false if occupied
     */
    public boolean isSquareEmpty(Position pos) {
        return positionIndex.get(pos) == null;
    }
    
    /**
     * Gets the current checkmate status.
     * 
     * @return true if the game is in checkmate, false otherwise
     */
    public boolean getCheckMate(){ return checkMate;}

    /**
     * Updates a piece's position in both the piece object and the position index.
     * 
     * This method maintains consistency between the piece's internal position
     * and the board's position tracking system.
     * 
     * @param piece the piece being moved
     * @param oldPos the piece's previous position
     * @param newPos the piece's new position
     */
    public void updatePiecePosition(Piece piece, Position oldPos, Position newPos) {
        positionIndex.remove(oldPos);
        positionIndex.put(newPos, piece);
        piece.setPosition(newPos);
    }
    
    /**
     * Handles piece capture mechanics while maintaining position index consistency.
     * 
     * This method is called once a valid capture has been confirmed. It removes
     * the captured piece from the appropriate player's piece collection, adds it
     * to the capturing player's capture list, and updates the position index.
     * 
     * @param capturingPiece the piece performing the capture
     * @param capturePos the position where the capture occurs
     */
    public void capturePiece(Piece capturingPiece, Position capturePos) {
        Piece capturedPiece = positionIndex.get(capturePos);
        if (capturedPiece != null) {
            // Remove from appropriate player's piece list
            if (capturedPiece.getColor() == Color.WHITE) {
                white.getCurrentPieces().remove(capturedPiece);
                blackHasCaptured.put(capturedPiece, capturedPiece.hashCode()); // Using hashCode as ID
            } else {
                black.getCurrentPieces().remove(capturedPiece);
                whiteHasCaptured.put(capturedPiece, capturedPiece.hashCode()); // Using hashCode as ID
            }
        }
        // Update position index
        positionIndex.remove(capturingPiece.getPosition());
        positionIndex.put(capturePos, capturingPiece);
    }

    /**
     * Adds a captured piece to the specified capture dictionary.
     * 
     * @param dict the capture dictionary to add to
     * @param capPiece the piece that was captured
     */
    public void addPieceToCaptures(Dictionary<Piece, Integer> dict, Piece capPiece){
        dict.put(capPiece, capPiece.hashCode()); // Using hashCode as piece ID
    }

    /**
     * Displays the chess board from the perspective of the current player.
     * 
     * Shows the board with file letters (a-h) and rank numbers (1-8).
     * The board orientation changes based on whose turn it is:
     * - White players see the board with rank 1 at bottom, rank 8 at top
     * - Black players see the board with rank 8 at bottom, rank 1 at top
     * 
     * Pieces are displayed using their symbolic representation (e.g., "wK" for white king),
     * and empty squares are shown as "##".
     * 
     * @param whosMove the color of the current player (determines board orientation)
     */
    public void displayBoard(Color whosMove){ 
        System.out.println("  a  b  c  d  e  f  g  h");

        // Display board from black's perspective (rank 8 to 1) or white's (rank 1 to 8)
        // Clever loop construction to handle both orientations in one loop
        for(int rank = (whosMove == Color.WHITE ? 7 : 0); whosMove == Color.WHITE ? rank >= 0 : rank < 8; rank += (whosMove == Color.WHITE ? -1 : 1)) {
            
            System.out.print((rank + 1) + " ");
            
            for(int file = 0; file < 8; file++) {
                Position pos = new Position(file, rank);
                Piece piece = getPieceAt(pos);
                
                if(piece != null) {
                    System.out.print(piece.getDisplaySymbol() + " ");
                } else {
                    System.out.print("## ");
                }
            }
            System.out.println();
        }
        System.out.println("  a  b  c  d  e  f  g  h");
    }
}




                        //variable board area constructors - for shits and giggles
    // public Board(AI aiPlayer, Player PlayerPlayer, int whoFirst, int legnth, int width){
    //     whoFirst(aiPlayer, PlayerPlayer, whoFirst);
    //     this.length = length;
    //     this.width = width;
    // }
    // public Board(Player PlayerPlayer1, Player PlayerPlayer2, int whoFirst, int legnth, int width){
    //     whoFirst(PlayerPlayer1, PlayerPlayer2, whoFirst);
    //     this.length = length;
    //     this.width = width;
    // }