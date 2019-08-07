package net.alureon.assignment3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MultiUserChatClient {

    private static final int MIN_ARGS = 3;
    private static final int SERVER_ID = 0;
    private static final int PORT_ID = 1;
    private static final int NICKNAME_ID = 2;

    public static void main(String[] args) {
        if (args.length != MIN_ARGS) {
            System.out.println("Usage: client <SERVER> <PORT> <NICKNAME>");
            System.exit(1);
        }

        String serverName = args[SERVER_ID];
        String nickname = args[NICKNAME_ID];
        int port = 0;
        try {
            port = Integer.parseInt(args[PORT_ID]);
        } catch (NumberFormatException ex) {
            System.out.println("Please specify a valid integer for port");
            System.exit(1);
        }

        try {
            Socket socket = new Socket(serverName, port);
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            MessageSender sender = new MessageSender(nickname, output);
            Thread receiver = new MessageReader(sender, nickname, input);
            sender.start();
            receiver.start();
            // immediately send the nick the client specified to the server
            output.writeUTF(nickname + "> " + "/nick " + nickname);
        } catch (IOException e) {
            System.out.println("Failed to create socket with " + serverName + " on port " + port);
            System.exit(1);
        }
    }

}
