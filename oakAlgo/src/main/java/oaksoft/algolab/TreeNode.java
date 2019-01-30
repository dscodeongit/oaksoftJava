package oaksoft.algolab;

import edu.princeton.cs.algs4.StdOut;
import oaksoft.algolab.medium.tree.BinaryTreeLevelOrderTraversal;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class TreeNode {
    public int val;
    public TreeNode left;
    public TreeNode right;

    public TreeNode(int x) {
        val = x;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(List<Integer> l : BinaryTreeLevelOrderTraversal.levelOrder(this)){
            sb.append("[");
            for(int v : l) {
                sb.append(v).append(" ");
            }
            sb.append("]");
        }
        return sb.toString();
    }

    //[1,2,3,3,null,2,null]
    public static TreeNode fromArrayPreOrder(Integer[] vals){
        return fromArrayPreOrder(vals, 0, vals.length-1);
    }
    public static TreeNode fromArrayPreOrder(Integer[] vals, int f, int t){
        if(vals == null || vals.length==0) return null;
        if(vals.length == 1){
            if(vals[0]!=null) return new TreeNode(vals[0]);
            return null;
        }
        int m = (f+t)/2;
        Integer val = vals[m];
        if(val == null) return null;
        TreeNode rn = new TreeNode(val);
        if(m>f) {
            TreeNode l = fromArrayPreOrder(vals, f, m - 1);
            rn.left = l;
        }
        if(t>m){
            TreeNode r = fromArrayPreOrder(vals, m+1, t);
            rn.right = r;
        }

        return rn;
    }

    public static TreeNode fromArrayTopDownByLevel(Integer[] vals){
        if(vals==null || vals.length == 0) return null;
        Queue<TreeNode> q = new LinkedList<>();
        TreeNode head = new TreeNode(vals[0]);
        q.offer(head);
        for (int i=1; i<=vals.length-1; i+=2){
            TreeNode p = q.poll();
            if(vals[i]!=null) {
                TreeNode tn = new TreeNode(vals[i]);
                p.left=tn;
                q.offer(tn);
            }

            if(i<vals.length-1 && vals[i+1]!=null){
                TreeNode tn = new TreeNode(vals[i+1]);
                p.right=tn;
                q.offer(tn);
            }
        }
        return head;
    }

    public static TreeNode buildTree(int[][] indexes) {
        Queue<TreeNode> q = new LinkedList<>();
        TreeNode root = new TreeNode(1);
        q.offer(root);
        int i = 0;
        while(!q.isEmpty()){
            TreeNode node = q.remove();
            if(indexes[i][0] != -1) {
                TreeNode l = new TreeNode(indexes[i][0]);
                node.left = l;
                q.offer(l);
            }
            if(indexes[i][1] != -1) {
                TreeNode r = new TreeNode(indexes[i][1]);
                node.right = r;
                q.offer(r);
            }
            i++;
        }
        return root;
    }

    public static void main(String[] args){
        //Integer[] vals = new Integer[]{5,4,1,null,1,null,4,2,null,2,null};
        Integer[] vals = new Integer[]{5,4,1,null,1,null,4,2,null,2,null};
        TreeNode tn = fromArrayTopDownByLevel(vals);
        StdOut.println("DONE");
    }

}
