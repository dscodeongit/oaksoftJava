package oaksoft.algolab.algoI.code;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class BalancedBrackets {
    static String isBalanced(String s) {
        Map<Character, Character> brackMap = new HashMap<>();
        brackMap.put(')', '(');
        brackMap.put(']', '[');
        brackMap.put('}', '{');
        Stack<Character> stack = new Stack<>();
        for(int i = 0; i < s.length(); i++){
            char c = s.charAt(i);
            if(c == '(' || c == '[' || c == '{'){
                stack.push(c);
            }else if(c == ')' || c == ']' || c == '}'){
                if(stack.isEmpty() || stack.pop() != brackMap.get(c)) return "NO";
            }
        }
        return stack.isEmpty() ? "YES" : "NO";

    }
}
