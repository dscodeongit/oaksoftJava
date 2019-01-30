package oaksoft.algolab;

public class ListNode {
    public int val;
    public ListNode next;
    public ListNode(int x) { val = x; }



    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(val);

        ListNode p = next;
        while (p != null && p.next != this){
            sb.append("->").append(p.val);
            p = p.next;
        }

        return sb.toString();
    }


    public static ListNode buildFromVals(int[] vals){
        ListNode next = new ListNode(vals[vals.length-1]);
        for(int i=vals.length-2; i>=0; i--){
            ListNode n = new ListNode(vals[i]);
            n.next = next;
            next = n;
        }
        return next;
    }
}
