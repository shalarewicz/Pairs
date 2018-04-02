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
	
	/**
	 * 
	 * @param character Character or emoji which will appear on the card. 
	 */
	public Card (String character) {
		this.character = character;
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
		return this.character;

	}
	
	@Override
	public boolean equals(Object that) {
		return that instanceof Card && this.equalParts((Card) that);
	}
	
	/**
	 * 
	 * @param that
	 * @return true if the both cards have the same character, owner and are either both face down or both face up. 
	 */
	private boolean equalParts(Card that) {
		return this.character == that.character && this.owner == that.owner && this.faceUp == that.faceUp;
	}

	@Override
	public int hashCode() {
		return 0;
	}
	
}
