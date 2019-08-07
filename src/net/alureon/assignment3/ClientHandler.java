package net.alureon.assignment3;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private String name;
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private MultiUserChatServer server;
    private boolean connected = true;
    private static final String SERVER_PREFIX = "[SERVER] ";
    private static final String DISCONNECT_REQUEST = "/quit";
    private static final String NICKNAME_CHANGE = "/nick";
    private static final String LIST_USERS = "/list";
    private static final String HELP_REQUEST = "/help";

    public ClientHandler(MultiUserChatServer server, Socket socket, InputStream input, OutputStream output) {
        this.socket = socket;
        this.server = server;
        this.input = new DataInputStream(input);
        this.output = new DataOutputStream(output);
    }

    @Override
    public void run() {
        while (connected) {
            try {
                String message = this.input.readUTF();

                // user is always the first part of the string
                String messageContent = message.split(">")[1].trim();
                // command
                if (messageContent.startsWith("/")) {
                    parseCommand(messageContent);
                    // private message
                } else if (messageContent.startsWith("@")) {
                    handlePrivateMessage(messageContent);
                    // broadcast
                } else {
                    broadcastMessage(message);
                }
            } catch (IOException e) {
                System.out.println("Failed to read input stream...");
                connected = false;
            }
        }
        disconnect();
    }

    private void handlePrivateMessage(String messageContent) {
        String[] split = messageContent.split(" ");
        String recipient = split[0].substring(1);
        split[0] = "";
        sendPrivateMessage(String.join(" ", split), recipient);
    }

    private void sendPrivateMessage(String message, String recipient) {
        for (ClientHandler c : server.getClientPool()) {
            if (c.getName().equalsIgnoreCase(recipient)) {
                try {
                    c.getOutputStream().writeUTF("[" + this.name + "] " + message);
                } catch (IOException e) {
                    System.out.println("Failed to send: " + message + " to " + recipient);
                }
            }
        }
    }

    private void broadcastMessage(String broadcast) {
        for (ClientHandler handler : server.getClientPool()) {
            try {
                // if the name is null this is not a client with a name so we
                // won't communicate with them yet.
                if (this.name != null && !this.name.equalsIgnoreCase(handler.name)) {
                    handler.getOutputStream().writeUTF(broadcast);
                }
            } catch (IOException e) {
                System.out.println("Failed to broadcast string: " + broadcast);
            }
        }
    }

    private void broadcastJoinMessage(String nickname) {
        for (ClientHandler handler : server.getClientPool()) {
            try {
                handler.getOutputStream().writeUTF(SERVER_PREFIX + nickname + " has joined the server");
            } catch (IOException e) {
                System.out.println("Failed to broadcast join message for " + nickname);
            }
        }
    }

    private void disconnect() {
        try {
            this.socket.close();
        } catch (IOException e) {
            System.out.println("Failed to close socket");
        }
    }

    private void parseCommand(String command) {
        if (command.equalsIgnoreCase(DISCONNECT_REQUEST)) {
            handleDisconnect();
            connected = false;
            server.getClientPool().remove(this);
        } else if (command.startsWith(NICKNAME_CHANGE)) {
            handleNicknameCommand(command);
        } else if (command.startsWith(LIST_USERS)) {
            handleListUsers();
        } else if (command.startsWith(HELP_REQUEST)) {
            sendHelp();
        } else {
            System.out.println("Server received unknown command from " + name + ": " + command);
        }
    }

    private void sendHelp() {
        String intro = SERVER_PREFIX + " The following commands are accepted: ";
        String cmd1 = SERVER_PREFIX + "/nick {nickname}   -   change your nickname";
        String cmd2 = SERVER_PREFIX + "/quit              -   disconnect from the server";
        String cmd3 = SERVER_PREFIX + "/list              -   list users currently online";
        String cmd4 = SERVER_PREFIX + "@nick {message}    -   private message another user";
        sendMessageToClient(intro);
        sendMessageToClient(cmd1);
        sendMessageToClient(cmd2);
        sendMessageToClient(cmd3);
        sendMessageToClient(cmd4);
    }

    private void handleDisconnect() {
        broadcastMessage(SERVER_PREFIX + name + " has left the server");
    }

    private void handleListUsers() {
        String online = SERVER_PREFIX + "There are " + server.getClientPool().size() + " users currently online:";
        StringBuilder sb = new StringBuilder(SERVER_PREFIX);
        for (ClientHandler c : server.getClientPool()) {
            sb.append(c.name).append(" ");
        }
        sendMessageToClient(online);
        sendMessageToClient(sb.toString().trim());
    }

    private void handleNicknameCommand(String command) {
        String[] split = command.split(" ");
        if (split.length > 1) {
            String newnickname = split[1];
            if (nicknameIsFree(newnickname)) {
                sendMessageToClient("@CLIENT nick:" + newnickname);
                if (this.name == null) {
                    broadcastJoinMessage(newnickname);
                    this.name = newnickname;
                } else {
                    broadcastMessage(SERVER_PREFIX + this.name + " is now known as " + newnickname);
                    this.name = newnickname;
                    server.getClientPool().remove(this);
                }
                server.getClientPool().add(this);
            } else {
                sendMessageToClient(SERVER_PREFIX + "Nickname already in use.");
            }
        } else {
            sendMessageToClient(SERVER_PREFIX + "Received malformed command");
        }
    }

    private void sendMessageToClient(String message) {
        try {
            this.output.writeUTF(message);
        } catch (IOException e) {
            System.out.println("ERROR: Failed to write message to client: " + message);
        }
    }

    private boolean nicknameIsFree(String newnickname) {
        for (ClientHandler clientHandler : server.getClientPool()) {
            if (clientHandler.name != null) { // first connect
                if (clientHandler.name.equalsIgnoreCase(newnickname)) {
                    return false;
                }
            }
        }
        return true;
    }

    private String determineRecipient(String received) {
        if (received.startsWith("@")) {
            String recipient = received.split(" ")[0];
            return recipient.substring(1, recipient.length());
        } else {
            return null;
        }
    }

    public String getName() {
        return this.name;
    }

    public DataOutputStream getOutputStream() {
        return this.output;
    }
}
