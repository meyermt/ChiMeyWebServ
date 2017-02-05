package com.meyermt.api;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Creates the complete http response for any valid request. 200 responses are returned as byte arrays, and all others
 * are Strings.
 * Created by michaelmeyer on 1/28/17.
 */
public class HttpResponseCreator {

    private StringBuilder response = new StringBuilder();
    private Map<String, String> mimeMap = new HashMap<>();
    private final static String mimeRegEx = "^.+\\.(?<ext>.+)$";

    // valid mimes
    private final static String PLAIN_MIME = "text/plain";
    private final static String HTML_MIME = "text/html";
    private final static String PDF_MIME = "application/pdf";
    private final static String PNG_MIME = "image/png";
    private final static String JPEG_MIME = "image/jpeg";

    // supported extensions
    private final static String PLAIN_EXT = "txt";
    private final static String HTML_EXT = "html";
    private final static String HTM_EXT = "htm";
    private final static String HTMLS_EXT = "htmls";
    private final static String HTX_EXT = "htx";
    private final static String SHTML_EXT = "shtml";
    private final static String PDF_EXT = "pdf";
    private final static String PNG_EXT = "png";
    private final static String X_PNG_EXT = "x-png";
    private final static String JPG_EXT = "jpg";
    private final static String JPEG_EXT = "jpeg";
    private final static String JPE_EXT = "jpe";
    private final static String JFIF_EXT = "jfif";
    private final static String JFIF_TBNL_EXT = "jfif-tbnl";

    // http methods and their header descriptions
    private final static String OK = "200 OK";
    private final static String MOVED = "301 Moved Permanently";
    private final static String FORBIDDEN = "403 Forbidden";
    private final static String NOT_FOUND = "404 Not Found";
    private final static String SERVER_ERROR = "500 Internal Server Error";

    /**
     * Instantiates a new Http response creator. Ensures that the line.separator property is set to "\r\n"
     */
    public HttpResponseCreator() {
        System.setProperty("line.separator", "\r\n");
    }

    /**
     * Creates 200 byte [ ]. For GET method requests, returns contents. For HEAD requests, returns only the header.
     * If there are IO issues when reading contents, this will instead return a 500 to the requester.
     *
     * @param requestedFile the requested file
     * @param resource      the requested resource
     * @param reqHeader     the request header
     * @return the byte [ ]
     */
    public byte[] create200(File requestedFile, String resource, String reqHeader) {
        loadMimeMap();
        generateHeader(requestedFile);
        endHeader();
        byte[] header = response.toString().getBytes();
        if (reqHeader.startsWith("GET")) {
            try {
                byte[] contents = getFileContents(requestedFile);
                byte[] wholeResponse = new byte[header.length + contents.length];
                System.arraycopy(header, 0, wholeResponse, 0, header.length);
                System.arraycopy(contents, 0, wholeResponse, header.length, contents.length);
                return wholeResponse;
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Issues reading file " + requestedFile.toString());
                response.setLength(0);
                generateHeader(resource, SERVER_ERROR);
                return endHeader().getBytes();
            }
        } else {
            return header;
        }
    }

    /**
     * Creates 301 response String.
     *
     * @param newLocation the new location where this resource has moved to
     * @return the Response header as a String
     */
    public String create301(String newLocation) {
        generateHeader(newLocation, MOVED);
        return endHeader();
    }

    /**
     * Creates 403 response String.
     *
     * @param resource the requested resource
     * @return the Response header as a String
     */
    public String create403(String resource) {
        generateHeader(resource, FORBIDDEN);
        return endHeader();
    }

    /**
     * Create 404 response String.
     *
     * @param resource the requested resource
     * @return the Response header as a String
     */
    public String create404(String resource) {
        generateHeader(resource, NOT_FOUND);
        return endHeader();
    }

    /*
        Overloaded method to handle generating a header for a 200 response
     */
    private void generateHeader(File requestedFile) {
        generateHeader("", OK);
        response.append("Content-Length: " + requestedFile.length() + System.lineSeparator());
        response.append("Content-Type: " + determineMime(requestedFile) + System.lineSeparator());
    }

    /*
        Main header response generator. Uses parameters to help fill in the details
     */
    private void generateHeader(String resource, String methodDescription) {
        response.append("HTTP/1.1 " + methodDescription + System.lineSeparator());
        if (methodDescription.equals(MOVED)) {
            response.append("Location: " + resource + System.lineSeparator());
        }
        response.append("Connection: close" + System.lineSeparator());
        String now = ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME);
        response.append("Date: " + now + System.lineSeparator());
        response.append("Server: ChiMey/1.0.0" + System.lineSeparator());
    }

    /*
        The ending to every header, including the blank line.
     */
    private String endHeader() {
        response.append("" + System.lineSeparator());
        return response.toString();
    }

    /*
        Uses a regex to extract the mime. Mime default is html for any unsupported types.
     */
    private String determineMime(File requestedFile) {
        String ext = requestedFile.getAbsolutePath().replaceAll(mimeRegEx, "${ext}").toLowerCase();
        System.out.println("looking for ext: " + ext);
        return mimeMap.getOrDefault(ext, HTML_MIME);
    }

    /*
        200 responses require the content type, which is retrieved from a map. This loads that map.
     */
    private void loadMimeMap() {
        mimeMap.put(PLAIN_EXT, PLAIN_MIME);
        mimeMap.put(HTML_EXT, HTML_MIME);
        mimeMap.put(HTM_EXT, HTML_MIME);
        mimeMap.put(HTMLS_EXT, HTML_MIME);
        mimeMap.put(HTX_EXT, HTML_MIME);
        mimeMap.put(SHTML_EXT, HTML_MIME);
        mimeMap.put(PDF_EXT, PDF_MIME);
        mimeMap.put(PNG_EXT, PNG_MIME);
        mimeMap.put(X_PNG_EXT, PNG_MIME);
        mimeMap.put(JPEG_EXT, JPEG_MIME);
        mimeMap.put(JPG_EXT, JPEG_MIME);
        mimeMap.put(JPE_EXT, JPEG_MIME);
        mimeMap.put(JFIF_EXT, JPEG_MIME);
        mimeMap.put(JFIF_TBNL_EXT, JPEG_MIME);
    }
    /*
        Retrieve file contents for GET 200 responses.
     */
    private byte[] getFileContents(File requestedFile) throws IOException {
        return Files.readAllBytes(requestedFile.toPath());
    }

}
