package memory;

import com.sun.net.httpserver.HttpExchange;

public class LookRequest implements Request {
	
	private final String player;
	private final HttpExchange exchange;

	public LookRequest(String player, HttpExchange exchange) {
		this.player = player;
		this.exchange = exchange;
	}
	@Override
	public boolean isFlip() {
		return false;
	}

	@Override
	public boolean isLook() {
		return true;
	}

	@Override
	public boolean isWatch() {
		return false;
	}

	@Override
	public int row() {
		return 0;
	}

	@Override
	public int col() {
		return 0;
	}

	@Override
	public String player() {
		return this.player;
	}
	
	@Override
	public HttpExchange exchange() {
		return this.exchange;
	}

}
