package memory;

import static org.junit.Assert.*;

import org.junit.Test;

public class CardTest {
	/*
	 * Testing strategy
     *   Char = '' or unicode or emoji
	 *	 owner = "" or [.]+ 
	 * 	 card is face up or face down
	 * 
	 * isFaceUP - checked
	 * 	card is up or down
	 * 
	 * claim / putFaceDown
	 * 	 card up || down checked
	 * 	 owned || not owned
	 * 
	 * release / getOwner / hasOwner
	 * 	owned || not owned
	 * 
	 */

    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    // TODO tests for position. 
    
  
    private static final Card UNICODE = new Card("\u00A9", 1, 1);
    private static final Card EMOJI = new Card("\u1F601", 1, 1);
    private static final String PLAYER = "Player 1";
    private static final String PLAYER2 = "Player 2";
    
    /*
     * Tests isFaceUp and put faceUp when card is not owned
     */
    @Test
    public void testIsFaceUp() {
    	final Card CHAR = new Card("A", 1, 1);
    	assertFalse("expected card face down", CHAR.isFaceUp());
    	assertTrue(CHAR.claim(PLAYER));
    	assertTrue("expected card face up", CHAR.isFaceUp());
    }
    
    /**
     * tests claim() when card is controlled. 
     */
    @Test
    public void testClaimOwnedFaceUp() {
    	final Card CHAR = new Card("A", 1, 1);
    	CHAR.claim(PLAYER2);
    	assertFalse("expected card to remain face up", CHAR.claim(PLAYER));
    }
    
    /**
     * Test claim() when card is not controlled but faceUp
     */
    @Test
    public void testClaimNotOwnedFaceUp() {
    	final Card CHAR = new Card("A", 1, 1);
    	assertTrue(!CHAR.isFaceUp());
    	CHAR.claim(PLAYER);
    	CHAR.release();
    	assertTrue(CHAR.claim(PLAYER2));
    	assertEquals("Player 2", CHAR.getOwner());
    }
    
    /**
     * Tests putFaceDown when card is controlled
     */
    @Test
    public void testPutFaceDownOwnedFaceUp() {
    	final Card CHAR = new Card("A", 1, 1);
    	CHAR.claim(PLAYER2);
    	assertFalse("expected card to remain face up", CHAR.putFaceDown());
    }
    
    /**
     * Tests putFaceDown() when card not controlled and is face up.
     */
    @Test
    public void testPutFaceDownUnownedFaceUp() {
    	final Card CHAR = new Card("A", 1, 1);
    	CHAR.claim(PLAYER2);
    	CHAR.release();
    	assertTrue("exptected card to be face down", CHAR.putFaceDown());
    }
    /**
     * test putFaceDown() when card is face down
     */
    @Test
    public void testPutFaceDown() {
    	final Card CHAR = new Card("A", 1, 1);
    	assertTrue("exptected card to be face down", CHAR.putFaceDown());
    }
    
    /**
     * test release when card is controlled and getOwner()
     */
    @Test
    public void testRelease() {
    	final Card CHAR = new Card("A", 1, 1);
    	CHAR.claim(PLAYER2);
    	CHAR.release();
    	assertEquals("", CHAR.getOwner());
    }
    
    /**
     * test release when card is not controlled and faceUp
     */
    @Test
    public void testReleaseUncontrolledFaceUp() {
    	final Card CHAR = new Card("A", 1, 1);
    	CHAR.claim(PLAYER2);
    	CHAR.release();
    	CHAR.release();
    	assertEquals("", CHAR.getOwner());
    }

    /**
     * test release when card is not controlled and face down
     */
    @Test
    public void testReleaseUncontrolledFacedown() {
    	final Card CHAR = new Card("A", 1, 1);
       	CHAR.release();
    	assertEquals("", CHAR.getOwner());
    }
    
    /**
     * Test hasOwner()
     */
    @Test
    public void testHasOwner() {
    	final Card CHAR = new Card("A", 1, 1);
    	assertFalse("expected uncontroled card", CHAR.hasOwner());
    	CHAR.claim(PLAYER2);
    	assertTrue("expected controlled card", CHAR.hasOwner());
    }
    
    @Test
    public void testToString() {
    	final Card CHAR = new Card("A", 1, 1);
    	assertEquals("A : (1, 1)", CHAR.toString());
    }
    
    @Test
    public void testEquals() {
    	assertFalse("expected unequal", UNICODE.equals(EMOJI));
    }
    
    @Test
    public void testUnequalOwneD() {
    	final Card TEST = new Card("\u00A9", 1, 1);
    	UNICODE.claim(PLAYER);
    	TEST.claim(PLAYER);
    	TEST.release();
    	assertFalse("expected unequal", UNICODE.equals(TEST));
    }
    @Test
    public void testUnequalFace() {
    	final Card TEST = new Card("\u00A9", 1, 1);
    	TEST.claim(PLAYER);
    	TEST.release();
    	assertFalse("expected unequal", UNICODE.equals(TEST));
    }
    
}
