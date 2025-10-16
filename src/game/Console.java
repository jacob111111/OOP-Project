package game;

import utils.GameType;

/**
 * Coop class for cooperative gameplay.
 * TODO: Add class description and usage details.
 */
public class Console extends Game {
    
    public Console(){
        super(false, WhosTurn)
        this.gameType = GameType.CONSOLE;
    }

}
