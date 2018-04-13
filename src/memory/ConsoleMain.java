/* Copyright (c) 2017 MIT 6.031 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package memory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

/**
 * Example code.
 * 
 * <p>PS4 instructions: you may use, modify, or remove this class.
 */
public class ConsoleMain {
    
    /**
     * Simulate a multi-player game.
     * 
     * @param args unused
     */
    public static void main(String[] args) {
        final int size = 10;
        final int players = 5;
        final int tries = 5;
        
        final Board board = Board.generateRandom(size, size, new HashSet<>(Arrays.asList("A", "B", "C", "D")));
        
        for (int ii = 0; ii < players; ii++) {
        	String id = String.valueOf(ii + 0);
            new Thread(() -> {
                Random random = new Random();
                board.addPlayer(id);
                
                for (int jj = 0; jj < tries; jj++) {
                	final int col = random.nextInt(size - 1) + 1;
                	final int row = random.nextInt(size - 1) + 1;
                	// Try to flip over a first card at (random.nextInt(size), random.nextInt(size))
                	//      which might block until this player can control that card
                	if (board.flip(col, row, id)) {
                		// And if that succeeded,
                		//      try to flip over a second card at (random.nextInt(size), random.nextInt(size))
                		final int col1 = random.nextInt(size - 1) + 1; 
                    	final int row1 = random.nextInt(size - 1) + 1;
                    	board.flip(col1, row1, id);
                	}
                	
                    
                    
                }
            }).start();
        }
    }
}
