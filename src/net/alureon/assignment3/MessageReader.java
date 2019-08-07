package net.alureon.assignment3;

import java.io.DataInputStream;
import java.io.IOException;

public class MessageReader extends Thread {

    private DataInputStream input;
    private String nickname;
    private MessageSender sender;
    private static final String FROM_SERVER = "@CLIENT";

    public MessageReader(MessageSender sender, String nickname, DataInputStream input) {
        this.input = input;
        this.nickname = nickname;
        this.sender = sender;
    }

    @Override
    public void run() {
        while (true) {
            try {
                String msg = input.readUTF();
                if (msg.startsWith(FROM_SERVER)) {
                    String[] split = msg.split(" ");
                    String command = split[1];
                    if (command.startsWith("nick")) {
                        String newnickname = command.split(":")[1];
                        this.nickname = newnickname;
                        sender.updateNickname(newnickname);
                        eraseCurrentLine();
                        System.out.print(nickname + "> ");
                    }
                } else {
                    eraseCurrentLine();
                    System.out.print(msg + "\n" + nickname + "> ");
                }
            } catch (IOException ex) {
                System.out.println("Failed to read input from server");
                System.exit(1);
            }
        }
    }

    public void eraseCurrentLine() {
        // there has got to be a better way to do this
        System.out.print("\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b");
        System.out.print("                    ");
        System.out.print("\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b");
    }
}
