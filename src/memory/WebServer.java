/* Copyright (c) 2017 MIT 6.031 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package memory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import memory.web.HeadersFilter;
import memory.web.LogFilter;

/**
 * HTTP web game server.
 * 
 * <p>PS4 instructions: the specifications of {@link #WebServer(Board, int)},
 * {@link #port()}, {@link #start()}, and {@link #stop()} are required.
 */
public class WebServer {
    
    private final HttpServer server;
    private final Board board;
    final ConcurrentMap<String, BlockingQueue<Request>> clients = new ConcurrentHashMap<String, BlockingQueue<Request>>();
    final Set<String> players = new HashSet<String>();
    
    // Abstraction function: 
    // 	 A web server over which players can play the game memory scramble through various requests
    // Representation invariant:
    //   assert true;
    // Safety from rep exposure:
    //   only primitive fields are returned
    // Thread safety argument:
    //   //TODO Responses might not be sent in the order in which the Request was sent. 
    
    /**
     * Make a new web game server using board that listens for connections on port.
     * 
     * @param board shared game board
     * @param port server port number
     * @throws IOException if an error occurs starting the server
     */
    public WebServer(Board board, int port) throws IOException {
    	this.board = board;
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        
        // handle concurrent requests with multiple threads
        server.setExecutor(Executors.newCachedThreadPool());
        
        LogFilter log = new LogFilter();
        HeadersFilter headers = new HeadersFilter();
        // allow requests from web pages hosted anywhere
        headers.add("Access-Control-Allow-Origin", "*");
        // all responses will be plain-text UTF-8
        headers.add("Content-Type", "text/plain; charset=utf-8");
        
        // handle requests for paths that start with /hello/, e.g. /hello/world
        //   HttpContext hello = server.createContext("/hello/", new HttpHandler() {
        //      public void handle(HttpExchange exchange) throws IOException {
        //          handleHello(exchange);
        //      }
        //  });
        // -or- use a lambda expression with "->"
        //HttpContext hello = server.createContext("/hello/", exchange -> handleHello(exchange));
        // -or- use a method reference with "::"
        //HttpContext hello = server.createContext("/hello/", this::handleHello);
        
        // add logging to the /hello/ handler and set required HTTP headers
        //   (do this on all your handlers)
        // hello.getFilters().addAll(Arrays.asList(log, headers));
        
        
        // Handle requests for /look/player
        // Look requests should also be able to be sent while a flip or watch request blocks
		HttpContext look = server.createContext("/look/", exchange -> handleLook(exchange));
		look.getFilters().addAll(Arrays.asList(log, headers));
		
		// Handle requests for /flip/player/row,col
		// Concurrent flips are not allowed. 
		HttpContext flip = server.createContext("/flip/", exchange -> handleFlip(exchange));
    	flip.getFilters().addAll(Arrays.asList(log, headers));
    	
    	// Handle requests for /watch/player
    	// Flip requests must continue to be sent while a watch request blocks. 
    	HttpContext watch = server.createContext("/watch/", exchange -> handleWatch(exchange));
        watch.getFilters().addAll(Arrays.asList(log, headers));
        
        checkRep();
    }
    
    private void checkRep() {
    	assert true;
    }
    
    /**
     * @return the port on which this server is listening for connections
     */
    public int port() {
        return server.getAddress().getPort();
    }
    
    /**
     * Start this server in a new background thread.
     */
    public void start() {
        System.err.println("Server will listen on " + server.getAddress());
        server.start();
    }
    
    /**
     * Stop this server. Once stopped, this server cannot be restarted.
     */
    public void stop() {
        System.err.println("Server will stop");
        server.stop(0);
    }
     
