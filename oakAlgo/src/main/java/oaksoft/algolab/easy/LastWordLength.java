package oaksoft.algolab.easy;

public class LastWordLength {
    public static int lengthOfLastWord(String s) {
        if(s == null || s.trim().length() ==0 ){
            return 0;
        }
        String ss = s.trim();
        if(!ss.contains(" ")) return ss.length();

        return ss.length()-1-ss.lastIndexOf(" ");

    }

    public static void main(String[] args) {

        System.out.println(lengthOfLastWord("b a "));
        System.out.println(lengthOfLastWord("abc edsfg sd s"));

    }
}
