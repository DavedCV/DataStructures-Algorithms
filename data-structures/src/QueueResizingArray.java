import java.util.Iterator;
import java.util.NoSuchElementException;

public class QueueResizingArray<Item> implements Iterable<Item> {

    // Initial capacity of underlying resizing array
    private static final int INIT_CAPACITY = 8;

    private Item[] queue; //queue elements
    private int n; // number of elements in the queue
    private int first; // index of first element of the queue
    private int last; // index of next available slot

    public QueueResizingArray() {
        queue = (Item[]) new Object[INIT_CAPACITY];
        n = 0;
        first = 0;
        last = 0;
    }

    public boolean isEmpty() {
        return n == 0;
    }

    public int size() {
        return n;
    }

    // resize the underlying array
    public void resize(int capacity) {
        Item[] copy = (Item[]) new Object[capacity];

        for (int i = 0; i < n; i++) {
            copy[i] = queue[(first + i) % queue.length];
        }

        queue = copy;
        first = 0;
        last = n;
    }

    public void enqueue(Item item) {
        if (n == queue.length) resize(queue.length*2);

        queue[last] = item;
        last = (last + 1) % queue.length;
        n++;
    }

    public Item dequeue() {
        if (isEmpty()) throw new NoSuchElementException("Queue Underflow");

        // get item and avoid loitering
        Item item = queue[first];
        queue[first] = null;

        // update index and size
        n--;
        first = (first + 1) % queue.length;

        // shrink size of array if necessary
        if (n > 0 && n == queue.length/4) resize(queue.length/2);

        return item;
    }

    public Item peek(){
        if (isEmpty()) throw new NoSuchElementException("Queue Underflow");

        return queue[first];
    }

    public Iterator<Item> iterator() {
        return new Iterator<Item>() {

            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < n;
            }

            @Override
            public Item next() {
                Item item = queue[(first + index) % queue.length];
                index++;

                return item;
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");

        for (int i = 0; i < n; i++) {
            if (first + i == last - 1) {
                sb.append(queue[(first + i) % queue.length]).append("]");
                continue;
            }
            sb.append(queue[(first + i) % queue.length]).append(", ");
        }

        return sb.toString();
    }

    public static void main(String[] args) {
        QueueResizingArray<Integer> queue = new QueueResizingArray<>();

        queue.enqueue(1);
        queue.enqueue(2);
        queue.enqueue(3);
        queue.enqueue(4);
        queue.enqueue(5);

        System.out.println("Enqueue up to 5: ");
        System.out.println(queue);

        System.out.println("Dequeue 2 times: ");
        queue.dequeue();
        queue.dequeue();
        System.out.println(queue);
    }
}
