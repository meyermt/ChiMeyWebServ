package com.meyermt.api;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by michaelmeyer on 1/28/17.
 */
public class ResourceCollector {

    private final static String REDIRECTS_URI = "www/redirects.defs";
    private Map<String, String> redirects = new HashMap<>();

    public ResourceCollector() {
        loadRedirects();
    }

    public String collectResource(String resource) {

    }

    private void loadRedirects() {
        Path redirectPath = Paths.get(REDIRECTS_URI);
        Pattern pattern = Pattern.compile("(.*) (.*)");
        try {
            redirects = Files.readAllLines(redirectPath).stream()
                    .map(line -> {
                        Matcher matcher = pattern.matcher(line);
                        return new AbstractMap.SimpleEntry<String, String>(matcher.group(0), matcher.group(1));
                    })
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Issues reading redirects.defs file. Shutting down server.");
            System.exit(1);
        }
    }
}
