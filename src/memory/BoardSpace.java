package memory;

import java.awt.image.BufferedImage;

public interface BoardSpace {

	/**
	 * This interface represents a space on a Memory Scramble Board. A space can either be EmptySpace or a Card. 
	 */
	
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
	 * 
	 * @return image representation of the card
	 */
	public BufferedImage generate();
	
	@Override
	public String toString();
	
	@Override
	public boolean equals(Object that);
	
	@Override
	public int hashCode();
}

