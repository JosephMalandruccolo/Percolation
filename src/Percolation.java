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
	// =>	CONSTANTS
	//###########################################################################
	private final int VIRTUAL_SOURCE_INDEX = 0;
	
	
	//###########################################################################
	// =>	CLASS PROPERTIES
	//###########################################################################
	//represent an N x N grid as a single array
	//we convert the 2-D array indices i, j to a single 1-D array index programatically
	private boolean[] open;						//is the site at the index open
	private WeightedQuickUnionUF id;			//structure used to determine whether we have percolated or not
	private WeightedQuickUnionUF full;			//structure used to determine whether the site is full (solves the backwash problem)
	private int sideLength;						//the public side length of the 2-D sites (ignores virtual source and sink)
	private int virtualSinkIndex;				//the 1-D index of the virtual sink
	
	
	
	//###########################################################################
	// =>	CONSTRUCTOR
	//###########################################################################
	
	/**
	 * primary constructor - creates an N x N grid, with all sites blocked
	 * initialized to false, by default
	 * @param N
	 */
	public Percolation(int N){
		
		//initialize an N x N grid, plus a virtual top site and virtual bottom site
		//we use a virtual top site to connect to each of the top row and a virtual bottom site to connect to each on the bottom row
		//we then test whether the virtual top site connects with the virtual bottom site
		//a second data structure without a virtual bottom site is maintained to solve the backwash problem
		this.sideLength = N;
		int totalSites = N * N + 2;
		this.virtualSinkIndex = totalSites - 1;
		this.open = new boolean[totalSites];
		this.id = new WeightedQuickUnionUF(totalSites);
		this.full = new WeightedQuickUnionUF(totalSites - 1);
	
		for (int n = 0; n < this.open.length; n++) this.open[n] = false;
		
		//open the virtual sites
		this.open[VIRTUAL_SOURCE_INDEX] = true;
		this.open[this.open.length - 1] = true;
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
		
		//continue method, here is where we union find all adjacent values, provided they are open
		//need to take care to programatically calculate what the adjacent values are, taking care of edges cases
		//separate union find data structure class is to be used
		//set the site to open
		this.open[index] = true;
		
		
		//connect vertically, then horizontally
		//connect site with vertical neighbors, one edge case where sideLength == 1
		if(i == 1 && j == 1 && this.sideLength == 1){
			unionWithSink(index);
			unionWithSource(index);
		}
		
		//all other cases
		else if(i == 1) {
			unionWithSource(index);
			unionSiteBelowIfOpen(index);
		}
		else if(i == this.sideLength) {
			unionSiteAboveIfOpen(index);
			unionWithSink(index);
		}
		else{
			unionSiteAboveIfOpen(index);
			unionSiteBelowIfOpen(index);
		}
		
		//connect site with horizontal neighbors, one edge case where this.sideLength == 1
		//unionSiteLeftIfOpen will union the node with the source, which is permissible
		if(j == 1 && this.sideLength > 1) unionSiteRightIfOpen(index);
		else if(j == this.sideLength) unionSiteLeftIfOpen(index);
		else{
			unionSiteRightIfOpen(index);
			unionSiteLeftIfOpen(index);
		}
	}
	
	
	/**
	 * determine whether the site at a given index is open or closed
	 * @param i - the vertical index - 1 is the top most index
	 * @param j - the horizontal index - 1 is the left most index
	 * @return true if the site is open, false otherwise
	 * @throws indexOutOfBoundsException is i or j are out of range N x N
	 */
	public boolean isOpen(int i, int j) {
		
		if(i < 1 || i > this.sideLength) throw new IndexOutOfBoundsException();
		if(j < 1 || j > this.sideLength) throw new IndexOutOfBoundsException();
		
		return this.open[convert2DindexTo1D(i, j)];
	}
	
	
	/**
	 * determine whether the site at a given index is full or empty
	 * @param i - the vertical index - 1 is the top most index
	 * @param j - the horizontal index - 1 is the left most index
	 * @return true if the site is full, false otherwise
	 * @throws indexOutOfBoundsException is i or j are out of range N x N 
	 */
	public boolean isFull(int i, int j) {
		
		if(i < 1 || i > this.sideLength) throw new IndexOutOfBoundsException();
		if(j < 1 || j > this.sideLength) throw new IndexOutOfBoundsException();
		
		return this.full.connected(VIRTUAL_SOURCE_INDEX, convert2DindexTo1D(i, j));
	}
	
	
	/**
	 * determines whether or not the system percolates
	 * @return true if the system percolates, false otherwise
	 */
	public boolean percolates() {return this.id.connected(VIRTUAL_SOURCE_INDEX, this.virtualSinkIndex);}
	
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
	
	
	//the set of helpers below programatically calculate the 1D index of the site above, below, left, or right, as indicated
	private void unionSiteBelowIfOpen(int index){
		if(this.open[index + this.sideLength]) {
			this.id.union(index, index + this.sideLength);
			this.full.union(index, index + this.sideLength);
		}
	}
	
	private void unionSiteAboveIfOpen(int index){
		if(this.open[index - this.sideLength]) {
			this.id.union(index, index - this.sideLength);
			this.full.union(index, index - this.sideLength);
		}
	}
	
	private void unionSiteRightIfOpen(int index){
		if(this.open[index + 1]) {
			this.id.union(index, index + 1);
			this.full.union(index, index + 1);
		}
	}
	
	private void unionSiteLeftIfOpen(int index){
		if(this.open[index - 1]) {
			this.id.union(index, index - 1);
			this.full.union(index, index - 1);
		}
	}
	
	private void unionWithSource(int index){
		this.id.union(VIRTUAL_SOURCE_INDEX, index);
		this.full.union(VIRTUAL_SOURCE_INDEX, index);
	}
	
	private void unionWithSink(int index){
		this.id.union(this.open.length - 1, index);
	}
	

	
}//end Percolation
