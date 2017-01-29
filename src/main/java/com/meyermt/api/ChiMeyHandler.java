package com.meyermt.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Basic handler for the ChiMeyServer. Services one client's request and afterwards closes the connection
 * Created by michaelmeyer on 1/28/17.
 */
public class ChiMeyHandler implements Runnable {

    private Socket client;

    /**
     * Instantiates a new ChiMeyHandler.
     *
     * @param client the client making a request
     */
    public ChiMeyHandler(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter output = new PrintWriter(client.getOutputStream(), true);
            String clientInput;
            List<String> headers = new ArrayList<>();
            while(!(clientInput = input.readLine()).equals("")) {
                //output.println("header: " + clientInput);
                headers.add(clientInput);
            }
            if (isMethodValid(headers.get(0))) {
                ResourceCollector collector = new ResourceCollector();
                String response = collector.collectResource(parseResource(headers.get(0)));
                System.out.println("sending response: " + response);
                output.println(response);
            } else {
                //need to add a return for a 403 here
                output.println("can't handle that");
            }
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Hit an error reading or writing to client.");
        }

    }

    private boolean isMethodValid(String methodString) {
        if (methodString.startsWith("GET") || methodString.startsWith("HEAD")) {
            return true;
        } else {
            return false;
        }
    }

    private String parseResource(String header) {
        //this matcher group should match resource, e.g. "GET some/resource HTTP/1.1"
        String regex = ".+ (?<resource>.+) .+";
        return header.replaceAll(regex, "${resource}");
    }
}
