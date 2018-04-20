package memory;

public class EmptySpace implements BoardSpace {

	private final int row, col;
	
	/*
	 * AF(character) - A empty space at (row, col) that is on a memory scramble board. 
	 * Rep Invariant - row, col > 0;
	 * Safety from rep exposure:
	 * 		only final values or primitive types are returned
	 * Thread Safety Argument - 
	 * 		EmptySpace is immutable;
	 */
	
	
	public EmptySpace(int col, int row) {
		this.row = row;
		this.col = col;
		checkRep();
	}
	
	private void checkRep() {
		assert this.row >= 0 && this.col >= 0;
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
	public void lock(){
		// don't do anything
	}
	
	@Override
	public void unlock(){
		// Don't do anything.
	}
	
}
