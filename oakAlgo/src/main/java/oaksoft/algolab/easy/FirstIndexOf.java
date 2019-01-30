package oaksoft.algolab.easy;

public class FirstIndexOf {
    public static int strStr(String haystack, String needle) {
        if(needle.length() ==0){
            return 0;
        }
        int i = 0;
        int j = 0;
        int bp = 0;
        while(i<haystack.length()){
            if(haystack.charAt(i)==needle.charAt(j)){
                if(j!=0 && haystack.charAt(i)==needle.charAt(0) && bp == 0){
                    bp = i;
                }
                i++;
                j++;
                if(j==needle.length()){
                    return i-j;
                }
            }else if(j!=0){
                if(bp!=0) {
                    i=bp;
                }
                j=0;
                bp=0;
            }else {
                i++;
            }
        }
        return -1;
    }

    public static int strStr_1(String haystack, String needle) {
        for (int i = 0; ; i++) {
            for (int j = 0; ; j++) {
                if (j == needle.length()) return i;
                if (i + j == haystack.length()) return -1;
                if (needle.charAt(j) != haystack.charAt(i + j)) break;
            }
        }
    }

    public static void main(String[] args) {

        System.out.println(strStr_1("abc edsfg sds", " h"));
        System.out.println(strStr_1("abc edsfg sds", ""));

    }
}
