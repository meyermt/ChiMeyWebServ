package com.meyermt.api;

/**
 * Created by michaelmeyer on 1/28/17.
 */
public class HttpResponseCreator {

    private StringBuilder response = new StringBuilder();
    private final static String PLAIN = "text/plain";
    private final static String HTML = "text/html";
    private final static String PDF = "application/pdf";
    private final static String PNG = "image/png";
    private final static String JPEG = "image/jpeg";


    public HttpResponseCreator() {
    }

    public String create301(String resource) {

    }

    private String determineMime(String resource) {

    }

}
