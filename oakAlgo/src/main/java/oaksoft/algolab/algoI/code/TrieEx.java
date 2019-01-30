package oaksoft.algolab.algoI.code;

import edu.princeton.cs.algs4.StdOut;

import java.util.*;

public class TrieEx {

    public int[] wordWithPrefix(String[] s, String[] prefixes){
        TrieNode root = new TrieNode(null);

        for(String w : s) {
            root.addChild(w);
        }

        int[] count = new int[prefixes.length];

        for(int i=0; i<prefixes.length; i++){
            count[i] = getPrefixCount(root, prefixes[i]);
        }

        return count;
    }

    private int getPrefixCount(TrieNode root, String prefix){
        char c = prefix.charAt(0);
        if(root.children.containsKey(c)) {
            TrieNode tn = root.children.get(c);
            TrieNode target = tn.search(prefix);
            if(target != null) return target.descentCount;
        }
        return 0;
    }

    private class TrieNode {
        String v;
        Map<Character, TrieNode> children;

        private int descentCount;

        TrieNode(String v) {
            this.v = v;
            children = new HashMap<>();
        }

        void addChild(String suffix) {
            if(suffix!=null && !suffix.isEmpty()) {
                char c = suffix.charAt(0);
                children.computeIfAbsent(c, k->new TrieNode(v==null? String.valueOf(c) : v+String.valueOf(c))).addChild(suffix.substring(1));
            }
            descentCount++;
        }

        TrieNode search(String suffix) {
            if(v == null) {
                if(children.containsKey(suffix.charAt(0))) {
                    TrieNode tn = children.get(suffix.charAt(0));
                    return tn.search(suffix);
                }else{
                    return null;
                }
            }else if(suffix.equals(v)) return this;
            else {
                char c = suffix.charAt(v.length());
                if(children.containsKey(c)){
                    return children.get(c).search(suffix);
                }
            }
            return null;
        }

        public int getDescentCount(){
            return descentCount;
        }
    }

    public int[] contacts(String[][] queries) {
        List<Integer> ans = new ArrayList<>();
        TrieNode root = new TrieNode(null);
        for(String[] q : queries) {
            if(q[0].equals("add")){
                root.addChild(q[1]);
            }else if(q[0].equals("find")){
                TrieNode target = root.search(q[1]);
                int count = (target == null ? 0 : target.descentCount);
                ans.add(count);
            }
        }
        int[] rs = new int[ans.size()];
        int i = 0;
        for(int c : ans){
            rs[i++]=c;
        }
        return rs;
    }


    public static void main(String[] args){
        TrieEx trieEx = new TrieEx();
        String[] s = {"try", "trie", "tyying", "tree", "triangle","triple", "tea", "tee", "ace", "able", "attemp","bee", "be", "best"};
        String[] prefixes = {"t", "tr", "try", "att", "attemp", "be", "hello"};
       // int wordsWithPrefix = trieEx.wordWithPrefix(s, "tr");

        StdOut.println(Arrays.toString(trieEx.wordWithPrefix(s, prefixes)));

        StdOut.println("Done");
    }
}
