package game;

import player.Server;
import piece.Piece;
import player.Client;
import utils.Color;
import utils.NetworkMessageType;
import utils.NetworkMessage;
import utils.Position;

public class Network extends GUI {
    private int port;
    private Server server = null;
    private Client client = null;
    private boolean isHost;
    private Color myColor;
    
    // ============================================================================
    // CONSTRUCTORS
    // ============================================================================
    
    // Constructor for HOST mode
    public Network(boolean isPvP, Color p1Color, int port) {
        super(isPvP, p1Color);
        this.port = port;
        this.isHost = true;
        this.myColor = p1Color;
        
        server = new Server(p1Color, port);
        server.acceptClient(); // Block until client connects
        
        // Send initial game state to client for sync
        Color clientColor = (p1Color == Color.WHITE) ? Color.BLACK : Color.WHITE;
        server.sendInitialSync(this, clientColor);
    }
    
    // Constructor for CLIENT mode
    public Network(boolean isPvP, Color p1Color, String serverIP, int port) {
        super(isPvP, p1Color);
        this.port = port;
        this.isHost = false;
        
        client = new Client(p1Color, serverIP, port);
        
        // Receive initial sync from server
        NetworkMessage syncMsg = client.receiveInitialSync();
        if (syncMsg != null) {
            // Update client's color based on server assignment
            this.myColor = syncMsg.clientColor;
            // Sync game state
            if (syncMsg.gameState != null) {
                this.board = syncMsg.gameState.getBoard();
                this.WhosTurn = syncMsg.gameState.WhosTurn;
            }
        }
    }

    // ============================================================================
    // SHARED METHODS (Both host and client use)
    // ============================================================================

    @Override
    public void end(Color winner) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'end'");
    }

    @Override
    public void play() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'play'");
    }

    /**
     * Handles 
     * 
     */
    @Override
    public void turn() {
        if (currentPlayer.getColor() == myColor) {
            
        } 
        else {

        }
    }

    
    // Syncs game state between host and client (for error recovery)
    public void updateGameState() {
        if (isHost) {
            // Host sends current game state to client
            Color clientColor = (myColor == Color.WHITE) ? Color.BLACK : Color.WHITE;
            server.sendInitialSync(this, clientColor);
        } else {
            // Client receives updated game state from host
            NetworkMessage syncMsg = client.receiveInitialSync();
            if (syncMsg != null && syncMsg.gameState != null) {
                this.board = syncMsg.gameState.getBoard();
                this.WhosTurn = syncMsg.gameState.WhosTurn;
            }
        }
    }

    public boolean shouldFlipBoard() {
        // Returns true if this player is black (board should be flipped)
        return myColor == Color.BLACK;
    }
    
    public Color getMyColor() {
        return myColor;
    }

    // ============================================================================
    // HOST-ONLY METHODS (Server-specific functionality)
    // ============================================================================

    // Handle incoming move request from client (only for host)
    public void handleMoveRequest() {
        if (!isHost) {
            throw new IllegalStateException("Only server can handle move requests");
        }

        NetworkMessage request = server.receiveMoveRequest();
        if (request != null) {
            // JORDAN YOU MAY NEED TO CHANGE THIS DEPENDING ON HOW YOU IMPLEMENT THE VALIDATION
            boolean isValidMove = validateMove(request.from, request.to);
            
            server.sendMoveResponse(isValidMove);
        }
    }

    // JORDAN IS IMPLEMENTING THIS
    // Validation logic (implement based on your game rules)
    private boolean validateMove(utils.Position from, utils.Position to) {
        // TODO: Implement actual move validation logic
        // This should check if the move is legal according to chess rules
        // For now, returning true as placeholder
        return true;
    }

    // ============================================================================
    // CLIENT-ONLY METHODS (Client-specific functionality)
    // ============================================================================

    // JORDAN IS IMPLEMENTING THIS
}
