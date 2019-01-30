package oaksoft.algolab.algoI.ass2;

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;


public class RandomizedQueue<Item> implements Iterable<Item> {
    private int n;         // number of elements on deque
    private Node first;    // beginning of deque
    private Node last;     // end of deque

    // construct an empty randomized queue
    public RandomizedQueue() {
        first = null;
        last  = null;
        n = 0;
    }
    // is the randomized queue empty?
    public boolean isEmpty() {
        return n == 0;

    }
    // return the number of items on the randomized queue
    public int size() {
        return n;
    }
    // add the item
    public void enqueue(Item item) {
        if(item == null) throw new IllegalArgumentException("Cannot add null.");
        Node tmp = last;
        last = new Node(item);
        last.next = null;
        if (isEmpty()) first = last;
        else tmp.next = last;
        n++;
    }
    // remove and return a random item
    public Item dequeue() {
        if(isEmpty()) throw new NoSuchElementException("No element to return.");
        Node tmp = first;
        first = first.next;
        tmp.next = null;
        n--;
        return tmp.item;
    }
    // return a random item (but do not remove it)
    public Item sample() {
        if(isEmpty()) throw new NoSuchElementException ("No element to return.");
        return iterator().next();
    }
    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
        return new Iterator<Item>() {
            Item[] copy = (Item[])copyToListAndShuffle();
            int i = 0;
            @Override
            public boolean hasNext() {
                return i < copy.length;
            }

            @Override
            public Item next() {
                if(i >= copy.length) throw new NoSuchElementException ("No element to return.");
                return copy[i++];
            }

            private Object[] copyToListAndShuffle(){
                Node pointer = first;
                Object[] copy = new Object[size()];
                int i =0;
                while(pointer != null){
                    copy[i++] = pointer.item;
                    pointer = pointer.next;
                }
                for(int j = 0; j< copy.length; j++) {
                    int k = StdRandom.uniform(size());
                    Object s = copy[j];
                    copy[j] = copy[k];
                    copy[k] = s;
                }
                return copy;
            }
        };
    }
    private class Node {
        private Item item;
        private Node next;
        Node(Item item) {
            this.item = item;
        }
    }


    // unit testing (optional)
    public static void main(String[] args) {

        RandomizedQueue<String> rq = new RandomizedQueue<>();
        rq.enqueue("a");
        rq.enqueue("b");
        rq.enqueue("c");
        rq.enqueue("d");
        rq.enqueue("e");
        rq.enqueue("f");

        Iterator<String> it = rq.iterator();
        StdOut.println(rq.size());
        while(it.hasNext()) {
            StdOut.println(it.next());
        }
        StdOut.println("****************");
        StdOut.println(rq.size());

        rq.dequeue();
        StdOut.println(rq.size());

        rq.dequeue();
        StdOut.println(rq.size());

        it = rq.iterator();

        while(it.hasNext()) {
            StdOut.println(it.next());
        }
    }
}