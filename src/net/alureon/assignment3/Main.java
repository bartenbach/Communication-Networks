package net.alureon.assignment3;

public class Main {

    private static final int NUM_ARGS = 1;

    public static void main(String[] args) {
        if (args.length < NUM_ARGS) {
            System.out.println("Usage: java (server)  <PORT NUMBER>");
            System.exit(1);
        }
        try {
            int port = Integer.parseInt(args[0]);
            new MultiUserChatServer(port).start();
        } catch (NumberFormatException ex) {
            System.out.println("Invalid integer given for port");
            System.exit(1);
        }
    }
}
