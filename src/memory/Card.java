package memory;

import java.awt.image.BufferedImage;

public class Card implements BoardSpace{

	/**
	 * A Card is a mutable threadsafe type that can be played on a Memory Scramble Board. Card values are determined by UTF-* supported characters. 
	 * Cards can be controlled by players and turned over on the board. 
	 */
	final String character;
	boolean faceUp = false;
	String owner = "";
	private final int row, col;
	
	/*
	 * AF(character) - A card at (row, col) that can be played on a memory scramble board. 
	 * Rep Invariant - char != ""
	 * Safety from rep exposure:
	 * 		only final values or primitive types are returned
	 * Thread Safety Argument - 
	 * 		All methods which mutate or observe non final parts of the rep 
	 * 		first obtain a lock on the entire object. 
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
	public synchronized int row() {
		return this.row;
	}
	
	@Override
	public synchronized int col() {
		return this.col;
	}
	
	/**
	 * 
	 * @return true if the card is face up
	 */
	public synchronized boolean isFaceUp() {
		return this.faceUp;
	}
	
	private void checkRep() {
		assert !this.character.equals("");
		assert this.row > 0 && this.col > 0;
		
	}
	
	/**
	 * Turns a card face down. If the card is controlled by a player the card remains face up
	 * @return true if the card in now face down
	 */
	public synchronized boolean putFaceDown() {
		if (!this.hasOwner()) {
			this.faceUp = false;
		}
		checkRep();
		return !this.faceUp;
	}

	/**
	 * Attempts to claim a card. A card can only be claimed if it it not currently controlled. 
	 * If the card is face down and not controlled it will be face up.
	 * @param id id of the player claiming the card
	 * @return true if the card is successfully. returns false if the card was already controlled
	 */
	public synchronized boolean claim(String id) {
		if (!this.hasOwner()) {
			this.owner = id;
			this.faceUp = true;
			checkRep();
			return true;
		}
		return false;
	}
	
	/**
	 * releases the card from its owner
	 */
	public synchronized void release() {
		this.owner = "";
		checkRep();
	}
	
	/**
	 * 
	 * @return id of the current owner of the card. 
	 */
	public synchronized String getOwner() {
		return this.owner;
	}
	
	/**
	 * 
	 * @return true if the card has an owner
	 */
	public synchronized boolean hasOwner() {
		return !this.owner.equals("");
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
	 * 
	 * @return image representation of the card
	 */
	public BufferedImage generate() {
		//TODO
		return null;
	}

	@Override
	public synchronized boolean isEmpty() {
		return false;
	}
	
	@Override
	public String toString() {
		return this.character + " : (" + this.row + ", " + this.col + ")";
	}
	
	@Override
	public boolean equals(Object that) {
		return that instanceof Card && this.equalParts((Card) that);
	}
	
	/**
	 * 
	 * @param that
	 * @return true if the both cards have the same character, owner and are either both face down 
	 * or both face up. 
	 */
	private synchronized boolean equalParts(Card that) {
		return this.character == that.character && 
				this.owner == that.owner && 
				this.faceUp == that.faceUp &&
				this.row == that.row &&
				this.col == that.col;
	}

	@Override
	public int hashCode() {
		return 0;
	}
	
}
