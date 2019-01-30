package oaksoft.algolab.algoI.ass4;

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private final int[][] blocks;
    private Integer i,j;
    private Integer manhattan;
    private Integer hamming;

    // construct a board from an n-by-n array of blocks (where blocks[i][j] = block in row i, column j)
    public Board(int[][] blocks){
        this.blocks = copyOf(blocks);
    }

    private int[][] copyOf(int[][] blocks){
        int[][] copy = new int[blocks.length][blocks.length];
        for(int r = 0; r < blocks.length; r++){
            for(int c = 0; c < blocks.length; c++) {
                copy[r][c] = blocks[r][c];
            }
        }
        return copy;
    }

    // board dimension n
    public int dimension() {
        return blocks.length;
    }

    // number of blocks out of place
    public int hamming(){
        if(hamming == null) {
            hamming = 0;
            for (int r = 0; r < dimension(); r++) {
                for (int c = 0; c < dimension(); c++) {
                    if (blocks[r][c] != 0 && blocks[r][c] != r * dimension() + c+1) hamming++;
                }
            }
        }
        return hamming;
    }

    // sum of Manhattan distances between blocks and goal
    public int manhattan() {
        if(manhattan == null) {
            manhattan = 0;
            for (int r = 0; r < dimension(); r++) {
                for (int c = 0; c < dimension(); c++) {
                    int v = blocks[r][c];
                    if (v != 0) {
                        int gr = (v - 1) / dimension();
                        int gc = (v - 1) % dimension();
                        manhattan += Math.abs(r - gr) + Math.abs(c - gc);
                        //StdOut.println("dist of " + v + " : " + (Math.abs(r - gr) + Math.abs(c - gc)));
                    }
                }
            }
        }
        return manhattan;
    }

    // is this board the goal board?
    public boolean isGoal() {
        for(int r = 0; r < dimension(); r++){
            for(int c = 0; c < dimension() - 1; c++) {
                if(blocks[r][c] != r * dimension() + c+1) return false;
            }
        }
        return true;
    }

    // a board that is obtained by exchanging any pair of blocks
    public Board twin() {
        if(this.i == null) {
            randomTwinBlock();
        }
        int [][] twin = copyOf(blocks);

        int temp = twin[i/dimension()][i%dimension()];
        twin[i/dimension()][i%dimension()] = twin[j/dimension()][j%dimension()];
        twin[j/dimension()][j%dimension()] = temp;
        return new Board(twin);
    }

    private void randomTwinBlock(){
        int range = dimension()*dimension();

        do{
            i = StdRandom.uniform(range);
        } while(blocks[i/dimension()][i%dimension()] == 0);

        do{
            j = StdRandom.uniform(range);
        } while(i == j || blocks[j/dimension()][j%dimension()] == 0);

    }

    // does this board equal y?
    public boolean equals(Object y) {
        if(y == null || !(y instanceof Board) || dimension() != ((Board)y).dimension()) return false;
        Board by = (Board)y;
        for(int r = 0; r < dimension(); r++){
            for(int c = 0; c < dimension(); c++) {
                if(blocks[r][c] != by.blocks[r][c]) return false;
            }
        }
        return true;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        List<Board> nbs = new ArrayList<>();
        for(int i = 0; i < dimension(); i++) {
            for(int j = 0; j < dimension(); j++) {
                if(i > 0 && blocks[i-1][j] == 0) {
                    int[][] nbb = copyOf(blocks);
                    nbb[i-1][j] = blocks[i][j];
                    nbb[i][j] = 0;
                    nbs.add(new Board(nbb));
                }

                if(i < dimension()-1 && blocks[i+1][j] == 0) {
                    int[][] nbb = copyOf(blocks);
                    nbb[i+1][j] = blocks[i][j];
                    nbb[i][j] = 0;
                    nbs.add(new Board(nbb));
                }

                if(j > 0 && blocks[i][j-1] == 0) {
                    int[][] nbb = copyOf(blocks);
                    nbb[i][j-1] = blocks[i][j];
                    nbb[i][j] = 0;
                    nbs.add(new Board(nbb));
                }

                if(j < dimension()-1 && blocks[i][j+1] == 0) {
                    int[][] nbb = copyOf(blocks);
                    nbb[i][j+1] = blocks[i][j];
                    nbb[i][j] = 0;
                    nbs.add(new Board(nbb));
                }
            }
        }
        return nbs;
    }
    // string representation of this board (in the output format specified below)
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(blocks.length + "\n");
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks.length; j++) {
                sb.append(String.format("%2d ", blocks[i][j]));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    // unit tests (not graded)
    public static void main(String[] args) {
        int[][] blocks = {
                {1, 4, 3, 13},
                {8, 7, 9, 10},
                {6, 2, 15, 11},
                {0, 12, 14, 5}
        };
        blocks = new int[][]{
                {0, 1},
                {2, 3}
        };
        Board board = new Board(blocks);

        StdOut.println(board);

        StdOut.println("Neighbors : ");
        StdOut.println("Humming: " + board.hamming());
        StdOut.println("manhattan: " + board.manhattan());

        for(Board b : board.neighbors()) {
            StdOut.println(b);
        }
        StdOut.println("Neighbors ENDS ");
        StdOut.println("Twins Start ");

        StdOut.println(board.twin());
        StdOut.println(board.twin());

        StdOut.println(board.twin());
        StdOut.println(board.twin());

        StdOut.println(board.twin());
        StdOut.println(board.twin());
        StdOut.println("Twins ENDS ");


    }
}
