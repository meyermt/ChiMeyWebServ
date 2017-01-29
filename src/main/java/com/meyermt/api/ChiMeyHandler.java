package com.meyermt.api;

import java.io.*;
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
                headers.add(clientInput);
            }
            HttpResponseCreator responseCreator = new HttpResponseCreator();
            String resource = parseResource(headers.get(0));
            // if the regex doesn't pick up a resource, that signals a bad request that cannot be processed
            if (resource.equals("")) {
                output.println(responseCreator.create403(resource));
            } else {
                if (isMethodValid(headers.get(0))) {
                    respondToValidRequest(output, responseCreator, resource, headers.get(0));
                } else {
                    // if it was not a GET or HEAD, will return a 403
                    output.println(responseCreator.create403(resource));
                }
            }
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Hit an error reading or writing to client.");
        }

    }

    private void respondToValidRequest(PrintWriter output, HttpResponseCreator responseCreator, String resource, String reqHeader) throws IOException {
        ResourceCollector collector = new ResourceCollector();
        if (collector.resourceMoved(resource)) {
            String newLocation = collector.getNewLocation(resource);
            output.println(responseCreator.create301(newLocation));
        } else if (collector.resourceExists(resource)){
            File requestedFile = collector.collectResource(resource);
            client.getOutputStream().write(responseCreator.create200(requestedFile, resource, reqHeader));
        } else {
            output.println(responseCreator.create404(resource));
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
