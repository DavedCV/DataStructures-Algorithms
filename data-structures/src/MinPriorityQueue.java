import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class MinPriorityQueue<Key extends Comparable<Key>> implements Iterable<Key> {

    private static final int FIXED_SIZE = 8;

    // we are going to use the convention based on a 1 based array to simplify the index manipulation
    private Key[] pq;

    // index of the last position of an element in the pq
    private int n;

    public MinPriorityQueue(){
        pq = (Key[]) new Comparable[FIXED_SIZE];
        n = 0;
    }

    public MinPriorityQueue(int size) {
        pq = (Key[]) new Comparable[size];
        n = 0;
    }

    // Construct a priority queue (using heapify) from an array.
    // O(n)
    public MinPriorityQueue(Key[] elements) {

        // create the heap array with a good length in order to save the items in a 1 indexed fashion
        pq = (Key[]) new Comparable[elements.length+1];
        n = 0;

        // place all elements in the map and heap
        for (int i = 0; i < elements.length; i++) {
            pq[++n] = elements[i];
        }

        // heapify process O(n)
        // start from n / 2 cause all leaf nodes (nodes after the middle of the array) are heaps of size 1, already following the invariant
        for (int k = n / 2; k >= 1; k--) {
            sink(k);
        }
    }

    // Construct the heap from a collection of element
    // O(nlog(n))
    public MinPriorityQueue(Collection<Key> elements){
        this(elements.size()+1);
        for (Key elem : elements) insert(elem);
    }

    // test of the pq is empty
    public boolean isEmpty() {
        return n == 0;
    }

    // return the actual capacity of the heap
    public int capacity() {
        return pq.length;
    }

    // return the number of elements in the pq
    public int size() {
        return n;
    }

    // clear the heap and the map
    // O(n)
    public void clear() {
        for (int i = 0; i < n; i++) {
            pq[++i] = null;
        }
        n = 0;
    }

    // return the element at the root of the pq
    // O(1)
    public Key peek() {
        if (isEmpty()) return null;
        return pq[1];
    }

    // insert an item at the end of the array and the swim to maintain the invariant
    // O(log(n))
    public void insert(Key item) {

        if (item == null) throw new IllegalArgumentException();

        if (n == pq.length-1) resize(pq.length * 2);

        pq[++n] = item;

        swim(n);
    }

    // scenario when child's key become smaller than parent's kys
    private void swim(int index) {

        // while index is greater than the index of root key and parent is greater than element
        // at index

        // parent of node at index is in index / 2
        while (index > 1 && less(index, index / 2)){
            exch(index, index / 2);
            index /= 2;
        }
    }

    // O(log(n))
    public Key deleteMin() {

        if (isEmpty()) throw new NoSuchElementException("Priority queue underflow");

        Key element = pq[1];

        exch(1, n);
        pq[n--] = null;
        sink(1);

        if (n > 0 && n == pq.length / 4) resize(pq.length / 2);

        return element;
    }

    private void sink(int index) {

        // while there are childs
        while (2*index <= n) {
            int j = 2*index;

            // select the smallest of node child's
            // the child's of a node are those nodes in the index 2*k and 2*k+1
            if (j < n && less(j+1, j)) j++;

            // if node at index is smaller than the smallest child node, the stop
            if (less(index, j)) break;

            // exchange and update the index
            exch(index, j);
            index = j;
        }
    }

    // is pq[1..n] a min heap?
    private boolean isMinHeap() {
        for (int i = 1; i <= n; i++) {
            if (pq[i] == null) return false;
        }
        for (int i = n+1; i < pq.length; i++) {
            if (pq[i] != null) return false;
        }
        if (pq[0] != null) return false;
        return isMinHeap(1);
    }

    // Recursively checks if this heap is a min heap
    // This method is just for testing purposes to make
    // sure the heap invariant is still being maintained
    // Called this method with k=1 to start at the root
    public boolean isMinHeap(int k) {

        if (k > n) return true;

        int left = 2 * k;
        int right = 2 * k + 1;

        // Make sure that the current node k is less than
        // both of its children left, and right if they exist
        // return false otherwise to indicate an invalid heap
        if (left <= n && !less(k, left)) return false;
        if (right <= n && !less(k, right)) return false;

        // Recurse on both children to make sure they're also valid heaps
        return isMinHeap(left) && isMinHeap(right);
    }

    public String toString() {
        return Arrays.toString(Arrays.copyOfRange(pq, 1, n+1));
    }

    private boolean less(int i, int j) {
        return pq[i].compareTo(pq[j]) < 0;
    }

    private void exch(int i, int j) {
        Key aux = pq[i];
        pq[i] = pq[j];
        pq[j] = aux;
    }

    private void resize(int len) {
        Key[] copy = (Key[]) new Comparable[len];
        System.arraycopy(pq, 0, copy, 0, n+1);
        pq = copy;
    }

    // Returns an iterator that iterates over the keys on this priority queue
    // in ascending order.
    public Iterator<Key> iterator() {
        return new HeapIterator();
    }

    private class HeapIterator implements Iterator<Key> {

        private final MinPriorityQueue<Key> copy;

        public HeapIterator() {
            copy = new MinPriorityQueue<Key>(size());

            for (int i = 0; i < n; i++) {
                copy.insert(pq[++i]);
            }
        }

        @Override
        public boolean hasNext() {
            return !copy.isEmpty();
        }

        @Override
        public Key next() {
            return copy.deleteMin();
        }
    }

    public static void main(String[] args) {
        MinPriorityQueue<Integer> pq = new MinPriorityQueue<>(new Integer[]{7, 6, 5, 4, 3, 2, 1});
        System.out.println("Deleted min: " + pq.deleteMin());
        System.out.println("Deleted min: " + pq.deleteMin());
        System.out.println("Deleted min: " + pq.deleteMin());

        System.out.println("Insert -100");
        pq.insert(-100);
        System.out.println("PQ: " + pq.toString());
        System.out.println("Min PQ: " +  pq.isMinHeap(1));

        System.out.println("Deleted min: " + pq.deleteMin());
        System.out.println("PQ: " + pq.toString());
        System.out.println("Min PQ: " +  pq.isMinHeap(1));
    }
}
