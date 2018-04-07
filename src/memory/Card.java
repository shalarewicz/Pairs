package memory;

import java.awt.image.BufferedImage;

public class Card implements BoardSpace{

	/**
	 * A Card can be played on a Memory Scramble Board. Card values are determined by UTF-* supported characters. 
	 * Cards can be controlled by players and turned ver on the board. 
	 */
	final String character;
	boolean faceUp = false;
	String owner = "";
	private final int row, col;
	
	
	/**
	 * @param row row of the new card on the board
	 * @param col column of the new card on the board
	 * @param character Character or emoji which will appear on the card. 
	 */
	public Card (String character, int col, int row) {
		this.character = character;
		// TODO Check that row, col > 0;
		this.col = col;
		this. row = row;
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
	 * 
	 * @return true if the card is face up
	 */
	public boolean isFaceUp() {
		return this.faceUp;
	}
	
	private void checkRep() {
		assert true;
		
	}
	
	/**
	 * Turns a card face down. If the card is controlled by a player the card remains face up
	 * @return true if the card in now face down
	 */
	public boolean putFaceDown() {
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
	public boolean claim(String id) {
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
	public void release() {
		this.owner = "";
		checkRep();
	}
	
	/**
	 * 
	 * @return id of the current owner of the card. 
	 */
	public String getOwner() {
		return this.owner;
	}
	
	/**
	 * 
	 * @return true if the card has an owner
	 */
	public boolean hasOwner() {
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
	public boolean isEmpty() {
		return false;
	}
	
	
	//TODO
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
	private boolean equalParts(Card that) {
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
