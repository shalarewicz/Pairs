/* Copyright (c) 2017 MIT 6.031 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package memory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Text-protocol game server.
 * 
 * <p>PS4 instructions: the specifications of {{@link #TextServer(Board, int)},
 * {@link #port()}, and {@link #serve()} are required.
 */
public class TextServer {
    
    private final ServerSocket serverSocket;
    
    // Abstraction function:
    //   TODO
    // Representation invariant:
    //   TODO
    // Safety from rep exposure:
    //   TODO
    // Thread safety argument:
    //   TODO
    
    /**
     * Make a new text game server using board that listens for connections on port.
     * 
     * @param board shared game board
     * @param port server port number
     * @throws IOException if an error occurs opening the server socket
     */
    public TextServer(Board board, int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }
    
    // TODO checkRep
    
    /**
     * @return the port on which this server is listening for connections
     */
    public int port() {
        return serverSocket.getLocalPort();
    }
    
    /**
     * Run the server, listening for and handling client connections.
     * Never returns normally.
     * 
     * @throws IOException if an error occurs waiting for a connection
     */
    public void serve() throws IOException {
        while (true) {
            // block until a client connects
            Socket socket = serverSocket.accept();
            
            // handle the client
            try {
                handleConnection(socket);
            } catch (IOException ioe) {
                ioe.printStackTrace(); // but do not stop serving
            } finally {
                socket.close();
            }
        }
    }
    
    /**
     * Handle a single client connection.
     * Returns when the client disconnects.
     * 
     * @param socket socket connected to client
     * @throws IOException if the connection encounters an error or closes unexpectedly
     */
    private void handleConnection(Socket socket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        
        try {
            for (String input = in.readLine(); input != null; input = in.readLine()) {
                String output = handleRequest(input);
                out.println(output);
            }
        } finally {
            out.close();
            in.close();
        }
    }
    
    /**
     * Handle a single client request and return the server response.
     * 
     * @param input message from client
     * @return output message to client
     */
    private String handleRequest(String input) {
        String[] tokens = input.split(" ");
        
        /*
         * Handle a "hello <what>" request by responding with:
         *   Hello,
         *   <what>!
         * if <what> is a single word, or:
         *   Go away,
         *   <what>.
         * otherwise.
         */
        if (tokens[0].equals("hello")) {
            if (tokens[1].matches("\\w+")) {
                return "Hello,\n" + tokens[1] + "!";
            } else {
                return "Go away,\n" + tokens[1] + ".";
            }
        }
        
        // TODO handle "quit" requests
        
        // TODO handle "look" requests
        
        // TODO handle "flip <column> <row>" requests
        
        // if we reach here, the client message did not follow the protocol
        throw new UnsupportedOperationException(input);
    }
}
