import java.util.Scanner;
import game.*;
import utils.Color;

public class Main {
    public static void main(String[] args) {
        Scanner scnr = new Scanner(System.in);
        /**
        System.out.println("Do you want to use the console or GUI?");
        System.out.println("1. GUI");
        System.out.println("2. Console");

        //int choice = scnr.nextInt();
        */
        int choice = 2;
        if(choice == 1) {
            Game game = null; 
            System.out.println("Welcome to the Game!");
            System.out.println("What type of game would you like to play?");
            System.out.println("1-PLayer or 2-Player");
            System.out.print("Enter 1, 2: ");

            choice = scnr.nextInt();

            System.out.println("Choose player-1 color:");
            System.out.println("1. White   2. Black");
            System.out.print("Input 1-2: ");
            int colorChoice = scnr.nextInt();
            Color p1Color = (colorChoice == 1) ? Color.WHITE : Color.BLACK;
            
            switch(choice){
                case 1:
                    game = new Console(false, p1Color, scnr);
                    break;
                case 2:
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
        else if(choice == 2) {
            javax.swing.SwingUtilities.invokeLater(() -> {
                new gui.chessFrame(); // Uses CLASSIC palette by default

            });
        }
    }
}
