package oaksoft.algolab.algoI.ass1;

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

/*
 *  The class that models a percolation system
 *  http://coursera.cs.princeton.edu/algs4/assignments/percolation.html
 *  https://www.coursera.org/learn/algorithms-part1/programming/Lhp5z/percolation
 */
public class Percolation {
    private final WeightedQuickUnionUF unionUF;
    private static final int topDummySiteId = 0;
    private final int bottomDummySiteId;
    private int openSites = 0;
    // 1 - open; 0 - bocked
    private final int size;
    private final boolean[][] status;

    /*
     * create n-by-n grid, with all sites blocked
     * @param n the grid size
     */
    public Percolation(int n)
    {
        if(n<=0) throw new IllegalArgumentException("Grid size must be > 0.");
        this.size = n;
        this.bottomDummySiteId = n*n+1;
        this.unionUF = new WeightedQuickUnionUF(n*n + 2);
        this.status = new boolean[n][n];
    }

    /*
     * open a site of the grid if it is not open yet
     * @param row the row index of the site
     * @param col the column index of the site
     */
    public void open(int row, int col)    // open site (row, col) if it is not open already
    {
        if(!validSite(row, col)) throw new IllegalArgumentException("row index Or col index out of boundary. row=" + row + ", col="+ col);
        if(!isOpen(row, col)){
            status[row - 1][col - 1] = true;
            openSites++;
            union(row, col);
        }
    }

    /*
     * union a site of the grid
     * @param row the row index of the site
     * @param col the column index of the site
     */
    private void union(int row, int col) {
        int unionId = getUnionId(row, col);
        if(validSite(row-1, col) && isOpen(row-1, col)) {
            unionUF.union(unionId, getUnionId(row-1, col));
        }
        if(validSite(row+1, col) && isOpen(row+1, col)) {
            unionUF.union(unionId, getUnionId(row+1, col));
        }
        if(validSite(row, col-1) && isOpen(row, col-1)) {
            unionUF.union(unionId, getUnionId(row, col-1));
        }
        if(validSite(row, col+1) && isOpen(row, col+1)) {
            unionUF.union(unionId, getUnionId(row, col+1));
        }
        // if at top row, then union with topDummySite
        if(isTopSite(row)) {
            unionUF.union(topDummySiteId,unionId);
        }
        // if at bottom row, then union with bottomDummySite
        if(isBottomSite(row)){
            unionUF.union(bottomDummySiteId, unionId);
        }
    }
    /*
     * check is a site is open
     * @param row the row index of the site
     * @param col the column index of the site
     */
    public boolean isOpen(int row, int col)
    {
        if(!validSite(row, col)) throw new IllegalArgumentException("row index Or col index out of boundary. row=" + row + ", col="+ col);
        return status[row-1][col-1];
    }

    /*
     * check if a site is full
     * @param row the row index of the site
     * @param col the column index of the site
     */
    public boolean isFull(int row, int col)
    {
        if(!validSite(row, col)) throw new IllegalArgumentException("row index Or col index out of boundary. row=" + row + ", col="+ col);
        return unionUF.connected(topDummySiteId, getUnionId(row,col));
    }

    /*
     *check the # of open sites
     *@return the number of open sites
     */
    public int numberOfOpenSites()       // number of open sites
    {
        return openSites;
    }

    /*
     * check if it percolates
     */
    public boolean percolates()              // does the system percolate?
    {
        return unionUF.connected(topDummySiteId, bottomDummySiteId);
    }

    /*
     * check if a site is valid, i.e. within grid boundary
     * @param row the row index of the site
     * @param col the column index of the site
     */
    private boolean validSite(int row, int col){
        return row > 0 && col > 0 && row <= size && col <= size;
    }

    /*
     * calculate the id of the site in UnionFound
     * @param row the row index of the site
     * @param col the column index of the site
     */
    private int getUnionId(int row, int col){
        return (row-1)*size+col;
    }

    /*
     * check if a site at bottom row
     * @param row the row index of the site
     * @param col the column index of the site
     */
    private boolean isBottomSite(int row){
        return row == size;
    }
    /*
     * check if a site at top row
     * @param row the row index of the site
     * @param col the column index of the site
     */
    private boolean isTopSite(int row){
        return row == 1;
    }


    // main method for testing
    public static void main(String[] args) {   // test client (optional)
        Percolation percolation = new Percolation(10);
        while(!percolation.percolates()) {
            int siteRow = StdRandom.uniform(1, percolation.size+1);
            int siteCol = StdRandom.uniform(1, percolation.size+1);
            percolation.open(siteRow,siteCol);
        }
        //percolation.selfDraw();
        StdOut.println("OpenSites: " + percolation.numberOfOpenSites());
        StdOut.println("Thresthold: " + percolation.numberOfOpenSites()*1.0/(percolation.size*percolation.size));
    }
}
