/* Copyright (c) 2017 MIT 6.031 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package memory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * 
 * Mutable and threadsafe type used to play a game of memory scramble. Each board can be played by a {@link #Player} identified by a unique id. 
 * A board has rows * columns spots which can either be empty or filled by a {@link #Card}
 * 
 * A game of Memory Scramble obeys the following rules:
 * Multiple players manipulate the cards on the board concurrently. They play the game by trying to turn over pairs of identical cards. Here�s an informal summary of the rules:
 * 
 *	1. When a player turns over a first card in a pair, they control that card. If someone else already controls it, they block until they can take control.
 *	2. When the player turns over a second card, if the cards match, the player keeps control of them; otherwise, the player gives up control. The cards stay face up for now.
 *	3. When the player makes their next move, if their previous cards matched, those cards are removed from the board; otherwise, if no one controls them, they turn face down.
 * 
 * Spaces on the board are numbered such that (1, 1) refers the space in the upper left hand corner. 
 * 
 * <p>PS4 instructions: the specifications of static methods
 * {@link #parseFromFile(String)} and {@link #generateRandom(int, int, Set)} are
 * required.
 */
public class Board {
    
    /**
     * Make a new board by parsing a file.
     * 
     * @param filename path to a game board file
     * @return a new board with the size and cards from the given file
     * @throws IOException if an error occurs reading or parsing the file
     */
    public static Board parseFromFile(String filename) throws IOException {
    	BufferedReader in = new BufferedReader(new FileReader(new File(filename)));
    	try {
	    	final int rows = Integer.parseInt(in.readLine());
	    	final int cols = Integer.parseInt(in.readLine());
	    	final Set<String> cards = new HashSet<String>();
	    	
	    	in.lines().forEach(c -> cards.add(c));
	    	return new Board(rows, cols, cards, true);
    	} catch (Exception e) {
    		throw new IOException("Could not read board file.");
    	}
    	finally {
    		in.close();
    	}
    }
    
    /**
     * Make a new random board.
     * 
     * @param columns board width
     * @param rows board height
     * @param cards cards that appear on the board
     * @return a new columns-by-rows-size board filled randomly with the given
     *         cards in as equal numbers as possible
     */
    public static Board generateRandom(int columns, int rows, Set<String> cards) {
        return new Board(columns, rows, cards, true);
    }
    
    private BoardSpace[][] cards;
    private final int WIDTH, HEIGHT;
    private final ConcurrentMap<Player, Pair<BoardSpace>> players = new ConcurrentHashMap<Player, Pair<BoardSpace>>();
    private final ConcurrentMap<String, Player> playerIDs = new ConcurrentHashMap<String, Player>();
    
    private final List<BoardListener> listeners = new ArrayList();
    
    private final static BoardSpace EMPTY_SPACE = new EmptySpace(0, 0);
    
    public static class Pair<E> {
    	final E i, j;
    	
    	public Pair(E i, E j) {
    		this.i = i;
    		this.j = j;
    	}
    	
    	public E getFirst() {
    		return this.i;
    	}
    	
    	public E getSecond() {
    		return this.j;
    	}
    	@Override
    	public boolean equals(Object that) {
    		return that instanceof Pair<?> && this.sameParts((Pair<?>) that);
    	}
    	
    	@Override
    	public String toString() {
    		return "(" + this.i + ", " + this.j + ")";
    	}
    	
    	private boolean sameParts(Pair<?> that) {
    		return this.i.equals(i) && this.j.equals(j);
    	}

    }
    
    
    // Abstraction function:
    //   AF(WIDTH, HEIGHT, cards, players) ::= A Memory Scramble board with WIDTH columns, HEIGHT rows where each card in cards is at position (j, i) on the board. 
    // 		Each player controls the cards in players.get(player)
    // Representation invariant:
    // 	 cards.size= == WIDTH * HEIGHT
    //	 each row has same size
    //	 each column has same size
    // 	 each player either controls 0 cards, 1 card or 2 unique cards
    // Safety from rep exposure:
    //   only primitive types (int, boolean) or immutable Strings are returned. 
    // Thread safety argument:
    //   players and playerIDs are implemented using a ConcurrentHashMap which is a threadsafe data type
    //   Pair<E> is an immutable type. 
    // 	 cards look utilizes a lock on cards while looking at the board. 
    // 	 flip obtains a lock on the card before attempting to flip the card. 
    // 	 checkCards obtains a lock on cards before modifying the board. 
    // 	 Card is a threadsafe data type therefore all actions involving card are threadsafe. 
    //   TODO Constructors are threadsafe as they obtain a lock on cards. 
    // 
    
