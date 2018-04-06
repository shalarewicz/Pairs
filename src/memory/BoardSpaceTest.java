package memory;

import static org.junit.Assert.*;

import org.junit.Test;

public class BoardSpaceTest {

	//TODO Fix these tests
	private static final BoardSpace EMPTY = new EmptySpace(0, 0);
	private static final BoardSpace CARD = new Card("A", 0, 0);
	
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
		assertEquals("A : (0, 0)", CARD.toString());
	}
}
