package oaksoft.algolab.algoI.ass2;

import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque <Item> implements Iterable<Item> {
    private int n;         // number of elements on deque
    private Node first;    // beginning of deque
    private Node last;     // end of deque

    // construct an empty deque
    public Deque() {
        first = null;
        last  = null;
        n = 0;
    }
    // is the deque empty?
    public boolean isEmpty() {
        return n == 0;
    }
    // return the number of items on the deque
    public int size() {
        return n;
    }

    // add the item to the front
    public void addFirst(Item item) {
        if(item == null) throw new IllegalArgumentException("Cannot add null.");
        Node tmp = first;
        first = new Node(item);
        first.next = tmp;
        if(tmp != null) tmp.previous = first;
        if (isEmpty()) last = first;
        n++;
    }
    // add the item to the end
    public void addLast(Item item) {
        if(item == null) throw new IllegalArgumentException("Cannot add null.");
        Node tmp = last;
        last = new Node(item);
        last.next = null;
        last.previous = tmp;
        if (isEmpty()) first = last;
        else if(tmp != null){
            tmp.next = last;
        }
        n++;
    }
    // remove and return the item from the front
    public Item removeFirst() {
        if(isEmpty()) throw new NoSuchElementException("No element to return.");
        Node tmp = first;
        first = first.next;
        if(first != null) {
            first.previous = null;
        }
        if(last == tmp) last = null;
        tmp.next = null;
        n--;
        return tmp.item;

    }
    // remove and return the item from the end
    public Item removeLast() {
        if(isEmpty()) throw new NoSuchElementException ("No element to return.");
        Node tmp = last;
        last = last.previous;
        if(last != null) last.next = null;
        else first = null;
        tmp.previous = null;
        n--;
        return tmp.item;
    }
    // return an iterator over items in order from front to end
    public Iterator<Item> iterator() {
        return new Iterator<Item>() {
            Node pointer = first;
            @Override
            public boolean hasNext() {
                return pointer.next != null;
            }

            @Override
            public Item next() {
                if(pointer.next == null) throw new NoSuchElementException ("No element to return.");
                pointer = pointer.next;
                return pointer.item;
            }
        };
    }

    private class Node {
        private Item item;
        private Node next;
        private Node previous;
        Node(Item item) {
            this.item = item;
        }
    }

    // unit testing (optional)
    public static void main(String[] args) {
        Deque<String> de = new Deque<>();
        StdOut.println(de.isEmpty());
        de.addLast("h");

        de.addFirst("a");
        de.addFirst("b");
        de.addFirst("c");
        de.addFirst("d");
        de.addFirst("e");
        de.addFirst("f");
        de.addLast("g");
        StdOut.println(de.isEmpty());
        StdOut.println(de.size());

        de.removeFirst();
        de.removeLast();
        StdOut.println(de.size());

        Iterator<String> it = de.iterator();

        while(it.hasNext()) {
            StdOut.println(it.next());
        }

    }
}