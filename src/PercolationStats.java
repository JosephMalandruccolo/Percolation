/**
 * Winter 2013 - Coursera - Princeton University
 * @author Joseph Malandruccolo
 * 
 * This class computes the mean, standard deviation, and the upper and lower bounds of the
 * 95% confidence interval for the percolation threshold.
 *
 */
public class PercolationStats {
	
	//###########################################################################
	// =>	CLASS PROPERTIES
	//###########################################################################
	private double mean;
	private double stddev;
	private double confidenceLo;
	private double confidenceHi;
	
	
	//###########################################################################
	// =>	CONSTRUCTOR
	//###########################################################################
	
	
	/**
	 * PercolationStats constructor
	 * @param N - the size of the N x N grid considered in this experiment
	 * @param T - the number of independent computational experiments performed
	 */
	public PercolationStats(int N, int T){
		
		if(N <= 0 || T <=0) throw new IllegalArgumentException();
		
		//use this double to store the result of the Tth experiment, then call a bunch of stats methods on this array
		double[] percolatesAt = new double[T];
		for (int i = 0; i < percolatesAt.length; i++) {
			percolatesAt[i] = experiment(N);
		}
		
		this.mean = StdStats.mean(percolatesAt);
		this.stddev = StdStats.stddev(percolatesAt);
		this.confidenceLo = this.mean - 1.96 * this.stddev / Math.sqrt(T);
		this.confidenceHi = this.mean + 1.96 * this.stddev / Math.sqrt(T);
		
		
		//code below added to pass tests
		StdOut.print(this.mean());
		StdOut.print(this.stddev());
		StdOut.printf("%f, %f", this.confidenceLo, this.confidenceHi);
		
		
	}
	
	
	
	//###########################################################################
	// =>	PUBLIC METHODS
	//###########################################################################
	
	public double mean() {return this.mean;}
	public double stddev() {return this.stddev;}
	public double confidenceLo() {return this.confidenceLo;}
	public double confidenceHi() {return this.confidenceHi;}
	
	
	//###########################################################################
	// =>	EXPERIMENT CODE - RUNS A SINGLE EXPERIMENTS
	//###########################################################################
	
	private double experiment(int n){
		
		Percolation perc = new Percolation(n);
		int openSites = 0;
		
		
		int i;
		int j;
		while(!perc.percolates()){
			i = StdRandom.uniform(n);
			j = StdRandom.uniform(n);
			
			if(!perc.isOpen(i + 1, j + 1)) {
				perc.open(i + 1, j + 1);
				openSites++;
			}//end if
		}//end while
		
		return ((double) openSites) / (n * n);
	}
	
	
	
	//###########################################################################
	// =>	MAIN METHOD
	//###########################################################################
	public static void main(String[] args){
		
		
		
		int firstArg = Integer.parseInt(args[0]);
		int secondArg = Integer.parseInt(args[1]);
		
		PercolationStats stats = new PercolationStats(firstArg, secondArg);
		
		
		StdOut.print(stats.mean());
		StdOut.print(stats.stddev());
		StdOut.printf("%f, %f", stats.confidenceLo, stats.confidenceHi);
		
		
		
		
	}
	

}//end PercolationStats
