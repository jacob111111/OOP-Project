package game;

import player.Server;
import player.Client;
import utils.Color;
import utils.NetworkMessageType;

public class Network extends GUI {
    private int port; // this value is set from a text box in the gui
    Server server;  // Player 1
    Client client;  // Player 2

    public Network(boolean isPvP, Color p1Color, int port) {
        super(isPvP, p1Color);
        this.port = port;
        
        // open server
        server = new Server(p1Color,port);

        // wait for client to connect
            // server.acceptClient();
        // Print client accepted

    }

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

    @Override
    public void turn() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'turn'");
    }
    
    // on move from user, run validation,
    // if good: make move update and propagate game state
    // If invalid: send message back saying itsinvalid
    public boolean recieveMove() {
        return false;
    }

    public void updateGameState() {
        // this would ensure both players are looking at the same board,
        // need function that 
    }

    public void getFlippedBoard() {
        // currently don't know which classes need this 
        // flips the gui display for the player on black
    }
}
