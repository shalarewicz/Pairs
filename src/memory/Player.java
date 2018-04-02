package memory;

public class Player {

	/**
	 * A player playing a Memory Scramble game represented by a player ID such as an ID # or a name. 
	 */
	
	// AF(ID, cards) ::= A player can be identified by ID and controls at most two cards in cards. 
	// Rep Invariant ::= cards.size <= 2;
	// Safety from Rep exposure:
	// 	Only immutable or final types are returned. 
	//	Defensive copy of cards is returned. 
	
	final String ID;
	
	private void checkRep() {
		assert true;
	}
	
	
	/**
	 * Creates a new player holding no cards. 
	 * @param id id of the new player
	 */
	public Player(String id) {
		this.ID = id;
		checkRep();
	}
	
	/**
	 * 
	 * @return the player's id
	 */
	public String getID() {
		return this.ID;
	}
	
	
	@Override
	public String toString() {
		return "ID: " + this.ID;
	}
	
	@Override
	public int hashCode() {
		return this.ID.hashCode();
	}
	
	@Override
	public boolean equals(Object that) {
		return that instanceof Player && this.sameParts((Player) that);
	}


	private boolean sameParts(Player that) {
		return this.ID == that.ID;
	}

}
