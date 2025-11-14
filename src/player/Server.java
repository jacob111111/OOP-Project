package player;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import utils.NetworkMessageType;
import utils.Color;

public class Server extends Player{
    private Socket clientSocket = null;
    private ServerSocket serverSocket = null;
    private DataInputStream in = null;

    private int port;

    public Server(Color color, int port) {
        super(color);
        this.port = port;

        try {
            serverSocket = new ServerSocket(this.port);
            // print server started
        } catch(IOException e)
        {
            // Instead of printing to console, print to a text box within the gui
            System.out.println(e);
        }
    }

    public Socket acceptClient() {
        // Do I need to Check if server socket is not null
        try {
            clientSocket = serverSocket.accept();
            return clientSocket;
        } 
        catch(IOException e) {
            System.out.println(e);
            return null;
        }
    }

    public void manageClientInput(NetworkMessageType message) {

    }

}
