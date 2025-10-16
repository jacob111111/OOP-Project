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
    Color WhosTurn = Color.WHITE;
    Board board;
    Scanner scnr;
    GameType gameType;

    public Game(boolean isPvP, Color p1Color){
        this.board = new Board(isPvP, p1Color);
        this.scnr = new Scanner(System.in);
    }
    // //getter
    // public Color getWinner(){ return winner; }

    //methods
    public void start(){
        System.out.println("what type of game do you want to play?");
        System.out.println("1. Console   2. Player vs Ai   3. Player vs Player ");
        System.out.print("Input 1-3 (currently only console works): ");

        int userResponse = scnr.nextInt();
        switch(userResponse){
            default: //console
                game = new Console();
                break;
            case 2: //PvE
                //not implemented
                break;
            case 3: //PvP
                //not implemented
                break;
        }
    }

    public void end(Color winner){

    }
    public void play(){
        try () {

            if (game != null) {
                while(game.getWinner() == null){
                    game.turn();
                }
            }
        }
    }
}
