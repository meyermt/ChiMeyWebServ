package com.meyermt.api;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by michaelmeyer on 1/28/17.
 */
public class HttpResponseCreator {

    private StringBuilder response = new StringBuilder();

    // valid mimes
    private final static String PLAIN = "text/plain";
    private final static String HTML = "text/html";
    private final static String PDF = "application/pdf";
    private final static String PNG = "image/png";
    private final static String JPEG = "image/jpeg";

    // http methods and their header descriptions
    private final static String OK = "200 OK";
    private final static String MOVED = "301 Moved Permanently";
    private final static String FORBIDDEN = "403 Forbidden";
    private final static String NOT_FOUND = "404 Not Found";


    public HttpResponseCreator() {
    }

    public String create200(String resource, String contents) {
        return null;
    }

    public String create301(String newLocation) {
        return generateHeader(newLocation, MOVED);
    }

    public String create403(String resource) {
        return generateHeader(resource, FORBIDDEN);
    }

    public String create404(String resource) {
        return generateHeader(resource, NOT_FOUND);
    }

    private String determineMime(String resource) {
        return null;
    }

    private String generateHeader(String resource, String methodDescription) {
        response.append("HTTP/1.1 " + methodDescription + System.lineSeparator());
        if (methodDescription.equals(MOVED)) {
            response.append("Location: " + resource + System.lineSeparator());
        }
        response.append("Connection: close" + System.lineSeparator());
        String now = ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME);
        response.append("Date: " + now + System.lineSeparator());
        response.append("Server: ChiMey/1.0.0" + System.lineSeparator());
        if (methodDescription.equals(OK)) {

        }
        response.append("" + System.lineSeparator());
        return response.toString();
    }

}
