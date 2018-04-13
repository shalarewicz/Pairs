/* Copyright (c) 2017 MIT 6.031 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package memory;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

/**
 * TODO
 */
public class BoardTest {
    
    


	@Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    // Testing strategy
    /*
     * Constructors
     * 		parseFromFile()
     * 			file does and doesn't exist
     * 			file is isn't formatted correctly
     * 			row, col = < 0, n;
     * 			cards.size = 0, 1, n, col * row / 2
     * 		generateRandom()
     * 			cards.size = 0, 1, n, col * row / 2, > col * row / 2
     * 			row, col = < 0, n;
     */
    
    final Set<String> CARDS = new HashSet<String>(Arrays.asList("A", "B", "C", "D"));
    final Set<String> CARDS2 = new HashSet<String>(Arrays.asList("A", "B"));
    final Set<String> SINGLE_CARD = new HashSet<String>(Arrays.asList("A"));
    
    final String PLAYER_1 = "Player 1";
    final String PLAYER_2 = "Player 2";
    final String PLAYER_3 = "Player 3";
    final String TEST = "boards/perfect.txt";
    final String TEST2 = "boards/zoom.txt";
    final String MORE_CARDS = "tst/boards/tooManyCards";
    final String INVALID_BOARD = "tst/boards/invalid.txt";
    final String MISSING_FILE = "tst/boards/missing.txt";
    final String ROW_ZERO = "tst/boards/rowZero.txt";
    final String COL_ZERO = "tst/boards/colZero.txt";
    final String ROW_NEG = "tst/boards/rowNeg.txt";
    final String COL_NEG = "tst/boards/colNeg.txt";
    
    final int ZERO = 0;
    final int POSITIVE = 10;
    final int NEGATIVE = -1;
    
    
    @Test
    // Tests parseFromFile when row = 0 
    public void testFileBoardRow0() {
    	try {
    		Board.parseFromFile(ROW_ZERO);
    	}
    	catch (IOException e) {
    		return;
    	}
    	assert false;
    	
    }
    
    @Test
    // Tests parseFromFile when col = 0 
    public void testFileBoardCol0() {
    	try {
    		Board.parseFromFile(COL_ZERO);
    	}
    	catch (IOException e) {
    		return;
    	}
    	assert false;
    	
    }
    
    @Test
    // Tests parseFromFile when row < 0 
    public void testFileBoardRowNegative() {
    	try {
    		Board.parseFromFile(ROW_NEG);
    	}
    	catch (IOException e) {
    		return;
    	}
    	assert false;
    	
    }
    
    @Test
    // Tests parseFromFile when col < 0 
    public void testFileBoardColNegative() {
    	try {
    		Board.parseFromFile(COL_NEG);
    	}
    	catch (IOException e) {
    		return;
    	}
    	assert false;
    	
    }
    
    @Test(expected=IllegalArgumentException.class)
    // Tests parseFromFile when row = 0 
    public void testRandomBoardRow0() {
    	Board.generateRandom(POSITIVE, ZERO, CARDS);
    }
    
    @Test(expected=IllegalArgumentException.class)
    // Tests parseFromFile when col = 0 
    public void testRandomBoardCol0() {
    	Board.generateRandom(ZERO, POSITIVE, CARDS);
    }
    
    @Test(expected=IllegalArgumentException.class)
    // Tests parseFromFile when row < 0 
    public void testRandomBoardRowNeg() {
    	Board.generateRandom(POSITIVE, NEGATIVE, CARDS);
    }
    
    @Test(expected=IllegalArgumentException.class)
    // Tests parseFromFile when col < 0 
    public void testRandomBoardColNeg() {
    	Board.generateRandom(NEGATIVE, POSITIVE, CARDS);
    }
    
    @Test
    // Test when board file does not exist
    public void testFileMissing() {
    	try {
			Board.parseFromFile(MISSING_FILE);
		} catch (IOException e) {
			assert true;
			return;
		}
    	assert false;
    }
    
    @Test
    public void testFileMoreCardsThanSpace() {
    	try {
			Board.parseFromFile(TEST);
			
		} catch (IOException e) {
			assert false;
		}
    }
    
    @Test
    public void testRandomTooManyCards() {
    	//Tests to make sure an exception isn't thrown;
    	// TODO Check that an even number of cars are on the board. 
    	Board.generateRandom(2, 2, CARDS);
    }
    
    
    /*
     * TESTING STRATEGY - Methods
     * look(String id)
     * 		0, 1, n players control cards
     * 		player controls 0 1, 2 cards
     * flip(int col, int row, String player)
     * 		card up || down
	 * 	 	controlled || not controlled
	 * 		player controls 0, 1, 2 cards
	 * 		player controls two matching / non matching cards
     * addPlayer(String id);
     * 		player already on board
     * toString()
     * 	flipped cards, empty spaces, face down cards
     */
    
    
    @Test
    // Tests look when no cards are controlled and when another player controls cards
    // Tests that look has the right amount of cards and when a card is face up and face down
    public void testLookNone() {
    	final int rows = 2;
    	final int cols = 2;
    	Board board = Board.generateRandom(cols, rows, CARDS);
    	board.addPlayer(PLAYER_1);
    	board.addPlayer(PLAYER_2);
    	board.flip(1, 1, PLAYER_2);
    	String result = board.look(PLAYER_1);
    	assertTrue("expected no cards to be controlled", !result.contains(">"));
    	assertEquals("expected four cards on board", 4, result.replaceAll("[\\s\\n\\t]+", "").length());
    }
    
