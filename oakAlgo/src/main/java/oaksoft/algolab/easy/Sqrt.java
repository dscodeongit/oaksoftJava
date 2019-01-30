package oaksoft.algolab.easy;

public class Sqrt {
    public static int mySqrt(int x) {
        int left = 1;
        int right = x;

        while (left <= right) {
            int mid = (left + right) / 2;
            if (mid == x / mid) {
                return mid;
            }
            if (mid < x / mid) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return right;
    }

    public static void main(String[] args) {
        System.out.println(mySqrt(2147395599));

    }
}
