package net.alureon.assignment2;

/*
 * Copyright (c) 1995, 2014, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import net.alureon.assignment2.ServerThread;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class KnockKnockServer {

    private static final List<net.alureon.Joke> jokeList = new ArrayList<>();

    public static void main(String[] args)  {

        if (args.length != 2) {
            System.err.println("Usage: java KnockKnockServer <port number> <jokefile>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);
        String jokeFile = args[1];

        populateJokeList(jokeFile);
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            System.out.println("FATAL: Failed to start server!");
        }
        System.out.println("Server started on port " + portNumber);

        while (true) {
            System.out.println("Waiting for clients...");
            try {
                new ServerThread(serverSocket.accept(), jokeList).start();
            } catch (IOException ex) {
                System.out.println("ERROR: Failed to accept client connection!");
            }

        }
    }

    private static void populateJokeList(String jokeFile) {
        try {
            Scanner scanner = new Scanner(new File(jokeFile));
            while (scanner.hasNextLine()) {
                String joke = scanner.nextLine();
                String[] splitJoke = joke.split(":");
                if (splitJoke.length != 2) {
                    System.out.println("Invalid joke found in jokefile: " + joke);
                    System.out.println("Please use format clue:punchline");
                    continue;
                }
                String clue = splitJoke[0];
                String punchline = splitJoke[1];
                jokeList.add(new net.alureon.Joke(clue, punchline));
            }
        } catch (FileNotFoundException e) {
            System.out.println("Please specify a valid file.");
        }
    }
}