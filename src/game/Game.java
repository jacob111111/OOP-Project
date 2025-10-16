package game;

import java.util.Scanner;

import board.Board;
import player.*;
import utils.Color;
import utils.GameType;

/**
 * Game class for managing game state.
 * TODO: Add class description and usage details.
 */
public abstract class Game {
    protected Color WhosTurn = Color.WHITE;
    protected Board board;
    protected Scanner scnr;
    protected GameType gameType;
    protected Color winner;

    public Game(boolean isPvP, Color p1Color, Scanner scnr){
        this.board = new Board(isPvP, p1Color);
        this.scnr = scnr;
    }

    //setter
    public void setWinner(Color winnerColor){ this.winner = winnerColor; } 

    //methods
    public static Game createGame(){
        Scanner scnr = new Scanner(System.in);
        System.out.println("What type of game do you want to play?");
        System.out.println("1. Console   2. Player vs AI   3. Player vs Player ");
        System.out.print("Input 1-3: ");

        int userResponse = scnr.nextInt();
        
        // Ask for player color (common for all game types)
        System.out.println("Choose your color:");
        System.out.println("1. White   2. Black");
        System.out.print("Input 1-2: ");
        int colorChoice = scnr.nextInt();
        Color p1Color = (colorChoice == 1) ? Color.WHITE : Color.BLACK;
        
        switch(userResponse){
            case 1: // Console
                return new Console(false, p1Color, scnr); // isPvP = false for console mode
            case 2: // PvE (Player vs AI)
                // You'll need to create a PvE class similar to Console
                System.out.println("Player vs AI not yet implemented");
                return new Console(false, p1Color, scnr); // Fallback to console for now
            case 3: // PvP (Player vs Player)
                // You'll need to create a PvP class similar to Console
                System.out.println("Player vs Player not yet implemented");
                return new Console(true, p1Color, scnr); // isPvP = true for PvP mode
            default:
                System.out.println("Invalid choice, defaulting to Console mode");
                return new Console(false, p1Color, scnr);
        }
    }

    public void end(Color winner){

    }
    public abstract void play();
    
    // Abstract methods that subclasses need to implement
    public abstract Color getWinner();
    public abstract void turn();
}
