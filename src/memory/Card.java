package memory;

import java.util.concurrent.locks.ReentrantLock;

public class Card implements BoardSpace{

	/**
	 * A Card is a mutable threadsafe type that can be played on a Memory Scramble Board. Card values are determined by UTF-* supported characters. 
	 * Cards can be controlled by players and turned over on the board. 
	 */
	final String character;
	Boolean faceUp = false;
	String owner = "";
	private final int row, col;
	private final ReentrantLock lock = new ReentrantLock();
	
	/*
	 * AF(character) - A card at (row, col) that can be played on a memory scramble board. 
	 * Rep Invariant - char != ""
	 * Safety from rep exposure:
	 * 		only final values or primitive types are returned
	 * Thread Safety Argument - 
	 * 		int() and col() return immutable parts of the rep and are therefore threadsafe
	 * 		The following methods all obtain a lock on the Card before returning values and are therefore threadsafe. 
	 * 		All methods which mutate or observe non final parts of the rep 
	 * 		first obtain a lock on that part of the rep before it is viewed or modified. 
	 */
	
	/**
	 * @param row row of the new card on the board
	 * @param col column of the new card on the board
	 * @param character Character or emoji which will appear on the card. 
	 * @throws IllegalArgumentException if row or col <= 0
	 */
	public Card (String character, int col, int row) throws IllegalArgumentException {
		this.character = character;
		if (row <= 0 || col <= 0) {
			throw new IllegalArgumentException("(" + row + ", " + col + ") Row and Column must be positive");
		}
		this.col = col;
		this. row = row;
		this.checkRep();
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
	public boolean isFaceUp() {
		synchronized (this.faceUp) {
			return this.faceUp;
		}
	}
	
	private void checkRep() {
		assert !this.character.equals("");
		assert this.row > 0 && this.col > 0;
		
	}
	
	@Override
	public boolean claim(String id) {
		this.lock();
			if (!this.hasOwner()) {
				synchronized (owner) {
					this.owner = id;
				}
				synchronized (faceUp) {
					this.faceUp = true;
				}
				checkRep();
				return true;
			}
			return false;
	}
	
	@Override
	public void release() {
		synchronized (this.lock){
			synchronized (owner) {
				this.owner = "";
			}
			synchronized (faceUp) {
				this.faceUp = false;
			}
			this.lock.unlock();
			this.lock.notify();
		}
		checkRep();
	}
	
	@Override
	public String getOwner() {
		synchronized (owner) {
			return this.owner;
		}
	}
	
	@Override
	public boolean hasOwner() {
		synchronized (owner) {
			return !this.owner.equals("");
		}
	}
	
	@Override
	public boolean match(BoardSpace that) {
		return that instanceof Card && this.sameChar((Card) that);
	}
	
	private boolean sameChar(Card that) {
		return that.character.equals(this.character);
	}
	
	@Override
	public String character() {
		return this.character;
	}
	
	/**
	 * Attempts to obtain the lock on the card. 
	 */
	private void lock(){
		synchronized (this.lock){
			while (this.lock.isLocked()) {
				if (this.lock.isHeldByCurrentThread()) {return;}
				try {
					this.lock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					throw new RuntimeException("wait interupted");
				}
			}
			this.lock.lock();
		}
	}
	
	@Override
	public boolean isEmpty() {
		return false;
	}
	
	@Override
	public String toString() {
		return this.character + " : (" + this.row + ", " + this.col + ")";
	}
	
	@Override
	public boolean equals(Object that) {
//		System.out.println("checking equality");
		return that instanceof Card && this.equalParts((Card) that);
	}
	
	/**
	 * @param that
	 * @return true if the both cards have the same character and position 
	 * or both face up. 
	 */
	private boolean equalParts(Card that) {
		return this.character == that.character && 
				this.row == that.row &&
				this.col == that.col;
	}

	@Override
	public int hashCode() {
		return this.row * 31 + this.col;
	}
	
}
