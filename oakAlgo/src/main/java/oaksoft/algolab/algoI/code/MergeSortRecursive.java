package oaksoft.algolab.algoI.code;

/**
 * Stable sort
 */
public class MergeSortRecursive {
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

    public static void sort(Comparable[] a, Comparable[] aux, int lo, int hi) {
        if(lo >= hi) return;

        int mid = (lo + hi)/2;

        sort(aux, a, lo, mid);
        sort(aux, a, mid+1, hi);
        merge(a, aux, lo, mid, hi);
    }

    public static Comparable[] sort(Comparable[] a) {
        int n = a.length;
        Comparable[] aux = new Comparable[n];

        for(int i = 0; i < n; i++) {
            aux[i] = a[i];
        }
        sort(a, aux, 0, n-1);
        return aux;
    }
}
