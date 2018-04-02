package memory;

import static org.junit.Assert.*;

import org.junit.Test;

public class BoardSpaceTest {

	private static final BoardSpace EMPTY = new EmptySpace();
	private static final BoardSpace CARD = new Card("A");
	
	@Test
	public void testIsEmpty() {
		assertTrue(EMPTY.isEmpty());
		assertFalse(CARD.isEmpty());
	}
	
	@Test 
	public void testEquals() {
		assertFalse(EMPTY.equals(CARD));
	}
	
	@Test
	public void testToString() {
		assertEquals(" ", EMPTY.toString());
		assertEquals("*", CARD.toString());
	}
}
