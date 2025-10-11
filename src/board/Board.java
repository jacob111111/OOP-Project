package board;

import java.util.ArrayList;
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
    public Player white, black;
    private Dictionary<Piece, Integer> whiteHasCaptured = new Hashtable<>();
    private Dictionary<Piece, Integer> blackHasCaptured = new Hashtable<>();
    private int BoardSize = 64;

    //Basic constructors
    public Board(boolean isPvP, Color P1Color){
        if(isPvP){
            this.white = new Human();
            this.black = new Human();
        }else{
            Random rand = new Random();
            if(P1Color == Color.RANDOM){ //coin flip decides users (p1's) color
                if(rand.nextBoolean()){
                    P1Color = Color.WHITE;
                }else{
                    P1Color = Color.BLACK;
                }
            }
            if(P1Color == Color.WHITE){ 
                //user chooses white
                this.white = new Human();
                this.black = new AI();
            }else{  
                //user chooses black
                this.white = new AI();
                this.black = new Human();
            }
        }
    }
    

    //getters
    public Dictionary<Piece, Integer> getWhiteCaptures(){
        return whiteHasCaptured;
    }
    public Dictionary<Piece, Integer> getBlackCaptures(){
        return blackHasCaptured;
    }

    //setters
    public void addPieceToCaptures(Dictionary<Piece, Integer> dict, Piece capPiece){
        dict.put(capPiece, capPiece.getID());
    }


    //Methods
    public void displayBoard(boolean whitesMove){ 
        ArrayList<Piece> allCurrentPieces = new ArrayList<>(white.getCurrentPieces());
        allCurrentPieces.addAll(black.getCurrentPieces());

        
        if(whitesMove){//display white on the bottom (so print black first)
            for(int p=0; p<BoardSize; p++){
                
            }
        }else{
            for(int p=0; p< white.getCurrentPieces().size(); p++){

            }
        }

        /*
         * for(exisitng peices){
         * go through all exisiting peices and put them on the board using there built in position
         * }
         * [p1 - r1c1, p2 - r1c3, p3 - r1c4, ]
         * 
         */
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