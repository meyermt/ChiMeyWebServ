package com.meyermt.api;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Main driver for the ChiMeyServer. Validates port flag and input and runs server. Assumes that port entered will be a
 * valid integer. The server will fail with a NumberFormatException if that is not the case.
 * Created by michaelmeyer on 1/27/17.
 */
public class ChiMeyServer {

    private static final String PORT_ARG = "--serverPort";

    public static void main(String[] args) {
        // Should only have one arg for port
        int port = 0;
        if (args.length == 2 && args[0].startsWith(PORT_ARG)) {
            port = Integer.parseInt(args[1]);
        } else {
            System.out.println("Illegal arguments. Should be run with arguments: --serverPort <desired port number>.");
            System.exit(1);
        }
        runServer(port);
    }

    /*
        Starts the web server. Continuous loop that listens for clients. For each client connection, a new thread is run.
     */
    private static void runServer(int port) {
        try {
            ServerSocket server = new ServerSocket(port);
            // neverending loop
            System.out.println("Server started. To stop server press CTRL + C");
            while (true) {
                Socket client = server.accept();
                System.out.println("starting a new client thread");
                new Thread(new ChiMeyHandler(client)).start();
            }
        } catch(IOException e) {
            e.printStackTrace();
            System.out.println("Unrecoverable issue running server on port: " + port);
            System.exit(1);
        }
    }
}
