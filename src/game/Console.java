package game;

import java.util.Scanner;
import utils.Color;
import utils.GameType;

/**
 * Console class for console-based gameplay.
 * TODO: Add class description and usage details.
 */
public class Console extends Game {
    public Console(boolean isPvP, Color p1Color, Scanner scnr){
        super(isPvP, p1Color, scnr);
        this.gameType = GameType.CONSOLE;
        this.winner = null;
    }

    @Override
    public void play() {
        System.out.println("Starting console game...");
        // Main game loop
        while (getWinner() == null) {
            turn();
        }
        end(getWinner());
    }

    @Override
    public Color getWinner() {
        return winner;
    }

    @Override
    public void turn() {
        // TODO: Implement turn logic
        System.out.println(WhosTurn + "'s turn");
        
        // For now, just switch turns (you'll implement actual game logic here)
        if (WhosTurn == Color.WHITE) {
            WhosTurn = Color.BLACK;
        } else {
            WhosTurn = Color.WHITE;
        }
        if(board.checkMate)
        setWinner(WhosTurn);
        // TODO: Add win condition checking logic here
        // For testing purposes, let's end after a few turns
        // You should replace this with actual chess win conditions
    }
}
