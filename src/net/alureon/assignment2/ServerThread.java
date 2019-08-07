package net.alureon.assignment2;

import net.alureon.Joke;
import net.alureon.KnockKnockProtocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class ServerThread extends Thread {

    private List<Joke> jokeList;
    private Socket socket;

    public ServerThread(Socket socket, List<Joke> jokeList) {
        this.jokeList = jokeList;
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.println("Spun up a new thread");

        try (
        PrintWriter out =
                            new PrintWriter(socket.getOutputStream(), true);
                  BufferedReader in = new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));
            ) {
              System.out.println("Connected to client");
              String inputLine, outputLine;

              // Initiate conversation with client
              KnockKnockProtocol kkp = new KnockKnockProtocol(jokeList);
              outputLine = kkp.processInput(null);
              out.println(outputLine);

              while ((inputLine = in.readLine()) != null) {
                  outputLine = kkp.processInput(inputLine);
                  out.println(outputLine);
                  if (outputLine.equals("Bye."))
                      break;
              }
          } catch(IOException ex){
              System.out.println("ServerThread threw exception: " + ex.getMessage());
          }
    }
}
