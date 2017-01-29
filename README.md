# Simple Web Server

This is a web server that uses Java SE ServerSocket to serve client data.

## Getting Started

As a pre-req, Java 8 must be installed on whichever platform you choose to run this server on. To install and run the
web server, follow these steps:

1. Copy the project into desired directory
2. Copy file structure that you will be serving into a folder named "www" in the project's root directory. The document
root for this web server is that "www" folder.
3. Compile the code with the following command: `javac -d bin src/main/java/com/meyermt/api/*.java`
4. Run the server with the following command: `java -cp bin com.meyermt.api.ChiMeyServer --serverPort <portNumber>`. You
may choose whichever port number works for you.

## Additional Information

* The web server only accepts GET and HEAD requests
