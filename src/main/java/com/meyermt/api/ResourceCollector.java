package com.meyermt.api;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by michaelmeyer on 1/28/17.
 */
public class ResourceCollector {

    private final static String REDIRECTS_URI = "www/redirect.defs";
    private Map<String, String> redirects = new HashMap<>();
    private HttpResponseCreator responseCreator = new HttpResponseCreator();

    public ResourceCollector() {
        loadRedirects();
    }

    public String collectResource(String resource) {
        System.out.println("retrieving the resource: " + resource);
        File requestedFile = new File(resource);
        if (redirects.containsKey(resource)) {
            // send a 301 redirect
            responseCreator.create301(resource);
        } else if (requestedFile.isFile()) {
            // retrieve the file
            // determine mime
            responseCreator.create200(resource, contents);
        } else {
            // return a file not found
            responseCreator.create404(resource);
        }
    }

    private void loadRedirects() {
        Path redirectPath = Paths.get(REDIRECTS_URI);
        String regex = "(?<requested>.+) (?<redirect>.+)";
        try {
            redirects = Files.readAllLines(redirectPath).stream()
                    .map(line ->  new AbstractMap.SimpleEntry<String, String>(line.replaceAll(regex, "${requested}"), line.replaceAll(regex, "${redirect}")))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Issues reading redirects.defs file. Shutting down server.");
            System.exit(1);
        }
    }
}
