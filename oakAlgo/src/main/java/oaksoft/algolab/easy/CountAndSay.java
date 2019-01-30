package oaksoft.algolab.easy;

public class CountAndSay {
    public static String countAndSay(int n) {
        return sayThis("1", 1, n);
    }

    private static String sayThis(String s, int said, int max){
        if(said == max){
            return s;
        }
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for(int i=0; i<s.length(); i++){
            Character c = s.charAt(i);
            if(i==0 || (i>0 && c == s.charAt(i-1))){
                count++;
            }else{
                sb.append(String.valueOf(count)+(s.charAt(i-1)-'0'));
                count=1;
            }
            if(i==s.length()-1){
                sb.append(String.valueOf(count)+(c-'0'));
            }
        }
        return sayThis(sb.toString(), said+1, max);
    }

    public static String countAndSay_1(int n) {
        String said = "";
        for(int i=1; i <= n; i++){
            said = sayThis(said);
        }
        return said;
    }

    private static String sayThis(String s){
        if(s==""){
            return "1";
        }
        StringBuilder sb = new StringBuilder();
        int count = 0;

        for(int i=0; i<s.length(); i++){
            Character c = s.charAt(i);
            if(i==0 || (i>0 && c == s.charAt(i-1))){
                count++;
            }else{
                sb.append(String.valueOf(count)+(s.charAt(i-1)-'0'));
                count=1;
            }
            if(i==s.length()-1){
                sb.append(String.valueOf(count)+(c-'0'));
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(countAndSay_1(20));
    }
}
