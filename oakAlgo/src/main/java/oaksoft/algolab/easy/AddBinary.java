package oaksoft.algolab.easy;

import java.math.BigInteger;
import java.util.Arrays;

public class AddBinary {
    public static String addBinary(String a, String b) {
        StringBuilder sb = new StringBuilder();
        int c = 0;
        for(int i=a.length()-1, j=b.length()-1; i>=0 || j>=0; i--, j--){
            int x = (i>=0)? a.charAt(i)-'0' :0;
            int y = (j>=0)? b.charAt(j)-'0' :0;
            sb.insert(0,(x+y+c)%2);
            c=(x+y+c)/2;
        }
        if(c==1){
            sb.insert(0,c);
        }
        return sb.toString();
    }
    public static String addBinary_1(String a, String b) {
        return new BigInteger(a, 2).add(new BigInteger(b, 2)).toString(2);
    }


    public static void main(String[] args) {

       String a ="1001101";
       String b =  "1110111";

        // int[] strs = new int[] {3,0,-2,-1,1,2};
        System.out.println(addBinary_1(a,b));
    }
}
