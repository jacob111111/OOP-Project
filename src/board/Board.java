package board;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Random;

import piece.*;
import player.*;
import utils.Color;
import utils.Position;

/**
 * Board class representing the game board.
 * TODO: Add class description and usage details.
 */

public class Board {
    protected Player white, black;
    private Dictionary<Piece, Integer> whiteHasCaptured = new Hashtable<>();
    private Dictionary<Piece, Integer> blackHasCaptured = new Hashtable<>();
    
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
    public Dictionary<Piece, Integer> getCaptures(Color colorOfPiece){ 
        return colorOfPiece == Color.WHITE ? whiteHasCaptured : blackHasCaptured; 
    }
    public Player getPlayer(Color colorOfPiece){ return colorOfPiece == Color.WHITE ? white : black; }

    //setters
    public void addPieceToCaptures(Dictionary<Piece, Integer> dict, Piece capPiece){
        dict.put(capPiece, capPiece.getID());
    }


    //Methods
    public Position movePiece(Position possibleMove, Piece pieceToMove){
        if(pieceToMove.findPossibleMoves().contains(possibleMove)){

        }
    }


    public void displayBoard(Color whosMove){ 
        ArrayList<Piece> allCurrentPieces = new ArrayList<>(white.getCurrentPieces());
        allCurrentPieces.addAll(black.getCurrentPieces());
        //call Piceces sortByRowCol()

        StringBuilder tempRow = new StringBuilder();

        //display white on the bottom (so print black first)
        for(int rowNum=0; rowNum< allCurrentPieces.size(); rowNum++){
                while(allCurrentPieces.get(rowNum).getPosition().getX() == rowNum){
                    tempRow.append(rowNum);
                    tempRow.append(allCurrentPieces.get(rowNum).getName());
                    rowNum++;
                }
                if(whosMove == Color.BLACK){
                    tempRow.reverse();
                }
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