    private void handleLook(HttpExchange exchange) throws IOException {
    	// if you want to know the requested path:
        final String path = exchange.getRequestURI().getPath();
        
        // it will always start with the base path from server.createContext():
        final String base = exchange.getHttpContext().getPath();
        assert path.startsWith(base);
        
        final String id = path.substring(base.length());
        final String lookID = "/look/" + id;
        
        // Check if the request is valid
        if (id.matches("\\w+")) {
        	exchange.sendResponseHeaders(200, 0);
        	final Request look = new LookRequest(id, exchange);
        	
        	// IF the player is not on the board add them to the board and start a thread for them to handle look requests
        	if (!players.contains(id)) {
        		board.addPlayer(id);
        	}
        	if (!clients.containsKey(lookID)) {
        		clients.put(lookID, new SynchronousQueue<Request>());
    			new Thread(() -> {
    				//Start a thread for the player to handle look requests
    				while (true) {
	        			try {
							handleRequest(clients.get(lookID).take());
						} catch (IOException | InterruptedException e) {
							e.printStackTrace();
						}
    				}
    			}).start();   
        	} 
        	// Put the request in the player's queue
        	try {
				clients.get(lookID).put(look);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        } else {
            // otherwise, respond with HTTP code 404 to indicate an error
            exchange.sendResponseHeaders(404, 0);
            final String response = "Request: "+ id + " not recognized";
            OutputStream body = exchange.getResponseBody();
            PrintWriter out = new PrintWriter(new OutputStreamWriter(body, StandardCharsets.UTF_8), true);
	        out.println(response);
	        exchange.close();
        }
    }
    
      
    private void handleFlip(HttpExchange exchange) throws IOException {
    	// if you want to know the requested path:
        final String path = exchange.getRequestURI().getPath();
        
        // it will always start with the base path from server.createContext():
        final String base = exchange.getHttpContext().getPath();
        assert path.startsWith(base);
        
        final String request = path.substring(base.length());
        
        if (request.matches("[\\w]+/[0-9]+,[0-9]+")) {
        	// If the request is valid
        	final int endPlayerIndex = request.indexOf("/");
        	final String colRowDelimiter = ",";
        	final int colRowDelimiterIndex = request.substring(endPlayerIndex).indexOf(colRowDelimiter);
        	
        	final String id = request.substring(0, endPlayerIndex);
        	final String flipID = "/flip/" + id;
        	final int col = Integer.parseInt(request.substring(endPlayerIndex + 1, colRowDelimiterIndex + endPlayerIndex ));
        	final int row = Integer.parseInt(request.substring(colRowDelimiterIndex + endPlayerIndex + 1));
        	System.out.println("Player: " + id + " flips " + col + ", " + row );
        	
        	// Create the request
        	final Request flip = new FlipRequest(col, row, id, exchange);
        	
        	exchange.sendResponseHeaders(200, 0);
        	if (!players.contains(id)) {
        		board.addPlayer(id);
        	}
        	if (!clients.containsKey(flipID)) {
            	this.clients.put(flipID, new SynchronousQueue<Request>());
            	new Thread(() -> {
            		System.out.println("Started a thread in flip for " + id);
            		while (true) {
	            		try {
	            			
							this.handleRequest(this.clients.get(flipID).take());
						} catch (IOException | InterruptedException e) {
							e.printStackTrace();
						}
            		}
            	}).start();
            }
        	try {
        		this.clients.get(flipID).put(flip);
        	} catch (InterruptedException e) {
        		e.printStackTrace();
        	}
        } else {
            // otherwise, respond with HTTP code 404 to indicate an error
            exchange.sendResponseHeaders(404, 0);
            final String response = "Request: "+ request + " not recognized";
            // write the response to the output stream using UTF-8 character encoding
            OutputStream body = exchange.getResponseBody();
            PrintWriter out = new PrintWriter(new OutputStreamWriter(body, StandardCharsets.UTF_8), true);
            out.println(response);
            // if you do not close the exchange, the response will not be sent!
            exchange.close();
        }
        
        
    }
    
    private void handleWatch(HttpExchange exchange) throws IOException {
    	// if you want to know the requested path:
        final String path = exchange.getRequestURI().getPath();
        
        // it will always start with the base path from server.createContext():
        final String base = exchange.getHttpContext().getPath();
        assert path.startsWith(base);
        
        final String request = path.substring(base.length());
        
       if (request.matches("[\\w]+")) {
      	 exchange.sendResponseHeaders(200, 0);

    	   final String id = request;
    	   final String watchID = "/watch/" + id;
    	   final Request watch = new WatchRequest(id, exchange);
    	   
    	   if (!players.contains(id)) {
    		   this.board.addPlayer(id);
    	   }
    	   if (!clients.containsKey(watchID)) {
    		   clients.put(watchID, new SynchronousQueue<Request>());
    		   new Thread(() ->  {
    			   try {
    				   while (true) {
    					   this.handleRequest(this.clients.get(watchID).take());
    				   }
    			   } catch (IOException | InterruptedException e) {
					e.printStackTrace();
    			   }
    		   }).start();
    	   }
    	   try {
    		   this.clients.get(watchID).put(watch);
    	   } catch (InterruptedException e1) {
    		   e1.printStackTrace();
    	   }
        } else {
            // otherwise, respond with HTTP code 404 to indicate an error
            exchange.sendResponseHeaders(404, 0);
            final String response = "Request: "+ request + " not recognized";
            // write the response to the output stream using UTF-8 character encoding
            OutputStream body = exchange.getResponseBody();
            PrintWriter out = new PrintWriter(new OutputStreamWriter(body, StandardCharsets.UTF_8), true);
            out.println(response);
            
            // if you do not close the exchange, the response will not be sent!
            exchange.close();
        }
        
    }
    
    /**
     * Handles flip, look and watch requests from an HTTP client
     * @param request request to be processed
     * @throws IOException
     */
    private void handleRequest(Request request) throws IOException{
    	final HttpExchange exchange = request.exchange();
    	final String response;
    	
    	final String player = request.player();
    	
    	if (request.isFlip()) {
    		final int row = request.row();
    		final int col = request.col();
    		
    		this.board.flip(col, row, player);
    		response = this.board.httpLook(player);
    		OutputStream body = exchange.getResponseBody();
            PrintWriter out = new PrintWriter(new OutputStreamWriter(body, StandardCharsets.UTF_8), true);
            out.println(response);
            exchange.close();
            return;
    		
    	} else if (request.isLook()) {
    		response = this.board.httpLook(player);
    		OutputStream body = exchange.getResponseBody();
            PrintWriter out = new PrintWriter(new OutputStreamWriter(body, StandardCharsets.UTF_8), true);
            out.println(response);
            exchange.close();
            return;
    		
    	} else if (request.isWatch()) {
    		
    		board.addBoardListener(new BoardListener() {
    			@Override
    			public void onBoardChange() {
    				final String watchResponse = board.httpLook(player);
    				OutputStream body = exchange.getResponseBody();
    		        PrintWriter out = new PrintWriter(new OutputStreamWriter(body, StandardCharsets.UTF_8), true);
    		        out.println(watchResponse);
    		        exchange.close();
    			}
    		
    		});
    		return;
    		
    	} else {
    		exchange.sendResponseHeaders(404, 0);
            response = "Request: "+ request + " not recognized";
            // Invalid response terminates the function.
            return;
    	}
    	
    }
}
