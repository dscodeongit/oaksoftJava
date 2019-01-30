package oaksoft.algolab.easy;

import java.util.Arrays;

public class PlusOne {
    public static int[] plusOne(int[] digits) {
        int c =1;
        int[] rs = new int[digits.length+1];
        for(int i=digits.length-1; i>=0; i--){
            rs[i+1] = (digits[i]+c)%10;
            c = (digits[i]+c)/10;
        }
        rs[0]=c;

        if(rs[0]==0){
            return Arrays.copyOfRange(rs, 1, rs.length);
        }

        return rs;
    }
    public static void main(String[] args) {

        int[] strs = new int[]{9,9,9,9};
        int[] i2 = new int[]{3,1,1,3,3};

        // int[] strs = new int[] {3,0,-2,-1,1,2};
        System.out.println(Arrays.toString(plusOne(strs)));
        System.out.println(Arrays.toString(plusOne(i2)));

    }

}
