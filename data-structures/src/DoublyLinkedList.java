import java.util.Iterator;

public class DoublyLinkedList<T> implements Iterable<T> {

    private int size = 0;
    private Node head = null;
    private Node tail = null;

    private class Node{
        T data;
        Node prev, next;

        public Node(T data, Node prev, Node next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }

        @Override
        public String toString() {
            return data.toString();
        }
    }

    // O(n)
    public void clear(){
        Node trav = head;
        while(trav != null) {
            Node next = trav.next;
            trav.prev = trav.next = null;
            trav.data = null;

            trav = next;
        }

        head = tail = null;
        size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty(){
        return size == 0;
    }

    // O(1)
    public void add(T elem){
        addLast(elem);
    }

    // O(1)
    public void addLast(T elem) {
        if (isEmpty()) {
            head = tail = new Node(elem, null, null);
        }else {
            tail.next = new Node(elem, tail, null);
            tail = tail.next;
        }

        size++;
    }

    // O(1)
    public void addFirst(T elem) {
        if (isEmpty()) {
            head = tail = new Node(elem, null, null);
        } else {
          head.prev = new Node(elem, null, head);
          head = head.prev;
        }

        size++;
    }

    // O(1)
    public T peekFirst(){
        if (isEmpty()) throw new RuntimeException("Empty List");
        else return head.data;
    }

    // O(1)
    public T peekLast(){
        if (isEmpty()) throw new RuntimeException("Empty List");
        else return tail.data;
    }

    // O(1)
    public T removeFirst(){
        if (isEmpty()) throw new RuntimeException("Empty List");

        Node oldFirst = head;
        head = head.next;
        head.prev = null;
        size--;
        if (isEmpty()) tail = null;

        return oldFirst.data;
    }

    // O(1)
    public T removeLast(){
        if (isEmpty()) throw new RuntimeException("Empty List");

        Node oldLast = tail;
        tail = tail.prev;
        tail.next = null;
        size--;

        if(isEmpty()) head = null;

        return oldLast.data;
    }

    // remove an arbitrary node from the linked list, 0(1)
    private T remove(Node node) {
        // if the node to remove is at the head or tail, handle those cases independently
        if (node.prev == null) removeLast();
        if (node.next == null) removeLast();

        // make nodes adjacent to skip the node
        node.next.prev = node.prev;
        node.prev.next = node.next;

        // store the data to be returned
        T data = node.data;

        // memory cleanup
        node.data = null;
        node = node.prev = node.next = null;

        size--;

        return data;
    }

    // O(n)
    public T removeAt(int index){
        if (index < 0 || index >= size) throw new IllegalArgumentException();

        int i = 0;
        Node trav;

        if (index < size/2){
            for (i = 0, trav = head; i != index; i++)
                trav = trav.next;
        }else {
            for (i = size-1, trav = tail; i != index; i--)
                trav = trav.prev;
        }

        return remove(trav);
    }

    // remove a particular value in a linked list, O(n)
    public boolean remove(Object obj){
        Node trav = head;

        // support searching for null
        if (obj == null){
            for (trav = head; trav != null; trav = trav.next){
                if (trav.data == null){
                    remove(trav);
                    return true;
                }
            }
        }else{
            for (trav = head; trav != null; trav = trav.next){
                if (obj.equals(trav.data)){
                    remove(trav);
                    return true;
                }
            }
        }

        return false;
    }

    // O(n)
    public int indexOf(Object obj){
        int index = 0;
        Node trav = head;

        if (obj == null){
            for (trav = head; trav != null; trav = trav.next, index++)
                if (trav.data == null)
                    return index;
        }else{
            for (trav = head; trav != null; trav = trav.next, index++)
                if (obj.equals(trav.data))
                    return index;
        }

        return -1;
    }

    public boolean contains(Object obj) {
        return indexOf(obj) != -1;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            Node trav = head;

            @Override
            public boolean hasNext() {
                return trav != null;
            }

            @Override
            public T next() {
                T data = trav.data;
                trav = trav.next;
                return data;
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        Node trav = head;
        while (trav != null) {
            sb.append(trav.data);
            if (trav.next != null)
                sb.append(", ");
            trav = trav.next;
        }

        sb.append("]");
        return sb.toString();
    }
}
