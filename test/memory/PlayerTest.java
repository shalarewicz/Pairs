package memory;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PlayerTest {
	 // Testing strategy
    //   TODO
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    // TODO tests
    @Test
    public void testToString() {
    final Player test = new Player("Stephan");
    assertEquals("ID: Stephan", test.toString());
    }
}
