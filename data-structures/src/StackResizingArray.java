import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class StackResizingArray<Item> implements Iterable<Item> {

    // initial capacity of underlying resizing array
    private static final int INIT_CAPACITY = 8;
    private Item[] stack; // array of items
    private int n; // number of element on stack

    public StackResizingArray() {
        stack = (Item[]) new Object[INIT_CAPACITY];
        n = 0;
    }

    public boolean isEmpty() {
        return n == 0;
    }

    public int size() {
        return n;
    }

    // resize the underlying array holding the elements
    private void resize(int capacity) {
        stack = Arrays.copyOf(stack, capacity);
    }

    public void push(Item item) {
        if (n == stack.length) resize(stack.length*2);
        stack[n++] = item;
    }

    public Item pop() {
        if (isEmpty()) throw new NoSuchElementException("Stack Underflow");

        Item item = stack[--n];
        stack[n] = null;

        if (n > 0 && n == stack.length/4) resize(stack.length/2);
        return item;
    }

    public Item peek() {
        if (isEmpty()) throw new NoSuchElementException("Stack Underflow");

        return stack[n-1];
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");

        for (int i = 0; i < n; i++) {
            if (i == n-1) {
                sb.append(stack[i]).append("]");
                continue;
            }
            sb.append(stack[i]).append(", ");
        }

        return sb.toString();
    }

    @Override
    public Iterator<Item> iterator() {
        return new Iterator<Item>() {

            private int index = 0;
            @Override
            public boolean hasNext() {
                return index < n;
            }

            @Override
            public Item next() {
                return stack[index++];
            }
        };
    }

    public static void main(String[] args) {
        StackResizingArray<Integer> stack = new StackResizingArray<>();

        stack.push(1);
        stack.push(2);
        stack.push(3);
        stack.push(4);
        stack.push(5);

        System.out.println("Stack after pushing up to 5: ");
        System.out.println(stack);

        stack.pop();
        stack.pop();

        System.out.println("Stack after 2 pop: ");
        System.out.println(stack);

    }
}