    /**
     * 
     * Constructs a board with rows rows and columns columns where each card in cards in randomly 
     * and as evenly as possible distributed throughout the board. 
     * 
     * @param rows number of rows on the board
     * @param columns number of columns on the board
     * @param cards the types of cards on the board
     * @param shuffle if true randomly shuffles the cards on the board. 
     * @throws IllegalArgumentException if the boards does not have positive dimensions
     */
    Board(int columns, int rows, Set<String> cards, boolean shuffle) throws IllegalArgumentException {
    	//Construct an array with an even amount of each type of card then shuffle
    	
    	if (rows <= 0 || columns <= 0) {
    			throw new IllegalArgumentException();
    	}
    	this.cards = new BoardSpace[rows][columns];
    	this.HEIGHT= rows;
    	this.WIDTH = columns;
    	
    	final String[][] orderedCards = new String[rows][columns];
    	
    	final int SPOTS = rows * columns;
    	final int CARD_TYPES = cards.size();
    	
    	
    	// Cycle through the set of cards placing pairs of cards on the board until an entire 
    	// set of cards cannot be placed on the board. 
    	int i = 0;
    	while (i < SPOTS - CARD_TYPES * 2) {
    		for (String card : cards) {
    			orderedCards[i / columns][i % columns] = card;
    			i++;
    			orderedCards[i / columns][i % columns] = card;
    			i++;
    		}
    	}
    	
    	// Fill in remaining spots with pairs of cards
    	for (String card : cards) {
    		if (i <= SPOTS - 2) {
    			orderedCards[i / columns][i % columns] = card;
    			i++;
    			orderedCards[i / columns][i % columns] = card;
    			i++;
    		}
    	}
    	
    	//If set has an odd number of elements assign the last spot and empty space
    	if (SPOTS % 2 == 1) {
    		orderedCards[HEIGHT - 1][WIDTH - 1] = "";
    	}
    	
    	//Shuffle the array if applicable
    	if (shuffle) {
    		Random random = new Random();
    		
    		for (int x = orderedCards.length - 1; x > 0; x--) {
    			for (int y = orderedCards[x].length - 1; y > 0; y--) {
    				int m = random.nextInt(x + 1);
    				int n = random.nextInt(y + 1);
    				
    				String temp = orderedCards[x][y];
    				orderedCards[x][y] = orderedCards[m][n];
    				orderedCards[m][n] = temp;
    			}
    		}
    	}
    	
    	// Create the card array
    	for (int k = 0; k < HEIGHT; k++) {
    		for (int j = 0; j < WIDTH; j++) {
    			if (orderedCards[k][j].equals("")) {
    				this.cards[k][j] = new EmptySpace(k + 1, j + 1);
    			} else {
    				try {
    					this.cards[k][j] = new Card(orderedCards[k][j], j + 1, k + 1);
    				} catch (IllegalArgumentException iae){
    					throw new RuntimeException(iae.getMessage());
    				}
    			}
    		}
    	}
    	checkRep();
    }
    
    //TODO Remove for debugging only
    private void printCards() {
    	for (int i = 0; i < HEIGHT; i++) {
    		for (int j = 0; j < WIDTH; j++) {
    			System.out.print(this.cards[i][j].character() + ", ");
    		}
    		System.out.println();
    	}
    }
    
    private void checkRep() {
    	// check cards is width x height;
    	assert cards.length * cards[0].length== this.WIDTH * this.HEIGHT;
    	for (int i = 0; i < this.cards.length - 1; i++) {
    		//check each column and row have the same size
    		assert this.cards[i].length == this.cards.length;
    	}
    	
    	for (Player p : players.keySet()) {
    		Pair<BoardSpace> controlledSpots = players.get(p);
    		// Check that the player controls two unique Cards on the board. 
    		BoardSpace first = controlledSpots.getFirst();
    		BoardSpace second = controlledSpots.getSecond();
    		if (first.equals(second) && !first.isEmpty() && !second.isEmpty()){
    			try {
    				assert first.row() != second.row() || first.col() != second.col();
    			}
    			catch (AssertionError a) {
    				throw new RuntimeException("Found player holding two cards in same spot");
    			}
    		}
    	}
    }
    
    /**
     * 
     * @return A string representation of the board suited for communication with a text server
     */
    public String look(String id) {
    	StringBuilder sb = new StringBuilder();
    	
    	synchronized (this.cards) {
	    	for (int i = 0; i < this.cards.length; i++) {
	    		for (int j = 0; j < this.cards[i].length; j++) {
	    			BoardSpace card = this.cards[i][j];
	    			if (card.isFaceUp()) {
	    				if (card.getOwner().equals(id)) {
	    					sb.append(">");
	    				}
	    				else {
	    					sb.append(" ");
	    				}
	    				sb.append(card.character());
	    			}
	    			else if (!card.isEmpty()) {
	    				sb.append(" *");
	    			}
	    			else {
	    				sb.append("  ");
	    			}
	    		}
	    		if (i != this.cards.length - 1) {
	    			sb.append("\n");
	    		}
	    	}
    	}
    	return sb.toString();
    }
    
