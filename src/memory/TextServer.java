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
import java.util.HashSet;
import java.util.Set;

/**
 * Text-protocol game server.
 * 
 * <p>PS4 instructions: the specifications of {{@link #TextServer(Board, int)},
 * {@link #port()}, and {@link #serve()} are required.
 */
public class TextServer {
    
    private final ServerSocket serverSocket;
    private final Board board;
    //TODO Use a concurrent hashset
    final Set<String> clientIDs = new HashSet<String>();
    int nextClientID = 1;
    
    // Abstraction function:
    //   a server allowing you to play a memory scramble game using text protocols
    // Representation invariant:
    //   TODO
    // Safety from rep exposure:
    //   port() returns an integer
    // 	 handleRequest() returns immutable string. 
    // 	 remaining methods do not return an object. 
    // Thread safety argument:
    //   Board is a threadsafe data type, therefore all references to board are threadsafe.
    
    /**
     * Make a new text game server using board that listens for connections on port.
     * 
     * @param board shared game board
     * @param port server port number
     * @throws IOException if an error occurs opening the server socket
     */
    public TextServer(Board board, int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.board = board;
        this.checkRep();
    }
    
    // TODO checkRep
    private void checkRep() {
    	assert true;
    }
    
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
    	//Client IDs are assigned by order of connection starting at 1;
    	
        while (true) {
        	Socket s = serverSocket.accept();
        	if (s.isConnected()) {
	        	new Thread(() -> {
	        		// block until a client connects
	        		Socket socket = s;
	        		int clientID;
					try {
		        		//Assign the client the next available client ID. 
						synchronized (this.clientIDs) {
			        		clientID = this.nextClientID;
			        		
			        		// Check to make sure the client ID is unique and was successfully added
			        		while (!clientIDs.add(String.valueOf(clientID))) {
			        			clientID++;
			        		}
			        		System.out.println("Connecting a client with id " + clientID);
			        		nextClientID = clientID + 1;
			        		System.out.println("Current clients " + clientIDs);
			        		this.board.addPlayer(String.valueOf(clientID));
						}
		        		// handle the client
		        		try {
		        			//	System.out.println("handling");
		        			handleConnection(socket, String.valueOf(clientID));
		        		} catch (IOException ioe) {
		        			ioe.printStackTrace(); // but do not stop serving
		        		} finally {
							socket.close();
		        		}
					} catch (IOException e) {
						System.out.println("Server Error" + e);
						e.printStackTrace();
					}
					
	        	}).start();
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
    private void handleConnection(Socket socket, String id) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        
        try {
            for (String input = in.readLine(); input != null; input = in.readLine()) {
            	try {
            		String output = handleRequest(input, id);
            		if (output.equals("")) {
            			this.clientIDs.remove(id);
            			socket.close();
            			break;
            		}
            		out.println(output);
            	} catch (UnsupportedOperationException e) {
            		out.println(e.getMessage() + ": Command not recognized");
            	}
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
     * @param id id of player making the request
     * @return output message to client
     */
    private String handleRequest(String input, String id) {
        String[] tokens = input.split(" ");
        
        if (tokens[0].equals("hello")) {
            if (tokens[1].matches("\\w+")) {
                return "Hello,\n" + tokens[1] + "!";
            } else {
                return "Go away,\n" + tokens[1] + ".";
            }
        }
        
        if (tokens[0].equals("look")) {
        	return board.look(id);
        }
        
        if (tokens[0].equals("flip")) {
        	try {
	        	int column = Integer.parseInt(tokens[1]);
	        	int row = Integer.parseInt(tokens[2]);
	        	System.out.println("Received flip request for (" + column + ", " + row + ") from player id: " + id);
	        	board.flip(column, row, id);
	        	return board.look(id);
        	} catch (ArrayIndexOutOfBoundsException e) {
        		throw new UnsupportedOperationException("Not enough argumetns for command: " + input);
        	}
        }
        
        if (tokens[0].equals("quit")) {
        	//TODO Connection with client. Do not return a message. Can create a response interface
        	// response ::= quit + board
        	return "";
        }
        
        // if we reach here, the client message did not follow the protocol
        throw new UnsupportedOperationException(input);
    }
}
