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
 * Collects file resources to help determine which response the handler should choose.
 * Created by michaelmeyer on 1/28/17.
 */
public class ResourceCollector {

    private final static String REDIRECTS_URI = "www/redirect.defs";
    private Map<String, String> redirects = new HashMap<>();
    private static String DOC_ROOT = "www";

    /**
     * Instantiates a new Resource collector. The redirects file is loaded each time an instance of this is created.
     */
    public ResourceCollector() {
        loadRedirects();
    }

    /**
     * Collect resource file.
     *
     * @param resource the requested resource
     * @return the requested resource's file object
     */
    public File collectResource(String resource) {
        return new File(DOC_ROOT + resource);
    }

    /**
     * Resource moved boolean. Indicates whether or not this resource has been moved by checking with the redirects file.
     *
     * @param resource the requested resource
     * @return the boolean indicating whether it has been moved
     */
    public boolean resourceMoved(String resource) {
        if (redirects.containsKey(resource)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gets new location for a moved resource. Should be used in tandem with resourceMoved method.
     *
     * @param resource the requested resource
     * @return the new location of the resource
     */
    public String getNewLocation(String resource) {
        String newLocation = redirects.get(resource);
        return newLocation;
    }

    /**
     * Resource exists boolean. Indicates if the resource exists in the file directory as a file.
     *
     * @param resource the requested resource
     * @return the boolean indicating its existence
     */
    public boolean resourceExists(String resource) {
        return new File(DOC_ROOT + resource).isFile();
    }

    /*
        Loads the redirect.defs file. Uses some regex-ing to map the entries to a map.
     */
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
