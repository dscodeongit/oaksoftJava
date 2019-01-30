package oaksoft.algolab.algoI.code;

import java.util.Arrays;

public class MergeSortBottomUp {
    private static void merge(Comparable[] a, Comparable[] aux, int lo, int mid, int hi)
    {
        int i = lo, j = mid + 1;
        for(int k = lo; k <= hi; k++) {
            if(i > mid) aux[k] = a[j++];
            else if(j > hi) aux[k] = a[i++];
            else if(a[i].compareTo(a[j]) < 0) aux[k] = a[i++];
            else aux[k] = a[j++];
        }
    }

    public static void sort(Comparable[] a) {
        Comparable[] aux = new Comparable[a.length];
        int n = a.length;
        for(int size = 1; size < n; size *= 2) {
            for(int i = 0; i < n - size; i += size*2) {
                merge(a, aux, i, i + size - 1, Math.min(i + 2*size -1, n-1));
            }
        }
    }
}
