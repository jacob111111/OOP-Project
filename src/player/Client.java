package player;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import utils.Color;

public class Client extends Player{
    private Socket clientSocket = null;
    private DataInputStream in = null;

    private int port;

    public Client(Color color, int port) {
        super(color);
        this.port = port;

        try {
            serverSocket = new ServerSocket(port);
            // print server started

            clientSocket = serverSocket.accept();
            // Print client accepted



        } catch(IOException e)
        {
            // Instead of printing to console, print to a text box within the gui
            System.out.println(e);
        }
    }

    

}
