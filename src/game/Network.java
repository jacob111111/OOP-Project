package game;

import player.Server;
import player.Client;
import utils.Color;
import utils.NetworkMessageType;
import utils.NetworkMessage;
import utils.Position;

// THE FUNCTIONS NEED TO BE SORTED BASED ON WHETHER HOST/CLIENT CAN USE THEM


public class Network extends GUI {
    private int port;
    private Server server = null;
    private Client client = null;
    private boolean isHost;
    
    // Constructor for HOST mode
    public Network(boolean isPvP, Color p1Color, int port) {
        super(isPvP, p1Color);
        this.port = port;
        this.isHost = true;
        
        // Create server and wait for client
        server = new Server(p1Color, port);
        server.acceptClient(); // Blocks until client connects
    }
    
    // Constructor for CLIENT mode
    public Network(boolean isPvP, Color p1Color, String serverIP, int port) {
        super(isPvP, p1Color);
        this.port = port;
        this.isHost = false;
        
        client = new Client(p1Color, serverIP, port);
    }

    // THESE METHODS HANDLE THE MAIN GAMEPLAY LOOP NOT VERY APPLICABLE TO SERVER <--> CLIENT INTERACTION
    // SUBJECT TO CHANGE GIVEN NATURE OF NETWORKING

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

    // Methods that are identical regardless of mode
    @Override
    public void turn() {
        // GAME STATE MANAGEMENT NOT IMPLEMENTED YET, VAR NAMES SUBJECT TO CHANGE
        /**
        if (currentPlayer.getColor() == myColor) {
            waitForLocalMove();
        } else {
            waitForRemoteMove();
        }
        */
    }

    // SUBJECT TO CHANGE GIVEN NATURE OF NETWORKING

    
    // this would call the gameSync method of NetworkMessage.java
    // is only used during game start after player connects or if error occurs
    public void updateGameState() {
        // this would ensure both players are looking at the same board,
        // need function that 
    }

    public void getFlippedBoard() {
        // currently don't know which classes need this 
        // flips the gui display for the player on black
    }

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

    // JORDAN IS IMPLEMENTING THIS
}
