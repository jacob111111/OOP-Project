/**
 * Main entry point for the Chess Game application.
 * 
 * This class handles the initial game setup, including game mode selection,
 * player color choice, and game initialization. It provides a console-based
 * interface for users to start playing chess.
 * 
 
 */
import java.util.Scanner;
import game.*;
import utils.Color;

public class Main {
    
    /**
     * Main method that starts the chess game application.
     * 
     * Presents the user with game mode options (Console PVP, LAN, Player vs AI),
     * allows color selection, and initializes the appropriate game instance.
     * Currently, only console mode is fully implemented.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        Game game = null; 
        Scanner scnr = new Scanner(System.in);
        System.out.println("Welcome to the Game!");
        System.out.println("What type of game would you like to play?");
        System.out.println("1. PVP Console");
        System.out.println("3. PVP Lan");
        System.out.println("2. Player vs AI");
        System.out.print("Enter 1, 2, or 3: ");

        int choice = scnr.nextInt();

        System.out.println("Choose your color:");
        System.out.println("1. White   2. Black");
        System.out.print("Input 1-2: ");
        int colorChoice = scnr.nextInt();
        Color p1Color = (colorChoice == 1) ? Color.WHITE : Color.BLACK;
        
        switch(choice){
            case 1:
                game = new Console(false, p1Color, scnr);
                break;
            case 2: // Lan  - not implemented
                System.out.println("LAN not yet implemented");
                game = new Console(false, p1Color, scnr);
                break;
            case 3: // PVE - not implemented
                System.out.println("Player vs AI not yet implemented");
                game = new Console(true, p1Color, scnr);
                break;
            default:
                System.out.println("Invalid choice, defaulting to Console");
                game = new Console(false, p1Color, scnr);
        }
        
        if (game != null) {
            game.play();
        }
        scnr.close();
    }
}
