package game;

import java.util.Scanner;
import utils.Color;
import utils.Position;
import utils.CheckmateDetector;
import player.Player;
import piece.Piece;

/**
 * Console-based implementation of the chess game.
 * 
 * This class provides a text-based user interface for playing chess in the console.
 * It handles user input for moves, displays the board, validates moves, and manages
 * turn progression. Players enter moves using standard chess notation (e.g., "e2 e4").
 * 
 * The class extends the Game base class and implements the console-specific
 * versions of the abstract methods for game flow and turn management.
 * 
 */
public class Console extends Game {
    /** Scanner for reading user input */
    protected Scanner scnr;

    /** The winner of the console game */
    private Color winner;
    
    /**
     * Creates a new Console game instance.
     * 
     * @param isPvP true for Player vs Player mode, false for single player
     * @param p1Color the color that player 1 will control
     * @param scnr Scanner for reading user input from console
     */
    public Console(boolean isPvP, Color p1Color, Scanner scnr){
        super(isPvP, p1Color);
        this.winner = null;
        this.scnr = scnr;
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
                Piece piece = board.getPieceAt(pos);
                
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

    /**
     * Starts and manages the main game loop for console play.
     * 
     * Displays startup message and continues processing turns until
     * a winner is determined, then displays the end game message.
     */
    @Override
    public void play() {
        System.out.println("Starting console game...");
        // Main game loop
        while (getWinner() == null) {
            turn();
        }
        end(getWinner());
    }

    public void end(Color winner) {
        
    }
    
    /**
     * Gets the winner of the current game.
     * 
     * @return The color of the winner, or null if game is ongoing
     */
    @Override
    public Color getWinner() {
        return winner;
    }

    /**
     * Processes a single turn in the console game.
     * 
     * Displays the current player's turn, shows the board, prompts for move input,
     * validates the move, executes it if valid, and switches to the next player.
     * Handles move validation including:
     * - Chess notation format validation
     * - Piece existence at source position
     * - Piece ownership verification
     * - Move legality checking
     * 
     * If an invalid move is entered, the player is prompted to try again without
     * switching turns.
     */
    @Override
    public void turn() {
        System.out.println(WhosTurn + "'s turn");
        System.out.println("");
        Player player = board.getPlayer(WhosTurn);
        displayBoard(WhosTurn);
        
        System.out.println("Input move");
        System.out.println("Example: from e2 to e4 (e2 /enter/ e4)");
        System.out.print("From: ");
        String fromSquare = scnr.next();     
        System.out.print("To: ");
        String toSquare = scnr.next();     

        // Convert chess notation to Position objects
        Position fromPosition = board.chessNotationToPosition(fromSquare);
        Position toPosition = board.chessNotationToPosition(toSquare);
        
        if (fromPosition == null || toPosition == null) {
            System.out.println("Invalid move notation! Use format like 'e2 e4'");
            return;
        }
        
        Piece pieceToMove = board.getPieceAt(fromPosition);
        
        if (pieceToMove == null) {
            System.out.println("No piece at position " + fromSquare + "!");
            return;
        }
        
        if (pieceToMove.getColor() != WhosTurn) {
            System.out.println("That piece doesn't belong to you!");
            return;
        }

        // Validate the move first
        boolean moveSuccessful = player.attemptMove(toPosition, pieceToMove);
        
        if (!moveSuccessful) {
            System.out.println("Invalid move! That piece cannot move to " + toSquare);
            return;
        }
        
        // TODO: Add check validation - ensure move doesn't put own king in check
        // This would require temporarily making the move, checking if king is in check,
        // then undoing if invalid
        
        // Execute the move
        board.updatePiecePosition(pieceToMove, fromPosition, toPosition);
        System.out.println("Move successful: " + fromSquare + " to " + toSquare);
        
        // Check if opponent is in checkmate after this move
        CheckmateDetector detector = new CheckmateDetector(board);
        Color opponentColor = (WhosTurn == Color.WHITE) ? Color.BLACK : Color.WHITE;
        
        if (detector.isCheckmate(opponentColor)) {
            winner = WhosTurn; // Current player wins by checkmating opponent
            System.out.println("Checkmate! " + winner + " wins!");
            return;
        }
        
        if (WhosTurn == Color.WHITE) {
            WhosTurn = Color.BLACK;
        } 
        else { WhosTurn = Color.WHITE; }
    }
}
