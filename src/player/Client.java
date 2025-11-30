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
    private transient Socket clientSocket = null;
    private transient ObjectInputStream in = null;
    private transient ObjectOutputStream out = null;

    public Client(Color color, String serverIP, int port) {
        super(color);
            
        try {
            clientSocket = new Socket(serverIP, port);
            
            // Set socket timeout for cleaner shutdown (1 second)
            clientSocket.setSoTimeout(1000);
            
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

    /**
     * Sends a move request to the server asynchronously (non-blocking).
     * The response will be received by the network listener thread.
     * 
     * @param from The starting position of the move
     * @param to The ending position of the move
     */
    public void sendMoveRequest(Position from, Position to) {
        try {
            NetworkMessage request = NetworkMessage.moveRequest(from, to);
            out.writeObject(request);
            out.flush();
        } catch (IOException e) {
            System.err.println("Failed to send async move request: " + e.getMessage());
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

    /**
     * Receives a move update from the server (opponent's move).
     * This is a blocking call that waits for the server to send a move.
     * 
     * @return NetworkMessage containing the move (from, to), or null if error/wrong type
     */
    public NetworkMessage receiveMoveUpdate() {
        try {
            NetworkMessage update = (NetworkMessage) in.readObject();
            if (update.type == NetworkMessageType.MOVE_UPDATE) {
                return update;
            }
            return null;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error receiving move update: " + e.getMessage());
            return null;
        }
    }

    /**
     * Receives any type of network message from the server.
     * Used by the network listener thread to handle multiple message types.
     * 
     * @return NetworkMessage of any type, or null if error or timeout
     */
    public NetworkMessage receiveMessage() {
        try {
            return (NetworkMessage) in.readObject();
        } catch (java.net.SocketTimeoutException e) {
            // Timeout is normal - allows thread to check if it should continue listening
            return null;
        } catch (IOException | ClassNotFoundException e) {
            // Only show error for non-timeout issues
            if (!(e instanceof java.net.SocketTimeoutException)) {
                System.err.println("Error receiving message: " + e.getMessage());
            }
            return null;
        }
    }

    /**
     * Closes all network resources (streams and socket).
     * Should be called when the game ends to prevent resource leaks.
     */
    public void close() {
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing input stream: " + e.getMessage());
        }

        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing output stream: " + e.getMessage());
        }

        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing client socket: " + e.getMessage());
        }
    }
}
