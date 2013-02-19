/**
 * Winter 2013 - Coursera - Princeton University
 * @author Joseph Malandruccolo
 * 
 * This class models a percolation system.
 * The percolation problem is defined as follows => Given a composite system comprised of randomly distributed insulating and metallic
 * materials; what fraction of the materials need to be metallic so that the composite system is an electrical conductor? Given a 
 * porous landscape, with water on the surface (or oil below), under what conditions will water be able to drain through the bottom
 * (or oil gush to the surface)? Scientists have defined an abstract process known as a percolation to model such situations.
 */
public class Percolation {
	
	
	
	//###########################################################################
	// =>	CLASS PROPERTIES
	//###########################################################################
	//represent an N x N grid as a single array
	//we convert the 2-D array indices i, j to a single 1-D array index programatically
	private int[] id;
	private boolean[] open;
	private int sideLength;
	
	
	
	//###########################################################################
	// =>	CONSTRUCTOR
	//###########################################################################
	
	/**
	 * primary constructor - creates an N x N grid, with all sites blocked
	 * initialized to false, by default
	 * @param N
	 */
	Percolation(int N){
		
		//array initialized, each point in the array gets an id number
		//initialize an N x N grid, plus a virtual top site and virtual bottom site
		//we use a virtual top site to connect to each of the top row and a virtual bottom site to connect to each on the bottom row
		//we then test whether the virtual top site connects with the virtual bottom site
		this.sideLength = N;
		this.id = new int[N * N + 2];
		this.open = new boolean[N * N + 2];
		for (int n = 0; n < this.id.length; n++) this.id[n] = n;
		
	}
	
	
	//###########################################################################
	// =>	PUBLIC METHODS
	//###########################################################################

	/**
	 * open site (i, j) if it is not already open, site 1, 1 is the upper left corner
	 * @param i - ith index
	 * @param j - jth index
	 * @throws indexOutOfBoundsException is i or j are out of range N x N
	 */
	public void open(int i, int j){
		
		if(i < 1 || i > this.sideLength) throw new IndexOutOfBoundsException();
		if(j < 1 || j > this.sideLength) throw new IndexOutOfBoundsException();
		
		int index = convert2DindexTo1D(i, j);
		this.open[index] = true;
		
		//continue method, here is where we union find all adjacent values, provided they are open
		//need to take care to programatically calculate what the adjacent values are, taking care of edges cases
		
	}
	
	
	//###########################################################################
	// =>	PRIVATE HELPERS
	//###########################################################################
	
	/**
	 * converts two indices i,j in a 2D array, to their 1D representation
	 * @param i - index i
	 * @param j - index j
	 * @return arrayIndex - the 2D array index, converted to a 1D array index
	 */
	private int convert2DindexTo1D(int i, int j){
		//convert input i, j to a single array index
		int arrayIndex = 0;
				
		//i, j to single array translation
		arrayIndex += 1 + (i - 1) * this.sideLength;
		arrayIndex += j - 1;
		
		return arrayIndex;
	}
	
	
}//end class
