package oaksoft.algolab.algoI.code;

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import oaksoft.algolab.ListNode;

/**
 * Shuffling a linked list. Given a singly-linked list containing nn items, rearrange the items uniformly at random.
 * Your algorithm should consume a logarithmic (or constant) amount of extra memory and run in time proportional to
 * n*log(n) in the worst case.
 */
public class ShuffleLinkedList {
    public static ListNode shuffle(ListNode head){
        ListNode tail = head;
        while(tail.next != null) tail = tail.next;
        return shuffle(head, tail);
    }

    private static ListNode shuffle(ListNode head, ListNode tail) {
        if(head == tail) return head;
        if(head.next == tail) {
            if(secondFirst()) {
               tail.next = head;
               head.next = null;
               return tail;
            }
            return head;
        } else {
            ListNode fast = head, slow = head;
            while (fast != tail && fast.next != tail) {
                fast = fast.next.next;
                slow = slow.next;
            }
            ListNode head2 = slow.next;
            slow.next = null;

            ListNode ln1 = shuffle(head, slow);
            ListNode ln2 = shuffle(head2, tail);

            ListNode merge = merge(ln1, ln2);
            return merge;
        }
    }

    private static ListNode merge(ListNode ln1, ListNode ln2) {
        ListNode dummy = new ListNode(0);
        ListNode dp = dummy;
        while(ln1 != null && ln2 != null) {
            if(secondFirst()) {
                dp.next = ln2;
                ln2 = ln2.next;
            } else {
                dp.next = ln1;
                ln1 = ln1.next;
            }
            dp = dp.next;
        }
        if(ln1 != null) dp.next = ln1;
        if(ln2 != null) dp.next = ln2;
        return dummy.next;
    }

    private static boolean secondFirst() {
        return StdRandom.uniform(2) == 1;
    }

    public static void main(String[] args) {
        ListNode ln1 = new ListNode(1);
        ListNode ln2 = new ListNode(2);

        ListNode ln3 = new ListNode(3);
        ListNode ln4 = new ListNode(4);
        ListNode ln5 = new ListNode(5);
        ListNode ln6 = new ListNode(6);

        ln1.next = ln2;
        ln2.next=ln3;
        ln3.next=ln4;
        ln4.next=ln5;
        ln5.next=ln6;

        ListNode ln = shuffle(ln1);

        StdOut.println(ln);
    }

}
