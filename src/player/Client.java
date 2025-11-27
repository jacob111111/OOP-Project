package player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import game.Game;
import utils.Color;
import utils.NetworkMessage;
import utils.NetworkMessageType;
import utils.Position;

public class Client extends Player{
    private Socket clientSocket = null;
    private ObjectInputStream in = null;
    private ObjectOutputStream out = null;

    private int port;

    public Client(Color color, String serverIP, int port) {
        super(color);
        this.port = port;
            
        try {
            clientSocket = new Socket(serverIP, port);
            
            out = new ObjectOutputStream(clientSocket.getOutputStream()); 
            out.flush();
            in = new ObjectInputStream(clientSocket.getInputStream());
            
            // Wait for server's connection confirmation
            NetworkMessage welcome = (NetworkMessage) in.readObject();
            if (welcome.type == NetworkMessageType.PLAYER_CONNECTED) {
                System.out.println("Successfully connected to server!");
                // Update GUI to show "Connected" status
                // Also update gui to show the correct board orientation based on p1 color
            }
            
        } catch(IOException | ClassNotFoundException e) {
            System.out.println(e);
        }
    }   

    public boolean sendMoveRequest(Position from, Position to) {
        try {
            // Send move request
            NetworkMessage request = NetworkMessage.moveRequest(from, to);
            out.writeObject(request);
            out.flush();
            
            // Receive response
            NetworkMessage response = (NetworkMessage) in.readObject();
            
            if (response.type == NetworkMessageType.MOVE_RESPONSE) {
                return response.isValid;
            }
            return false;
            
        } catch (IOException | ClassNotFoundException e) {
            return false;
        }
    }

    // 3. For initial sync or error recovery
    public Game receiveGameState() {
        try {
            return (Game) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    public NetworkMessage receiveInitialSync() {
        try {
            NetworkMessage syncMsg = (NetworkMessage) in.readObject();
            if (syncMsg.type == NetworkMessageType.INITIAL_SYNC) {
                return syncMsg;
            }
            return null;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e);
            return null;
        }
    }
}
