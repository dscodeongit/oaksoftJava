package oaksoft.algolab.algoI.code;

import java.util.LinkedList;
import java.util.Queue;

public class LevelOrderTraversal {
    public static void levelOrder(Node root) {
        Queue<Node> s = new LinkedList<>();

        s.add(root);
        StringBuilder sb = new StringBuilder();
        while (!s.isEmpty()) {
            Node n = s.remove();
            sb.append(n.data).append(" ");
            if (n.left != null) s.add(n.left);
            if (n.right != null) s.add(n.right);
        }
        System.out.println(sb.toString().trim());
    }

    class Node {
        int data;
        Node left;
        Node right;
    }

}
