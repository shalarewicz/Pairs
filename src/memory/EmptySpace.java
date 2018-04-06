package memory;

import java.awt.image.BufferedImage;

public class EmptySpace implements BoardSpace {

	private final int row, col;
	
	public EmptySpace(int col, int row) {
		this.row = row;
		this.col = col;
	}
	
	@Override
	public int row() {
		return this.row;
	}
	
	@Override
	public int col() {
		return this.col;
	}
	
	/**
	 * An empty space on a memory scramble board
	 */
	@Override
	public boolean isEmpty() {
		return true;
	}

	//TODO
	@Override
	public String toString() {
		return "Empty : (" + this.row + ", " + this.col + ")";
	}
	
	@Override
	public boolean equals(Object that) {
		return that instanceof EmptySpace;
	}
	
	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public boolean isFaceUp() {
		return false;
	}

	@Override
	public boolean putFaceDown() {
		return false;
	}

	@Override
	public boolean claim(String id) {
		return false;
	}

	@Override
	public void release() {
		
	}

	@Override
	public String getOwner() {
		return "";
	}

	@Override
	public boolean hasOwner() {
		return false;
	}
	
	@Override
	public boolean match(BoardSpace that) {
		return false;
	}
	
	@Override
	public String character() {
		return " ";

	}
	
	@Override
	public BufferedImage generate() {
		// TODO Auto-generated method stub
		return null;
	}
}
