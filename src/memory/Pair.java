package memory;


public class Pair<E> {

	/**
	 * An immutable class representing pairs of objects. 
	 */
	
	/*
	 * AF(i, j) ::= A pair of objects i, j
	 * Rep invariant ::= true
	 * Safety from rep exxposure
	 * 	 Pair is immutable getFirst() and getSecond() return defensive copies of i and j
	 * Thread safety argument
	 * 		Pair is immutable
	 * 
	 */
	final E i, j;
	
	/**
	 * Creates a new pair of objects. 
	 * @param first - first item in the pair
	 * @param second second object in the pair
	 */
	public Pair(E first, E second) {
		this.i = first;
		this.j = second;
	}
	
	/**
	 * 
	 * @return the first object in the pair
	 */
	public E getFirst() {
		E result = this.i;
		return result;
	}
	
	/**
	 * 
	 * @return the second object in the pair
	 */
	public E getSecond() {
		E result = this.j;
		return result;
	}
	@Override
	public boolean equals(Object that) {
		return that instanceof Pair<?> && this.sameParts((Pair<?>) that);
	}
	
	@Override
	public String toString() {
		return "(" + this.i + ", " + this.j + ")";
	}
	
	/**
	 * 
	 * @param Pair we're comparing to
	 * @return true if the pair consists of the same objects. Order does not matter. 
	 */
	private boolean sameParts(Pair<?> that) {
		return (this.i.equals(i) || this.i.equals(that.j)) && (this.j.equals(j) || this.j.equals(that.i));
	}

}
