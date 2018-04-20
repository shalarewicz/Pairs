package memory;

import com.sun.net.httpserver.HttpExchange;

public class FlipRequest implements Request {
	
	private final int row, col;
	private final String player;
	private final HttpExchange exchange;

	
	public FlipRequest(int column, int row, String player, HttpExchange exchange) {
		this.row = row;
		this.col = column;
		this.player = player;
		this.exchange = exchange;
	}
	
	@Override
	public boolean isFlip() {
		return true;
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
	public int row() {
		return this.row;
	}

	@Override
	public int col() {
		return this.col;
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
