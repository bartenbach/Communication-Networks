package net.alureon.assignment3;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class MultiUserChatServer {

    private static Vector<ClientHandler> clientPool = new Vector<>();
    private int port;

    public MultiUserChatServer(int port) {
        this.port = port;
    }

    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Started server: listening on port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Accepted new client");
                ClientHandler client = new ClientHandler(this, socket, socket.getInputStream(),
                        socket.getOutputStream());
                Thread clientThread = new Thread(client);
                clientThread.start();
            }
        } catch (IOException e) {
            System.out.println("Failed to bind to port: " + port);
            System.exit(1);
        }

    }

    public Vector<ClientHandler> getClientPool() {
        return clientPool;
    }

}
