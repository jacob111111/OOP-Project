package player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import utils.NetworkMessageType;
import utils.NetworkMessage;
import utils.Color;

public class Server extends Player{
    private Socket clientSocket = null;
    private ServerSocket serverSocket = null;
    private ObjectInputStream in = null;
    private ObjectOutputStream out = null;

    private int port;

    public Server(Color color, int port) {
        super(color);
        this.port = port;

        try {
            serverSocket = new ServerSocket(this.port);

        } catch(IOException e)
        {
            // Instead of printing to console, print to a text box within the gui
            System.out.println(e);
        }
    }

    public Socket acceptClient() {
        try {
            clientSocket = serverSocket.accept();
            
            // Set socket timeout for cleaner shutdown (1 second)
            clientSocket.setSoTimeout(1000);
            
            // Set up streams
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(clientSocket.getInputStream());
            
            // Send connection confirmation
            NetworkMessage welcomeMsg = NetworkMessage.playerConnected("Player joined!");
            out.writeObject(welcomeMsg);
            out.flush();
            
            return clientSocket;
        } 
        catch(IOException e) {
            System.out.println(e);
            return null;
        }
    }

    public void sendMoveResponse(boolean isValidMove) {
        try {
            NetworkMessage response = NetworkMessage.moveResponse(isValidMove);
            out.writeObject(response);
            out.flush();
            
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
    /**
     * Sends a validated move update to the client.
     * Used when the server makes a move and needs to notify the client.
     * 
     * @param from The starting position of the move
     * @param to The ending position of the move
     */
    public void sendMoveUpdate(utils.Position from, utils.Position to) {
        try {
            NetworkMessage moveUpdate = NetworkMessage.moveUpdate(from, to);
            out.writeObject(moveUpdate);
            out.flush();
            
        } catch (IOException e) {
            System.out.println("Error sending move update to client: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendInitialSync(game.Game gameState, Color clientColor) {
        try {
            NetworkMessage syncMsg = NetworkMessage.initialSync(gameState, clientColor);
            out.writeObject(syncMsg);
            out.flush();
            
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public NetworkMessage receiveMoveRequest() {
        try {
            NetworkMessage request = (NetworkMessage) in.readObject();
            if (request.type == NetworkMessageType.MOVE_REQUEST) {
                return request;
            }
            return null;
        } catch (java.net.SocketTimeoutException e) {
            // Timeout is normal - allows thread to check if it should continue listening
            return null;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * Closes all network resources (streams and sockets).
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

        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing server socket: " + e.getMessage());
        }
    }
}
