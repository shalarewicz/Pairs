package memory;

import com.sun.net.httpserver.HttpExchange;

public interface Request {
	
	public boolean isFlip();
	
	public boolean isLook();
	
	public boolean isWatch();
	
	public HttpExchange exchange();
	
	public int row();
	
	public int col();
	
	public String  player();
	
}
