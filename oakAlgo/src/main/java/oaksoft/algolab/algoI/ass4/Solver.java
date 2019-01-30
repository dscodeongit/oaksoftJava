package oaksoft.algolab.algoI.ass4;

import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.List;

public class Solver {
    // find a solution to the initial board (using the A* algorithm)
    private final Board board;
    private final Stack<Board> solution;

    public Solver(Board initial){
        this.board = initial;
        this.solution = new Stack<>();
        solve();
    }

    private void solve() {
        BoardNode bd = new BoardNode(board, null);

        BoardNode twin = new BoardNode(board.twin(), null);
        //StdOut.println("Twin: \n" + twin.board);

        MinPQ<BoardNode> minPQ = new MinPQ<>();

        MinPQ<BoardNode> twinMinPQ = new MinPQ<>();

        minPQ.insert(bd);
        twinMinPQ.insert(twin);

        boolean turnForTwin = false;

        while(true){
            BoardNode bn = null;
            if(!minPQ.isEmpty() && !turnForTwin) {
                //StdOut.println("Turn for board");
                bn = minPQ.delMin();
            }else if(!twinMinPQ.isEmpty() && turnForTwin){
                //StdOut.println("Turn for twin");
                bn = twinMinPQ.delMin();
            }

           if(bn != null) {
               if (bn.board.isGoal()) {
                   if (!turnForTwin) {
                       solution.push(bn.board);
                       while (bn.parent != null) {
                           solution.push(bn.parent.board);
                           bn = bn.parent;
                       }
                   }
                   //StdOut.println("Goal reached !!!");

                   break;
               } else {
                   for (Board nb : bn.board.neighbors()) {
                       if(!bn.tried(nb)) {
                           if(turnForTwin){
                               twinMinPQ.insert(bn.addChild(nb));
                           } else {
                               minPQ.insert(bn.addChild(nb));
                           }
                       }
                   }
               }
           }
           turnForTwin = !turnForTwin;
        }
    }

    // is the initial board solvable?
    public boolean isSolvable(){
        return !solution.isEmpty();
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves(){
        if(isSolvable()) return solution.size();
        return -1;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        return solution;
    }

    private class BoardNode implements Comparable<BoardNode>{
        final Board board;
        final List<BoardNode> children;
        final BoardNode parent;

        BoardNode(Board board, BoardNode parent) {
            this.board = board;
            this.parent = parent;
            this.children = new ArrayList<>();
        }

        BoardNode addChild(Board child) {
            BoardNode bn = new BoardNode(child,this);
            this.children.add(bn);
            return bn;
        }

        public boolean tried(Board bd) {
            if(parent == null) return false;
            else if(bd.equals(parent.board)) {
                return true;
            } else return parent.tried(bd);
        }

        @Override
        public int compareTo(BoardNode o) {
            return Integer.valueOf(board.manhattan()).compareTo(Integer.valueOf(o.board.manhattan()));
        }
    }

    // solve a slider puzzle (given below)
    public static void main(String[] args) {

        int[][] blocks = {
                {1, 2, 3},
                {4, 5, 6},
                {8, 7, 0}
        };

        /*
        int[][] blocks = {
                {1, 2, 3},
                {0, 7, 6},
                {5, 4, 8}
        };
        */
        Board board = new Board(blocks);

        Solver solver = new Solver(board);
        StdOut.println(solver.isSolvable());

        StdOut.println("Total Moves: " + (solver.isSolvable() ? (solver.moves()-1) : -1));
    }
}
