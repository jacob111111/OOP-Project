package utils;

import java.io.Serializable;
import game.Game;

public class NetworkMessage implements Serializable {
    public NetworkMessageType type;
    public Position from;
    public Position to;
    public Boolean isValid;
    public Game gameState;
    public Color p1Color;
    public Color winner;
    public String welcomeMsg;
    
    // Constructor for move request
    public static NetworkMessage moveRequest(Position from, Position to) {
        NetworkMessage msg = new NetworkMessage();
        msg.type = NetworkMessageType.MOVE_REQUEST;
        msg.from = from;
        msg.to = to;
        return msg;
    }
    
    // Constructor for validation response
    public static NetworkMessage moveResponse(boolean valid) {
        NetworkMessage msg = new NetworkMessage();
        msg.type = NetworkMessageType.MOVE_RESPONSE;
        msg.isValid = valid;
        return msg;
    }

    // Constructor for syncing game state
    public static NetworkMessage gameSync(Game gameState) {
        NetworkMessage msg = new NetworkMessage();
        msg.type = NetworkMessageType.GAME_STATE_UPDATE;
        msg.gameState = gameState;
        return msg;
    }

    // Constructor for game complete notification
    public static NetworkMessage gameEnd(Color winner) {
        NetworkMessage msg = new NetworkMessage();
        msg.type = NetworkMessageType.GAME_END;
        msg.winner = winner;
        return msg;
    }

    // Constructor for handshake / connection confirmation
    public static NetworkMessage playerConnected(String welcomeMsg) {
        NetworkMessage msg = new NetworkMessage();
        msg.type = NetworkMessageType.PLAYER_CONNECTED;
        msg.welcomeMsg = welcomeMsg;
        return msg;
    }

    // Constructor for game intialization
    public static NetworkMessage intializeGame(Color p1Color) {
        NetworkMessage msg = new NetworkMessage();
        msg.type = NetworkMessageType.PLAYER_CONNECTED;
        msg.winner = p1Color;
        return msg;
    }

}