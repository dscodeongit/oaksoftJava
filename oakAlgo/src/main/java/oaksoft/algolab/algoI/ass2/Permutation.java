package oaksoft.algolab.algoI.ass2;

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class Permutation {
    public static void main(String[] args) {
        RandomizedQueue<String> rq = new RandomizedQueue<>();
        int k = Integer.parseInt(args[0]);
        while(k>0) {
            String s = StdIn.readString();
            rq.enqueue(s);
            k--;
        }
        for(String s : rq) {
            StdOut.println(s);
        }
    }
}
