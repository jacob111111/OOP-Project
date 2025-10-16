/**
 * Main class for the application.
 * TODO: Add class description and usage details.
 */
import java.util.Scanner;
import game.*;

public class Main {
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
        
        switch(choice){
            case 1:
                game = new Console();
                break;
            case 2:
                // game = new PvP(); // LAN PvP is not implemented
                System.out.println("Player vs Player mode is not yet implemented.");
                scnr.close();
                return;
            case 3:
                // game = new PvE(); // PvE is not implemented
                System.out.println("Player vs AI mode is not yet implemented.");
                scnr.close();
                return;
            default:
                System.out.println("Invalid choice. Exiting.");
                scnr.close();
                return;
        }
        
        if (game != null) {
            game.play();
        }
        scnr.close();
    }
}
