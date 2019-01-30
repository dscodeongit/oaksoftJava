package oaksoft.algolab.algoI.ass1;

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;
/*
    Class that  perform a series of computational experiments for Percolation and generate statistical data
 */
public class PercolationStats {
    private final int siteSize;
    private Double mean;
    private Double stddev;
    private double[] thresholds ;
    /*
     * Calss Constrcutor
     * @param n size of the Percolation
     * @param trials the number of experiments to perform
     */
    public PercolationStats(int n, int trials) {    
        if(n <= 0 || trials <= 0) throw new IllegalArgumentException("invalid grid size or trials param. n=" + n + ", trials="+ trials);
        this.siteSize = n;
        this.thresholds = new double[trials];
        doSimulation();
    }

    /*
     * run the simulation
     */
    private void doSimulation() {
        for(int i=0; i<thresholds.length; i++){
            Percolation percolation = new Percolation(siteSize);
            while(!percolation.percolates()) {
                int siteRow = StdRandom.uniform(1, siteSize+1);
                int siteCol = StdRandom.uniform(1, siteSize+1);
                percolation.open(siteRow,siteCol);
            }
            thresholds[i] = (percolation.numberOfOpenSites()*1.0)/(siteSize*siteSize);
        }
    }

    /*
      * calculates the sample mean of percolation threshold if it's not calculated yet
      * @return the sample mean of percolation threshold
     */
    public double mean(){
        if(mean == null){
            mean = StdStats.mean(thresholds);
        }
        return mean;
    }

    /*
     * calculates the sample standard deviation of percolation threshold if it's not calculated yet
     * @return the standard deviation of percolation threshold
     */
    public double stddev() {
        if(stddev == null){
            stddev = StdStats.stddev(thresholds);
        }
        return stddev;
    }

    /*
     * calculates low  endpoint of 95% confidence interval
     * @return the low  endpoint of 95% confidence interval
     */
    public double confidenceLo() {
        return mean() - stddev();
    }
    /*
     *  calculates high endpoint of 95% confidence interval
     *  @return the high endpoint of 95% confidence interval
     */
    public double confidenceHi() {
        return mean() + stddev();
    }

    //main method for testing
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int t = Integer.parseInt(args[1]);
        PercolationStats percolationStats = new PercolationStats(n, t);
        StdOut.printf("%-24s%s%f\n","mean", "= ", percolationStats.mean());
        StdOut.printf("%-24s%s%f\n","stddev", "= ", percolationStats.stddev());
        StdOut.printf("%-24s%s[%f,%f]\n","95% confidence interval", "= ", percolationStats.confidenceLo(), percolationStats.confidenceHi());
    }
}
