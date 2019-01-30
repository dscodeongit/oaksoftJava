package oaksoft.algolab;

import edu.princeton.cs.algs4.StdOut;
import oaksoft.algolab.medium.tree.BinaryTreeLevelOrderTraversal;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class TreeLinkNode extends TreeNode{
    public int val;
    public TreeLinkNode left;
    public TreeLinkNode right;
    public TreeLinkNode next;

    public TreeLinkNode(int x) {
        super(x);
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
    public static TreeLinkNode fromArrayPreOrder(Integer[] vals){
        return fromArrayPreOrder(vals, 0, vals.length-1);
    }
    public static TreeLinkNode fromArrayPreOrder(Integer[] vals, int f, int t){
        if(vals == null || vals.length==0) return null;
        if(vals.length == 1){
            if(vals[0]!=null) return new TreeLinkNode(vals[0]);
            return null;
        }
        int m = (f+t)/2;
        Integer val = vals[m];
        if(val == null) return null;
        TreeLinkNode rn = new TreeLinkNode(val);
        if(m>f) {
            TreeLinkNode l = fromArrayPreOrder(vals, f, m - 1);
            rn.left = l;
        }
        if(t>m){
            TreeLinkNode r = fromArrayPreOrder(vals, m+1, t);
            rn.right = r;
        }

        return rn;
    }

    public static TreeLinkNode fromArrayTopDownByLevel(Integer[] vals){
        if(vals==null || vals.length == 0) return null;
        Queue<TreeLinkNode> q = new LinkedList<>();
        TreeLinkNode head = new TreeLinkNode(vals[0]);
        q.offer(head);
        for (int i=1; i<=vals.length-1; i+=2){
            TreeLinkNode p = q.poll();
            if(vals[i]!=null) {
                TreeLinkNode tn = new TreeLinkNode(vals[i]);
                p.left=tn;
                q.offer(tn);
            }

            if(i<vals.length-1 && vals[i+1]!=null){
                TreeLinkNode tn = new TreeLinkNode(vals[i+1]);
                p.right=tn;
                q.offer(tn);
            }
        }
        return head;
    }

    public static void main(String[] args){
        //Integer[] vals = new Integer[]{5,4,1,null,1,null,4,2,null,2,null};
        Integer[] vals = new Integer[]{5,4,1,null,1,null,4,2,null,2,null};
        TreeLinkNode tn = fromArrayTopDownByLevel(vals);
        StdOut.println("DONE");
    }

}