    /**
     * 
     * @param id player looking at the board
     * @return an http server response of the board
     */
    public String httpLook(String id) {
    	StringBuilder sb = new StringBuilder(this.WIDTH + "\n" + this.HEIGHT + "\n");
    	
//    	System.out.println("reading cards for http response");
    	for (int i = 0; i < this.cards.length; i++) {
    		for (int j = 0; j < this.cards[i].length; j++) {
//    			System.out.println("Starting");
    			BoardSpace card = this.cards[i][j];
//    			System.out.println("got card");
    			if (card.isFaceUp()) {
    				if (card.getOwner().equals(id)) {
//    					System.out.println("checked face up and owner");
    					sb.append("my " + card.character());
    				}
    				else sb.append("up " + card.character());
    			} else if (card.isEmpty()) {
    				sb.append("none");
    			}
    			else {
    				sb.append("down");
    			}
    			sb.append("\n");
//    			System.out.println("finishing");
    		}
    	}
//    	System.out.println("created http response");
    	return sb.toString();
    	
    }
    
    /**
     * Attempts to flip the card located at (row, col). 
     * If the card is face-down and not controlled it is turned over and is controlled by the player. 
     * If the card is face up and the card is not controlled the player gains control of the card
     * If the card is face up and controlled by another player, or the player already controls two cards no action on the board is taken. 
     * 
     * @param col column of the card to flipped
     * @param row row of the card to be flipped
     * @param player player on the board flipping the card
     * @return returns true if the card is flipped or false if the space is empty
     */
    public boolean flip(int col, int row, String player) {
    	// If this is the first card a player is flipping over check to see if 
    	// they already hold two cards. If they do, removed the cards if they match, otherwise
    	// flip the cards back over as long as they are still face up and not controlled by another 
    	// player. 
    	
    	Player p = this.playerIDs.get(player);
    	
    	// obtain a lock on the card
    //	synchronized (this.cards[row -1][col -1]) {
    		System.out.println("locking the card");
    	this.cards[row - 1][col - 1].lock();
    	try {
//    		System.out.println("Player: " + player + " acquired lock on " + row + ", " + col);
	    //	synchronized (this.cards[row - 1][col - 1]) {
	    		final BoardSpace card = this.cards[row - 1][col - 1];
	    		
	    		// Handle the cards the player holds
//	    		System.out.println("checking cards");
	    		this.checkCards(player);
//	    		System.out.println("checked cards");
	    		BoardSpace first = this.players.get(p).getFirst();
	    		BoardSpace second = this.players.get(p).getSecond();
	    		
//	    		System.out.println("Got " + player + "' s cards");
	    		// Check if the player tries to flip a card he already controls
	    		if (first.equals(card) || second.equals(card)) {
	    			return false;
	    		}
//	    		System.out.println("checked equal");
	    		
	    		// Attempt to flip the card. 
	    		try {
	    			boolean result = card.claim(player);
//	    			System.out.println(player + "claimed card");
	    			if (result) {
	    				// If the player successfully flipped the card. Add the card to the players control
	    				if (first.isEmpty()) {
//	    					System.out.println("putting first");
	    					this.players.put(p, new Pair<BoardSpace>(card, second));
//	    					System.out.println("put first");
	    				} 
	    				else if (second.isEmpty()) {
//	    					System.out.println("putting second");
	    					this.players.put(p, new Pair<BoardSpace>(first, card));
//	    					System.out.println("put second");
	    				}
	    				else {
	    					throw new RuntimeException("Should never get here player already holds two cards");
	    				}
	    			}
	    			checkRep();
	//    			this.addActionListener(new ActionListener() {
	//    				@Override
	//    			    public void actionPerformed(ActionEvent event) {
	//    			        generate();
	//    			    }
	//    			});
	    				
	    			if (result) {this.notifyBoardListeners();}
	    			return result;
	    		} catch (IndexOutOfBoundsException e) {
	    			return false;
	    		}
	    	//}
    	} catch (Exception e) {
    		System.out.println(e);
    		e.printStackTrace();
    		throw new RuntimeException(e.getMessage());
    	} 
    	
    	}
//    }
    
   

