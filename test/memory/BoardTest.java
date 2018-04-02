/* Copyright (c) 2017 MIT 6.031 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package memory;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

/**
 * TODO
 */
public class BoardTest {
    
    // Testing strategy
    //   TODO
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    // TODO tests
    final Set<String> TEST = new HashSet<String>(Arrays.asList("A", "B", "C", "D"));
    final String PLAYER_1 = "Player 1";
    final String TEST_BOARD = "boards/perfect.txt";
    final String TEST_BOARD_2 = "boards/zoom.txt";
    
    @Test
    public void testBoard() {
    	Board board = Board.generateRandom(5, 5, TEST);
    	try {
    		Board board2 = Board.parseFromFile(TEST_BOARD_2);
    		board2.addPlayer(PLAYER_1);
    		board2.flip(2, 2, PLAYER_1);
    		System.out.println(board2.look(PLAYER_1));
    	}
    	catch (IOException e) {
    		throw new RuntimeException("Could not read board file");
    	}
    	
    }
    
}
