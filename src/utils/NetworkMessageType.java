package utils;

public enum NetworkMessageType {
    MOVE_REQUEST, // Client ask Server for move validation
    MOVE_RESPONSE, // Server replies with move validation result
    MOVE_UPDATE, // Server informing client of their move
    GAME_STATE_UPDATE, // Full game state sync
    INITIAL_SYNC,
    PLAYER_CONNECTED,
    GAME_END
}