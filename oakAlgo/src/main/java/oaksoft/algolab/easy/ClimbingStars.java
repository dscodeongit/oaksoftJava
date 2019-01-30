package oaksoft.algolab.easy;

public class ClimbingStars {
    public static int climbStairs(int n) {
        if(n==0 || n==1) return 1;
        int waysN_1 = 1;
        int waysN_2 = 1;
        int waysN = 0;

        for(int i=2; i<=n; i++){
            waysN = waysN_1 + waysN_2;
            waysN_2 = waysN_1;
            waysN_1 = waysN;
        }

        return waysN;
    }
}