    @Test
    // Tests look when player controls one card and another player controls 1 or 2 cards
    public void testLookControl1() {
    	final int rows = 2;
    	final int cols = 2;
    	Board board = Board.generateRandom(cols, rows, CARDS);
    	board.addPlayer(PLAYER_1);
    	board.addPlayer(PLAYER_2);
    	board.addPlayer(PLAYER_3);
    	board.flip(1, 1, PLAYER_2);
    	board.flip(2, 1, PLAYER_3);
    	board.flip(2, 2, PLAYER_3);
    	String result = board.look(PLAYER_2);
    	assertTrue("expected one card to be controlled", result.contains(">"));
    	final int index = result.indexOf('>');
    	assertTrue("expected no other >", !result.substring(0, index).contains(">") 
    			&& !result.substring(index + 1).contains(">"));
    }
    
    @Test
    // Tests look when player controls two cards and another player controls 1 or 0 cards
    public void testLookControl2() {
    	final int rows = 2;
    	final int cols = 2;
    	Board board = Board.generateRandom(cols, rows, CARDS);
    	board.addPlayer(PLAYER_1);
    	board.addPlayer(PLAYER_2);
    	board.addPlayer(PLAYER_3);
    	board.flip(1, 1, PLAYER_2);
    	board.flip(2, 1, PLAYER_3);
    	board.flip(2, 2, PLAYER_3);
    	String result = board.look(PLAYER_3);
    	assertTrue("expected two cards to be controlled", result.contains(">"));
    	final int index = result.indexOf('>');
    	assertTrue("expected one other >", result.substring(0, index).contains(">") 
    			^ result.substring(index + 1).contains(">"));
    }
    
    @Test
    //Tests flip control 0, 1, 2 cards, card face down, cards not controlled
    public void testFlipControl() {
    	final int rows = 3;
    	final int cols = 3;
    	Board board = new Board(cols, rows, SINGLE_CARD, false);
    	board.addPlayer(PLAYER_1);
    	board.addPlayer(PLAYER_2);
    	assertTrue("expected to flip card", board.flip(1, 1, PLAYER_1));
    	assertEquals("owners equal", PLAYER_1, board.getCard(1, 1).getOwner());
    	assertTrue("expected to flip card", board.flip(2, 1, PLAYER_1));
    	assertEquals("owners equal", PLAYER_1, board.getCard(2, 1).getOwner());
    	assertTrue("expected to be able to flip card", board.flip(3, 2, PLAYER_1));
    	assertTrue("expected empty card", board.getCard(1, 1).isEmpty());
    	assertTrue("expected empty card", board.getCard(2, 1).isEmpty());
    	BoardSpace card = board.getCard(3, 2);
    	assertTrue("expected card face up", card.isFaceUp());
    }
    
    @Test
    //Tests flip when card is controlled
    public void testFlipControlledCard() {
    	final int rows = 3;
    	final int cols = 3;
    	Board board = new Board(cols, rows, SINGLE_CARD, false);
    	board.addPlayer(PLAYER_1);
    	board.addPlayer(PLAYER_2);
    	board.flip(1, 1, PLAYER_1);
    	assertFalse("expected to not be able to flip", board.flip(1, 1, PLAYER_2));
    }
    @Test
    //Test flip when player holds two cards that don't match
    public void testFlipNoMatch() {
    	final int rows = 2;
    	final int cols = 2;
    	Board board = new Board(cols, rows, CARDS2, false);
    	board.addPlayer(PLAYER_1);
    	board.flip(1, 1, PLAYER_1);
    	board.flip(2, 2, PLAYER_1);
    	board.flip(1, 2, PLAYER_1);
    	if (!board.getCard(1, 1).match(board.getCard(2, 2))){
    		assertFalse("expected card on board", board.getCard(1, 1).isEmpty());
    		assertFalse("expected card on board", board.getCard(2, 2).isEmpty());
    		assertTrue("expected card face down", !board.getCard(1, 1).isFaceUp());
    		assertTrue("expected card face down", !board.getCard(2, 2).isFaceUp());
    	}
    	else {
    		testFlipNoMatch();
    	}
    }
    
    @Test
    // tests trying to flip an empty spot. 
    public void testFlipEmpty() {
    	final int rows = 3;
    	final int cols = 3;
    	Board board = new Board(cols, rows, CARDS2, false);
    	board.addPlayer(PLAYER_1);
    	assertFalse("expected unflipped emptyspace",board.flip(3, 3, PLAYER_1));
    }
    
    @Test
    // tests trying to flip an empty spot. 
    public void testFlipYourCard() {
    	final int rows = 3;
    	final int cols = 3;
    	Board board = new Board(cols, rows, CARDS2, false);
    	board.addPlayer(PLAYER_1);
    	board.flip(1, 1, PLAYER_1);
    	assertFalse("can't flip your own card",board.flip(1, 1, PLAYER_1));
    }
    
    @Test
    public void testAddPlayer() {
    	final int rows = 3;
    	final int cols = 3;
    	Board board = new Board(cols, rows, CARDS2, false);
    	assertTrue("expected to add player", board.addPlayer(PLAYER_1));
    	assertFalse("expected to not add player", board.addPlayer(PLAYER_1));
    }
    
    @Test
    public void testBoard() {
    	try {
    		Board board2 = Board.parseFromFile(TEST2);
    		board2.addPlayer(PLAYER_1);
    		board2.flip(2, 2, PLAYER_1);
    	}
    	catch (IOException e) {
    		throw new RuntimeException("Could not read board file");
    	}
    	
    }
    
}
