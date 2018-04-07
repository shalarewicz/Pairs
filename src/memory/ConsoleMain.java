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
        final int players = 2;
        final int tries = 5;
        
        final Board board = Board.generateRandom(size, size, new HashSet<>(Arrays.asList("A", "B")));
        
        for (int ii = 0; ii < players; ii++) {
        	String id = String.valueOf(ii + 0);
            new Thread(() -> {
                Random random = new Random();
                board.addPlayer(id);
                // TODO set up player ii on the board if necessary
                
                for (int jj = 0; jj < tries; jj++) {
                	final int col = random.nextInt(size - 1) + 1;
                	final int row = random.nextInt(size - 1) + 1;
                	System.out.println("Player " + id +" flips " + row + ", " + col);
                	board.flip(col, row, id);
                	System.out.println("player " + id + "\n" + board.look(id));
                	
                    // TODO try to flip over a first card at (random.nextInt(size), random.nextInt(size))
                    //      which might block until this player can control that card
                    
                    // TODO and if that succeeded,
                    //      try to flip over a second card at (random.nextInt(size), random.nextInt(size))
                    
                }
            }).start();
        }
    }
}
