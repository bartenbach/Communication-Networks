package net.alureon.assignment3;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class MessageSender extends Thread {

    private final Scanner scn = new Scanner(System.in);
    private DataOutputStream output;
    private String nickname;

    public MessageSender(String nickname, DataOutputStream dataOutputStream) {
        this.output = dataOutputStream;
        this.nickname = nickname;
    }

    @Override
    public void run() {
        while (true) {
            System.out.print(nickname + "> ");
            String input = scn.nextLine();
            try {
                output.writeUTF(nickname + "> " + input);
            } catch (IOException ex) {
                System.out.println("Failed to send message to server");
            }
        }
    }

    public void updateNickname(String newnickname) {
        this.nickname = newnickname;
    }
}
