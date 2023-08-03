import java.util.*;

public class MinPriorityQueueQuickRemovals<Key extends Comparable<Key>> {

    private static final int FIXED_SIZE = 8;

    // we are going to use the convention based on a 1 based array to simplify the index manipulation
    private Key[] pq;

    // index of the last position of an element
    private int n;

    /*
    * This maps keep track of the possible indices a particular node
    * is found in the heap. Having this mapping lets us have O(log(n))
    * removals and O(1) element containment check at the cost of some
    * additional space and minor overhead
    * */
    private Map<Key, TreeSet<Integer>> map = new HashMap<>();

    public MinPriorityQueueQuickRemovals(){
        pq = (Key[]) new Comparable[FIXED_SIZE];
        n = 0;
    }

    public MinPriorityQueueQuickRemovals(int size){
        pq = (Key[]) new Comparable[size];
        n = 0;
    }

    // Construct a priority queue (using heapify) from an array.
    // O(n)
    public MinPriorityQueueQuickRemovals(Key[] elements) {

        // create the heap array with a good length in order to save the items in a 1 indexed fashion
        pq = (Key[]) new Comparable[elements.length+1];
        n = 0;

        // place all elements in the map and heap
        for (int i = 0; i < elements.length; i++) {
            pq[++n] = elements[i];
            mapAdd(elements[i], i);
        }

        // heapify process O(n)
        // start from n / 2 cause all leaf nodes (nodes after the middle of the array) are heaps of size 1, already following the invariant
        for (int k = n / 2; k >= 1; k--) {
            sink(k);
        }
    }

    // Construct the heap from a collection of element
    // O(nlog(n))
    public MinPriorityQueueQuickRemovals(Collection<Key> elements){
        this(elements.size()+1);
        for (Key elem : elements) insert(elem);
    }

    // test if the pq is empty
    public boolean isEmpty() {
        return n == 0;
    }

    // return the actual capacity of the heap
    public int capacity() {
        return pq.length;
    }

    // clear the heap and the map
    public void clear() {
        for (int i = 0; i < n; i++) {
            pq[++i] = null;
        }
        n = 0;
        map.clear();
    }

    // return the element at the root of the pq
    // O(1)
    public Key peek() {
        if (isEmpty()) return null;
        return pq[1];
    }

    // Test if an element is in heap
    // O(1)
    public boolean contains(Key element) {
        if (element == null) return false;

        // map lookup to check containment, O(1)
        return map.containsKey(element);
    }

    // insert an item at the end of the array and the swim to maintain the invariant
    public void insert(Key item) {

        if (item == null) throw new IllegalArgumentException();

        if (n == pq.length-1) resize(pq.length * 2);

        pq[++n] = item;

        mapAdd(item, n);

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

    // removes a particular element in the heap
    // O(log(n))
    public boolean remove(Key element) {
        if (element == null) return false;

        int index = mapGet(element);
        if (index != -1) removeAt(index);
        return index != -1;
    }

    public Key removeAt(int index) {
        if (isEmpty()) return null;

        Key element = pq[index];
        exch(index, n);

        pq[n] = null;
        mapRemove(element, n);
        n--;

        // removed last element, nothing more to do
        if (index == n+1) return element;

        // Element that was in the last position of the heap before the exch
        Key exchElement = pq[index];

        // try sinking
        sink(index);

        // if sinking didn't work, then try swim the element
        if (pq[index] == exchElement) swim(index);

        return element;
    }

    // Recursively checks if this heap is a min heap
    // This method is just for testing purposes to make
    // sure the heap invariant is still being maintained
    // Called this method with k=1 to start at the root
    public boolean isMinHeap(int k) {

        if (k >= n) return true;

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

    @Override
    public String toString() {
        return Arrays.toString(Arrays.copyOfRange(pq, 1, n+1));
    }

    private boolean less(int i, int j) {
        return pq[i].compareTo(pq[j]) < 0;
    }

    private void exch(int i, int j){
        Key aux = pq[i];
        pq[i] = pq[j];
        pq[j] = aux;

        mapExch(pq[i], pq[j], i, j);
    }

    // add a node value and its index to the map
    private void mapAdd(Key value, int index) {
        TreeSet <Integer> set = map.get(value);

        // New value being inserted in map
        if (set == null) {
            set = new TreeSet<>();
            set.add(index);
            map.put(value, set);
        }else{
            // value already exist in map
            set.add(index);
        }
    }

    // Remove the index at a given value
    // O(log(n))
    private void mapRemove(Key value, int index) {
        TreeSet<Integer> set = map.get(value);
        set.remove(index); // TreeSet takes O(log(n)) time to remove

        if (set.isEmpty()) map.remove(value);
    }

    // Return the index of the value in the heap
    private int mapGet(Key value) {
        TreeSet<Integer> set = map.get(value);

        if (set != null) return set.last();
        return -1;
    }

    // Exch the index of two nodes internally within the map
    private void mapExch(Key value1, Key value2, int index1, int index2) {

        TreeSet <Integer> set1 = map.get(value1);
        TreeSet <Integer> set2 = map.get(value2);

        set1.remove(index1);
        set2.remove(index2);

        set1.add(index2);
        set2.add(index1);
    }

    private void resize(int len) {
        Key[] copy = (Key[]) new Comparable[len];
        System.arraycopy(pq, 0, copy, 0, n+1);
        pq = copy;
    }


    public static void main(String[] args) {
        /*
        MinPriorityQueue<Integer> pq = new MinPriorityQueue<Integer>();

        System.out.println("Capacity: " + pq.capacity());

        pq.insert(5);
        pq.insert(4);
        pq.insert(3);
        pq.insert(2);
        pq.insert(1);

        System.out.println("Deleted min: " + pq.deleteMin());
        System.out.println("Deleted min: " + pq.deleteMin());
        System.out.println("Deleted min: " + pq.deleteMin());

        pq.insert(-100);
        System.out.println("Deleted min: " + pq.deleteMin());
        System.out.println("Deleted min: " + pq.deleteMin());
         */

        MinPriorityQueueQuickRemovals<Integer> pq = new MinPriorityQueueQuickRemovals<>(new Integer[]{7, 6, 5, 4, 3, 2, 1});
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
