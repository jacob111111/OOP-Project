package game;

import java.util.Scanner;
import utils.Color;
import utils.Position;
import piece.Piece;

/**
 * Console-based chess game with text interface.
 * Handles user input via chess notation (e.g., "e2 e4").
 */
public class Console extends Game {
    // Scanner for reading user input
    protected Scanner scnr;

    /**
     * Creates a console game.
     * 
     * @param isPvP   true for Player vs Player
     * @param p1Color player 1's color
     * @param scnr    Scanner for user input
     */
    public Console(boolean isPvP, Color p1Color, Scanner scnr) {
        super(isPvP, p1Color);
        this.winner = null;
        this.scnr = scnr;
        // Initialize move validator from board
        setValidMoveDetector(board.getCheckmateDetector());
    }

    /**
     * Displays the board from current player's perspective.
     * White sees rank 1 at bottom, black sees rank 8 at bottom.
     * 
     * @param whosMove current player's color (determines orientation)
     */
    public void displayBoard(Color whosMove) {
        System.out.println("  a  b  c  d  e  f  g  h");

        // Display board from black's perspective (rank 8 to 1) or white's (rank 1 to 8)
        // Clever loop construction to handle both orientations in one loop
        for (int rank = (whosMove == Color.WHITE ? 7 : 0); whosMove == Color.WHITE ? rank >= 0
                : rank < 8; rank += (whosMove == Color.WHITE ? -1 : 1)) {

            System.out.print((rank + 1) + " ");

            for (int file = 0; file < 8; file++) {
                Position pos = new Position(file, rank);
                Piece piece = board.getPieceAt(pos);

                if (piece != null) {
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
     * Main game loop - processes turns until game ends.
     */
    public void play() {
        System.out.println("Starting console game...");
        // Main game loop
        while (getWinner() == null) {
            turn();
        }
        end(getWinner());
    }

    public void end(Color winner) {
        // TODO
    }

    /**
     * Processes one turn: display board, get input, validate and execute move.
     * Checks for checkmate after move and switches turns if successful.
     */
    public void turn() {
        System.out.println(WhosTurn + "'s turn");
        System.out.println("");
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

        // Validate and execute the move
        boolean moveSuccessful = board.attemptMove(fromPosition, toPosition, pieceToMove);

        if (!moveSuccessful) {
            System.out.println("Invalid move! That piece cannot move to " + toSquare);
            return;
        }

        System.out.println("Move successful: " + fromSquare + " to " + toSquare);

        // Check if opponent is in checkmate after this move
        player.Player opponent = getOpponentPlayer();

        if (validMoveDetector.isCheckmate(opponent.getColor())) {
            winner = WhosTurn; // Current player wins by checkmating opponent
            System.out.println("Checkmate! " + winner + " wins!");
            return;
        }

        // Switch to next player's turn
        switchTurn();
    }
}
