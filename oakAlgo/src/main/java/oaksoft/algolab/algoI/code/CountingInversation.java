package oaksoft.algolab.algoI.code;

import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

/**
 * Counting inversions. An inversion in an array a[] is a pair of entries a[i]a[i] and a[j]a[j] such that i < j, but a[i] > a[j].
 * Given an array, design a logarithmic algorithm to count the number of inversions.
 */
public class CountingInversation {
    //Count while mergeSort the array
    public static int inversions(int [] a) {
        if(a == null || a.length == 1) return 0;
        int n = a.length;
        int[] aux = new int[n];

        for(int i = 0; i < n; i++) {
            aux[i] = a[i];
        }
        int invs = countWhileSort(a, aux, 0, a.length-1);

        StdOut.println(Arrays.toString(aux));

        return invs;
    }

    private static int countWhileSort(int[] a, int[] aux, int lo, int hi) {
        if(lo >= hi) return 0;
        int mid = (lo + hi)/2;

        int c1 = countWhileSort(aux, a, lo, mid);
        int c2 = countWhileSort(aux, a, mid + 1, hi);

        int c3 = merge(a, aux, lo, mid, hi);

        return c1 + c2 + c3;
    }

    private static int merge(int[] a, int[] aux, int lo, int mid, int hi) {
        int exchg = 0;
        int i = lo, j = mid + 1, ln = mid - lo +1, rn = hi - mid;

        for(int k = lo; k <= hi; k++) {
            if(i > mid) aux[k] = a[j++];
            else if(j > hi) {
                aux[k] = a[i++];
            } else if(a[i] > a[j]) {
                aux[k] = a[j++];
                exchg += (lo + ln - i);
            } else {
                aux[k] = a[i++];
            }
        }
        return exchg;
    }

    public static void main(String[] args){
        int[] a = {6, 2, 7, 1, 3, 8};
        StdOut.println(inversions(a));
    }
}
