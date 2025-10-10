package board;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Random;

import piece.*;
import player.*;
import utils.Color;

/**
 * Board class representing the game board.
 * TODO: Add class description and usage details.
 */

public class Board {
    //private int length, width;
    public Player playerA, playerB;
    private Dictionary<Piece, Integer> whiteHasCaptured = new Hashtable<>();
    private Dictionary<Piece, Integer> blackHasCaptured = new Hashtable<>();

    //Basic constructors
    public Board(boolean isPvP, Color decideWhite){
        if(isPvP){
            this.playerA = new Human();
            this.playerB = new Human();
        }else{
            this.playerA = new Human();
            this.playerB = new AI();
        }
        whoFirst(decideWhite);
    }

    //getters
    public Dictionary<Piece, Integer> getWhiteCaptures(){
        return whiteHasCaptured;
    }
    public Dictionary<Piece, Integer> getBlackCaptures(){
        return blackHasCaptured;
    }
    //setters
    


    //Utils
    private void whoFirst(Color whoFirst){
        Random rand = new Random();
       
        switch(whoFirst){
            case Color.WHITE: //user chooses white
                playerA.setColor(Color.WHITE);
                playerB.setColor(Color.BLACK);
                break;
            case Color.BLACK:  //user chooses black
                playerA.setColor(Color.BLACK);
                playerB.setColor(Color.WHITE);
                break;
            case Color.RANDOM:
                if(rand.nextBoolean()){ //user wins coin filp
                    playerA.setColor(Color.WHITE);
                    playerB.setColor(Color.BLACK);
                }else{  //user looses coin filp
                    playerA.setColor(Color.BLACK);
                    playerB.setColor(Color.WHITE);
                }
                break;
        }
    }




























        
                        //variable board area constructors - for shits and giggles
    // public Board(AI aiPlayer, Human humanPlayer, int whoFirst, int legnth, int width){
    //     whoFirst(aiPlayer, humanPlayer, whoFirst);
    //     this.length = length;
    //     this.width = width;
    // }
    // public Board(Human humanPlayer1, Human humanPlayer2, int whoFirst, int legnth, int width){
    //     whoFirst(humanPlayer1, humanPlayer2, whoFirst);
    //     this.length = length;
    //     this.width = width;
    // }
}
