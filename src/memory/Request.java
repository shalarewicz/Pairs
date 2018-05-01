package memory;

import com.sun.net.httpserver.HttpExchange;

public interface Request {
	
	public boolean isFlip();
	
	public boolean isLook();
	
	public boolean isWatch();
	
	public boolean isQuit();
	
	/**
	 * 
	 * @return The exchange for the HTTP request or null if the request was from a text protocol
	 */
	public HttpExchange exchange();
	
	public int row();
	
	public int col();
	
	public String  player();
	
}
