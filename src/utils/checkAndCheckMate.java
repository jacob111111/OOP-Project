package utils;
import java.util.ArrayList;
import board.*;
import piece.*;
import player.*;

/*
 * This class contains the function for checking if moving your own peice will check your king or if it wil check the oppponents king
 * 
 */
public class checkAndCheckMate extends Board{
    public checkAndCheckMate(boolean isPvP, Color p1Color) {
        super(isPvP, p1Color);
    }
    
    //did you just check yourself?
    public boolean isAttackPossibleOnKCurrentPlayersKing(Position kingPos, Color opponentsColor){ //the king's pos is the opponents king
        Player playerToCheck = getPlayer(opponentsColor);
        ArrayList<Piece> playerPieces = playerToCheck.getCurrentPieces();
        for(Piece piece : playerPieces){
            if (piece instanceof Queen) {
                if (checkQueenAttack(piece, kingPos)) return true;
            } else if (piece instanceof Rook) {
                if (checkRookAttack(piece, kingPos)) return true;
            } else if (piece instanceof Bishop) {
                if (checkBishopAttack(piece, kingPos)) return true;
            }
        }
     //start with queen for example. check if king is in move list. yes, then check if position that piece wants to move from is in move list. yes, then run checkSquaresBetween   
    }
    

    //did you check the opponents king?
    /**
     * @param kingPos position of opponnets king
     * @param pieceToCheck piece to investigate for checkmate/check
     * @return boolean stating whether the king is in check. checkmate variable will be updated if king is in checkmate
     */
    public boolean isOpponentKingInCheck(Position kingPos, Piece pieceToCheck){ 
        // for(Piece piece : playerPieces){
        //     if (piece instanceof Queen) {
        //         if (checkQueenAttack(piece, kingPos)) return true;
        //     } else if (piece instanceof Rook) {
        //         if (checkRookAttack(piece, kingPos)) return true;
        //     } else if (piece instanceof Bishop) {
        //         if (checkBishopAttack(piece, kingPos)) return true;
        //     } else if (piece instanceof Knight) {
        //         if (checkKnightAttack(piece, kingPos)) return true;
        //     } else if (piece instanceof Pawn) {
        //         if (checkPawnAttack(piece, kingPos)) return true;
        //     }
        // }
        // return false;
        return true;
        //revist discovered checks

    }



    //has a king been put in check?
    /**
     * @param kingPos position of king to investigate
     * @param pieceToCheck piece to investigate for checkmate/check
     * @return boolean stating whether the king is in check. checkmate variable will be updated if king is in checkmate
     */
    public void isKingInCheckMate(Position kingPos, Piece pieceCheckingKing){
        if(pieceCheckingKing.getPossibleMoves().indexOf(kingPos) == -1){ return; } //piece that was moved isnt attacking king
        if(getPieceAt(kingPos).getPossibleMoves().size() !=0){ return; }; //king can move
        


        //figure out if king is in checkmate if not then call isOpponentKingInCheck

    }


    //Helper Methods

    /**
    * check between king and moved piece's hash index. king on 28 and rook on 32, check 31-27 for pieces via find peice. nothing = isCheck
    */
    private boolean checkSquaresBetween(){

        return false;
    }


    /**
    * shorthand functions to systematically investigate peices ability to attack king. (i.e pawn is >2 square away so move on immediately )
    */
    private boolean checkQueenAttack(Piece queen, Position kingPos) {
        // TODO: Implement queen attack logic (combines rook + bishop)
        return false;
    }
    
    private boolean checkRookAttack(Piece rook, Position kingPos) {
        // TODO: Implement rook attack logic (horizontal/vertical lines)
        return false;
    }
    
    private boolean checkBishopAttack(Piece bishop, Position kingPos) {
        // TODO: Implement bishop attack logic (diagonal lines)
        return false;
    }
    
    private boolean checkKnightAttack(Piece knight, Position kingPos) {
        // TODO: Implement knight attack logic (L-shaped moves)
        return false;
    }
    
    private boolean checkPawnAttack(Piece pawn, Position kingPos) {
        // TODO: Implement pawn attack logic (diagonal captures)
        return false;
    } 
}
