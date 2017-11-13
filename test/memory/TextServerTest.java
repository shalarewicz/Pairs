/* Copyright (c) 2017 MIT 6.031 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package memory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.URISyntaxException;

import org.junit.Test;

/**
 * TODO
 */
public class TextServerTest {
    
    // Testing strategy
    //   TODO
    
    private static final String LOCALHOST = "127.0.0.1";
    
    private static final int MAX_CONNECTION_ATTEMPTS = 5;
    
    /* Start server on its own thread. */
    private static Thread startServer(final TextServer server) {
        Thread thread = new Thread(() ->  {
            try {
                server.serve();
            } catch (IOException ioe) {
                throw new RuntimeException("serve() threw IOException", ioe);
            }
        });
        thread.start();
        return thread;
    }
    
    /* Connect to server with retries on failure. */
    private static Socket connectToServer(final Thread serverThread, final TextServer server) throws IOException {
        final int port = server.port();
        assertTrue("server.port() returned " + port, port > 0);
        for (int attempt = 0; attempt < MAX_CONNECTION_ATTEMPTS; attempt++) {
            try { Thread.sleep(attempt * 10); } catch (InterruptedException ie) { }
            if ( ! serverThread.isAlive()) {
                throw new IOException("server thread no longer running");
            }
            try {
                final Socket socket = new Socket(LOCALHOST, port);
                socket.setSoTimeout(1000 * 3);
                return socket;
            } catch (ConnectException ce) {
                // may try again
            }
        }
        throw new IOException("unable to connect after " + MAX_CONNECTION_ATTEMPTS + " attempts");
    }
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test
    public void testHelloValid() throws IOException, URISyntaxException {
        // Warning! This test is not legal because it provides a null board!
        // TODO You should revise or remove this test
        // TODO You should also avoid duplicating similar code in many tests
        final TextServer server = new TextServer(null, 0);
        final Thread thread = startServer(server);
        final Socket socket = connectToServer(thread, server);
        
        final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        final PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        
        out.println("hello world");
        assertEquals("Hello,", in.readLine());
        assertEquals("world!", in.readLine());
        
        socket.close();
    }
    
    @Test
    public void testHelloInvalid() throws IOException, URISyntaxException {
        // Warning! This test is not legal because it provides a null board!
        // TODO You should revise or remove this test
        // TODO You should also avoid duplicating similar code in many tests
        final TextServer server = new TextServer(null, 0);
        final Thread thread = startServer(server);
        final Socket socket = connectToServer(thread, server);
        
        final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        final PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        
        final String world = new String(new int[] { 0x1F30D }, 0, 1);
        
        out.println("hello " + world);
        assertEquals("Go away,", in.readLine());
        assertEquals(world + ".", in.readLine());
        
        socket.close();
    }
    
    // TODO tests
    
}
