package gui.mainMenu;

import javax.swing.JFrame;

import gui.MainPanel;
import gui.UIPalette;
import gui.UIStyle;

public class MenuPanel extends JFrame{
    private UIStyle style = new UIStyle();
    
    public MenuPanel(){
        this(UIPalette.CLASSIC);
    } 

    public void setup(){
        /*
         *         move this stuff to gui
         * System.out.println("Welcome to the Game!");
            System.out.println("What type of game would you like to play?");
            System.out.println("1. PVP Console");
            System.out.println("3. PVP Lan");
            System.out.println("2. Player vs AI");
            System.out.print("Enter 1, 2, or 3: ");

            int choice = scnr.nextInt();

            System.out.println("Choose your color:");
            System.out.println("1. White   2. Black");
            System.out.print("Input 1-2: ");
         */
    }
}
