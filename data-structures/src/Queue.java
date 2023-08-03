import java.util.Iterator;
import java.util.LinkedList;

public class Queue <T> implements Iterable<T> {

    private LinkedList<T> list = new LinkedList<>();

    public Queue(){}

    public Queue(T firstElement) {
        enqueue(firstElement);
    }

    // return the size of the queue
    public int size() {
        return list.size();
    }

    // return if the list is empty
    public boolean isEmpty() {
        return size() == 0;
    }

    // peek the element at the front of the queue
    public T peek(){
        if (isEmpty()) throw new RuntimeException("Queue Empty");
        return list.peekFirst();
    }

    // dequeue an element
    public T dequeue(){
        if (isEmpty()) throw new RuntimeException("Queue Empty");
        return list.removeFirst();
    }

    // enqueue an element
    public void enqueue(T element) {
        list.addLast(element);
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");

        for (T entry : list) {
            if (entry == list.getLast()) {
                sb.append(entry).append("]");
                continue;
            }
            sb.append(entry).append(", ");
        }

        return sb.toString();
    }

    public static void main(String[] args) {
        Queue<Integer> queue = new Queue<>();

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
