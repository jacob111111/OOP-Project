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
 * Board class representing the game board.
 * TODO: Add class description and usage details.
 */

public class Board {
    protected Player white, black;
    private Dictionary<Piece, Integer> whiteHasCaptured = new Hashtable<>();
    private Dictionary<Piece, Integer> blackHasCaptured = new Hashtable<>();
    
    private Map<Position, Piece> positionIndex = new HashMap<>();
    
    //Basic constructors
    public Board(boolean isPvP, Color P1Color){
        if(isPvP){
            this.white = new Human(Color.WHITE);
            this.black = new Human(Color.BLACK);
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
                this.white = new Human(Color.WHITE);
                this.black = new AI(Color.BLACK);
            }else{  
                //user chooses black
                this.white = new AI(Color.WHITE);
                this.black = new Human(Color.BLACK);
            }
        }
        
        // Initialize position index for O(1) lookups
        initializePositionIndex();
    }
    
    private void initializePositionIndex() {
        // Add all white pieces to index
        for (Piece piece : white.getCurrentPieces()) {
            positionIndex.put(piece.getPosition(), piece);
        }
        // Add all black pieces to index
        for (Piece piece : black.getCurrentPieces()) {
            positionIndex.put(piece.getPosition(), piece);
        }
    }
    
    //getters
    public Dictionary<Piece, Integer> getCaptures(Color colorOfPiece){ 
        return colorOfPiece == Color.WHITE ? whiteHasCaptured : blackHasCaptured; 
    }
    public Player getPlayer(Color colorOfPiece){ 
        return colorOfPiece == Color.WHITE ? white : black; 
    }
    
    public Piece getPieceAt(Position pos) {
        return positionIndex.get(pos);
    }
    
    public boolean isSquareEmpty(Position pos) {
        return positionIndex.get(pos) == null;
    }
    
    // Call this method whenever a piece moves to keep index updated
    public void updatePiecePosition(Piece piece, Position oldPos, Position newPos) {
        positionIndex.remove(oldPos);
        positionIndex.put(newPos, piece);
    }
    
    // Method to handle captures while maintaining index
    // Called once valid capture confirmed
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

    //setters
    public void addPieceToCaptures(Dictionary<Piece, Integer> dict, Piece capPiece){
        dict.put(capPiece, capPiece.hashCode()); // Using hashCode as piece ID
    }

    //Methods
    public void displayBoard(Color whosMove){ 
        System.out.println("  a b c d e f g h");
        
        // Display board from black's perspective (rank 8 to 1) or white's (rank 1 to 8)
        //something neat i found you could do to reduce the amount of for loops you write. absolutely awful to look at though
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
        System.out.println("  a b c d e f g h");
    }

    //Helper functions
    private boolean canPieceAttackKing(Piece opponentPiece, Position kingPos){
        if (opponentPiece instanceof Queen) {
            return checkQueenAttack(opponentPiece, kingPos);
        } else if (opponentPiece instanceof Rook) {
            return checkRookAttack(opponentPiece, kingPos);
        } else if (opponentPiece instanceof Bishop) {
            return checkBishopAttack(opponentPiece, kingPos);
        } else if (opponentPiece instanceof Knight) {
            return checkKnightAttack(opponentPiece, kingPos);
        } else if (opponentPiece instanceof Pawn) {
            return checkPawnAttack(opponentPiece, kingPos);
        }
        return false;
    }
    
    // Stub implementations - you'll need to implement these based on piece movement rules
    private boolean checkQueenAttack(Piece queen, Position kingPos) {
        // TODO: Implement queen attack logic (combines rook + bishop)
        return false;
    }
    
    private boolean checkRookAttack(Piece rook, Position kingPos) {
        // TODO: Implement rook attack logic (horizontal/vertical lines)
        return false;
    }
    
    private boolean checkBishopAttack(Piece bishop, Position kingPos) {
        // TODO: Implement bishop attack logic (diagonal lines)
        return false;
    }
    
    private boolean checkKnightAttack(Piece knight, Position kingPos) {
        // TODO: Implement knight attack logic (L-shaped moves)
        return false;
    }
    
    private boolean checkPawnAttack(Piece pawn, Position kingPos) {
        // TODO: Implement pawn attack logic (diagonal captures)
        return false;
    }
}




                        //variable board area constructors - for shits and giggles
    // public Board(AI aiPlayer, Human humanPlayer, int whoFirst, int legnth, int width){
    //     whoFirst(aiPlayer, humanPlayer, whoFirst);
    //     this.length = length;
    //     this.width = width;
    // }
    // public Board(Human humanPlayer1, Human humanPlayer2, int whoFirst, int legnth, int width){
    //     whoFirst(humanPlayer1, humanPlayer2, whoFirst);
    //     this.length = length;
    //     this.width = width;
    // }