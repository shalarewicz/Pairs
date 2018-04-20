package memory;

import java.awt.image.BufferedImage;

public interface BoardSpace {

	/**
	 * This interface represents a space on a Memory Scramble Board. A space can either be EmptySpace or a Card. 
	 */
	
	/**
	 * 
	 * @return row of the space
	 */
	public int row();
	
	/**
	 * 
	 * @return column of the space. 
	 */
	public int col();
	
	/**
	 * 
	 * @return true if the the space is empty
	 */
	public boolean isEmpty();
	
	/**
	 * 
	 * @return true if the card is face up
	 */
	public boolean isFaceUp();
	
	/**
	 * Turns a card face down. If the card is controlled by a player the card remains face up
	 * @return true if the card in now face down
	 */
	public boolean putFaceDown();
	
	/**
	 * Attempts to turn over a card so that it is face up. A card can only be turned over by 
	 * it is not currently controlled. 
	 * @param id id of the player turning the card over
	 * @return true if the card is turned over. returns false if the card was already controlled
	 */
	public boolean claim(String id);
	
	/**
	 * releases the card from its owner
	 */
	public void release();
	
	/**
	 * 
	 * @return id of the current owner of the card. 
	 */
	public String getOwner();
	
	/**
	 * 
	 * @return true if the card has an owner
	 */
	public boolean hasOwner();
	
	/**
	 * Returns true if two BoardSpaces represent the same character. Empty spaces do not
	 * match each other or any card. 
	 * @param that BoardSpace you  are comparing to
	 * @return true if the the spaces represent the same character
	 */
	public boolean match(BoardSpace that);
	
	/**
	 * 
	 * @return character the card represents or " " if empty
	 */
	public String character();
	
	/**
	 * Attempts to acquire a lock on the board space
	 */
	public void lock(); 
	
	/**
	 * Attempts to release the lock on the board space. 
	 */
	public void unlock();
	
	@Override
	public String toString();
	
	@Override
	public boolean equals(Object that);
	
	@Override
	public int hashCode();
}

