package game;

import java.util.Scanner;
import utils.Color;
import utils.GameType;
import utils.Position;
import player.Player;
import piece.Piece;

/**
 * Console class for console-based gameplay.
 * TODO: Add class description and usage details.
 */
public class Console extends Game {
    private Color winner;
    
    public Console(boolean isPvP, Color p1Color, Scanner scnr){
        super(isPvP, p1Color, scnr);
        this.gameType = GameType.CONSOLE;
        this.winner = null;
    }

    @Override
    public void play() {
        System.out.println("Starting console game...");
        // Main game loop
        while (getWinner() == null) {
            turn();
        }
        end(getWinner());
    }

    @Override
    public Color getWinner() {
        return winner;
    }

    @Override
    public void turn() {
        // Get the current player
        System.out.println(WhosTurn + "'s turn");
        System.out.println("");
        Player player = board.getPlayer(WhosTurn);
        board.displayBoard(WhosTurn);
        
        System.out.println("Input move");
        System.out.println("Example: from e2 to e4 (e2 /enter/ e4)");
        System.out.print("From: ");
        String fromSquare = scnr.next();     
        System.out.print("To: ");
        String toSquare = scnr.next();     

        // Convert chess notation to Position objects
        Position fromPosition = chessNotationToPosition(fromSquare);
        Position toPosition = chessNotationToPosition(toSquare);
        
        if (fromPosition == null || toPosition == null) {
            System.out.println("Invalid move notation! Use format like 'e2 e4'");
            return; // Don't switch turns, let player try again
        }
        
        // Get the piece at the starting position
        Piece pieceToMove = board.getPieceAt(fromPosition);
        
        if (pieceToMove == null) {
            System.out.println("No piece at position " + fromSquare + "!");
            return; // Don't switch turns, let player try again
        }
        
        // Check if the piece belongs to the current player
        if (pieceToMove.getColor() != WhosTurn) {
            System.out.println("That piece doesn't belong to you!");
            return; // Don't switch turns, let player try again
        }
        
        // Attempt to move the piece
        boolean moveSuccessful = player.movePiece(toPosition, pieceToMove);
        
        if (!moveSuccessful) {
            System.out.println("Invalid move! That piece cannot move to " + toSquare);
            return; // Don't switch turns, let player try again
        }
        
        System.out.println("Move successful: " + fromSquare + " to " + toSquare);
        
        // Check for checkmate before switching turns
        if(board.getCheckMate()) {
            // The current player is in checkmate, so the other player wins
            winner = (WhosTurn == Color.WHITE) ? Color.BLACK : Color.WHITE;
            System.out.println("Checkmate! " + winner + " wins!");
            return;
        }
        
        // Switch to the other player's turn
        if (WhosTurn == Color.WHITE) {
            WhosTurn = Color.BLACK;
        } else {
            WhosTurn = Color.WHITE;
        }
        
    }
    
    /**
     * Converts chess notation (like "e4") to a Position object
     * Chess board: a-h columns (0-7), 1-8 rows (0-7)
     * @param notation Chess notation string (e.g., "e4")
     * @return Position object or null if invalid notation
     */
    private Position chessNotationToPosition(String notation) {
        if (notation == null || notation.length() != 2) {
            return null;
        }
        
        char file = notation.toLowerCase().charAt(0); // column (a-h)
        char rank = notation.charAt(1); // row (1-8)
        
        // Validate input
        if (file < 'a' || file > 'h' || rank < '1' || rank > '8') {
            return null;
        }
        
        // Convert to 0-based coordinates
        int x = file - 'a'; // a=0, b=1, ..., h=7
        int y = rank - '1'; // 1=0, 2=1, ..., 8=7
        
        return new Position(x, y);
    }
}
