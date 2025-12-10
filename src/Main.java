import java.util.Scanner;
import game.*;
import utils.Color;

/**
 * Main entry point for the Chess Game application.
 * Allows users to choose between GUI and console modes.
 * 
 * @author Jordan Atchison, Jacob Atchison
 */
public class Main {

    /**
     * Launches the chess game with user-selected interface (GUI or console).
     * 
     * @param args Command line arguments (unused)
     */
    public static void main(String[] args) {
        Scanner scnr = new Scanner(System.in);

        System.out.println("Choose game interface:");
        System.out.println("1. GUI (Combined Board & Menu)");
        System.out.println("2. Console");
        System.out.print("Enter choice (1-2): ");

        int choice = scnr.nextInt();

        if (choice == 1) {
            // Launch GUI with combined board and menu
            javax.swing.SwingUtilities.invokeLater(() -> {
                new gui.ChessFrame(); // Launches with classic palette and empty board
            });
            scnr.close(); // Close scanner since GUI will handle input
        } else if (choice == 2) {
            // Console mode
            Console game = null;
            System.out.println("Welcome to the Console Chess Game!");
            System.out.println("What type of game would you like to play?");
            System.out.println("1-Player or 2-Player");
            System.out.print("Enter 1, 2: ");

            choice = scnr.nextInt();

            System.out.println("Choose player-1 color:");
            System.out.println("1. White   2. Black");
            System.out.print("Input 1-2: ");
            int colorChoice = scnr.nextInt();
            Color p1Color = (colorChoice == 1) ? Color.WHITE : Color.BLACK;

            switch (choice) {
                case 1:
                    game = new Console(false, p1Color, scnr);
                    break;
                case 2:
                    game = new Console(true, p1Color, scnr);
                    break;
                default:
                    System.out.println("Invalid choice, defaulting to 2-Player Console");
                    game = new Console(true, p1Color, scnr);
            }
            if (game != null) {
                game.play();
            }
            scnr.close();
        } else {
            System.out.println("Invalid choice. Exiting...");
            scnr.close();
        }
    }
}
