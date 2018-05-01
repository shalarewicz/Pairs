package memory;

import com.sun.net.httpserver.HttpExchange;

public class QuitRequest implements Request {

	private final String player;
	private final HttpExchange exchange;

	public QuitRequest(String player, HttpExchange exchange) {
		this.player = player;
		this.exchange = exchange;
	}
	
	public QuitRequest(String player) {
		this.player = player;
		this.exchange = null;
	}
	
	@Override
	public boolean isFlip() {
		return false;
	}

	@Override
	public boolean isLook() {
		return false;
	}

	@Override
	public boolean isWatch() {
		return false;
	}

	@Override
	public boolean isQuit() {
		return true;
	}

	@Override
	public HttpExchange exchange() {
		return this.exchange;
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

}