	/**
     * Return the card at (row, column) = (i,j) on the board
     * @param row row of the card
     * @param col column of the card
     * @return BoardSpace at position (i,j) or an empty space if space is not on board
     */
    //TODO Is this method used for testing only? Not threadsafe
    BoardSpace getCard(int col, int row) {
    	try {
    		return this.cards[row - 1][col - 1];
    	} catch (IndexOutOfBoundsException e) {
    		return new EmptySpace(0, 0);
    	}
    }
    
    
    /**
     * Checks to see if a player controls two matching cards. If the player does not hold two 
     * cards no action is taken. If the player holds two matching cards, they are removed from
     * the board and the player relinquishes control of the cards. If the two cards do not match
     * the player relinquishes control and the cards are turned faced down. 
     * @param id id of the player whose cards are being checked. 
     */
    private void checkCards(String id) {
//    	System.out.println("Acuiring player for " + id);
    	Player player = playerIDs.get(id);
//    	System.out.println("Acuired player for " + id);
    	
    	// Get the cards the player currently holds
//    	System.out.println("Acuiring cards for " + id);
    	BoardSpace first = players.get(player).getFirst();
    	BoardSpace second = players.get(player).getSecond();
//    	System.out.println("Acquired cards for " + id);
    	
//    	System.out.println("Building empties");
    	final BoardSpace empty1 = new EmptySpace(first.col(), first.row());
    	final BoardSpace empty2 = new EmptySpace(second.col(), second.row());
//    	System.out.println("Built empties");
    	
    	// Obtain a lock on the array. Do not need to lock each card since Card is threadsafe. 
    	// Furthermore, if the cards are removed from the board we have a lock on cards so we're good. 
    	// TODO What if a player tries to flip a card while you are removing cards. A lock on the spot in the 
    	// array won't solve the problem. Can't put a lock on flip because other players should be allowed to flip a card
    	// at the same time. In it's current state it can be seen as the player was too slow to flip the card, but if the player
    	// flips first then we removed the card the player would still be unable to flip the card as it is currently owned. On the
    	// other hand if the cards didn't match then a lock on the card is obtained before flipping it. Is this the same as a lock 
    	// in the Card object?
//    	System.out.println(id + " acquiring lock in checkCards");
    //	synchronized (this.cards) {
//    		System.out.println(id + " acquired lock in checkCards");
    		if (first.match(second)){
//    			System.out.println(id + "'s cards match");
    			//TODO Players are still blocking after a card is removed
    			System.out.println("Removing matching cards");
    			this.cards[first.row() - 1][first.col() - 1].release();
    			this.cards[first.row() - 1][first.col() - 1] = empty1;
    			this.cards[second.row() - 1][second.col() - 1].release();
    			this.cards[second.row() - 1][second.col() - 1] = empty2;
    			this.players.put(player, new Pair<BoardSpace>(empty1, empty2));
    			this.notifyBoardListeners();
    		} else if (!first.isEmpty() && !second.isEmpty()){
//    			System.out.println(id + "'s cards don't match");
    			// Are these locks necessary since each BoardSpace is threadsafe?
    			//synchronized (this.cards[first.row() - 1][first.col() - 1] ) {
//    				System.out.println("Releaseing one");
//    				System.out.println("unlocking");
	    			first.release();
	    			first.putFaceDown();
    			//}
    			//synchronized (this.cards[second.row() - 1][second.col() - 1] ) {
//    				System.out.println("Releasing two" );
//    				System.out.println("unlocking");
	    			second.release();
	    			second.putFaceDown();
	    			this.notifyBoardListeners();
    		//	}
    			this.players.put(player, new Pair<BoardSpace>(empty1, empty2));
    		}
    //	}
    	
    }
    
    /**
     * Adds a player to the board. The player controls no cards when they are added. 
     * @param id id of player to be added. Must be a unique player id
     * @return true if the player is added to the board. 
     */
    public boolean addPlayer(String id) {
    	Player p = new Player(id);
    	final boolean result = !this.playerIDs.containsKey(id);
    	this.playerIDs.putIfAbsent(id, p);
    	this.players.putIfAbsent(p, new Pair<BoardSpace>(EMPTY_SPACE, EMPTY_SPACE));
    	checkRep();
    	return result;
    }
    
    public void addBoardListener(BoardListener listener) {
    	this.listeners.add(listener);
    }
    
    protected void notifyBoardListeners() {
    	this.listeners.forEach(listener -> listener.onBoardChange());
    }
    
    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("MEMORY: " + this.HEIGHT + " x " + this.WIDTH + "\n");
    	synchronized (this.cards) {
	    	for (int i = 0; i < this.cards.length; i++) {
	    		for (int j = 0; j < this.cards[i].length; j++) {
	    			sb.append(this.cards[i][j].character());
	    			sb.append(this.cards[i][j]);
	    			if (j != this.cards[i].length - 1) {
	    				sb.append(", ");
	    			}
	    		}
	    		sb.append("\n");
	    	}
    	}
    	return sb.toString();
    }
    
    
    
}
