import java.util.Iterator;
import java.util.NoSuchElementException;

@SuppressWarnings("unchecked")
public class ResizingArray <Item> implements Iterable<Item>{

    private static final int INIT_CAPACITY = 8;
    private Item[] arr;
    private int len = 0; // Length user thinks array is
    private int capacity = 0; // Actual array size

    public ResizingArray() {
        this(INIT_CAPACITY);
    }

    public ResizingArray(int capacity){
        if (capacity < 0) throw new IllegalArgumentException("Illegal capacity: " + capacity);
        this.capacity = capacity;
        arr = (Item[]) new Object[capacity];
    }

    public int size(){
        return len;
    }

    public boolean isEmpty(){
        return size() == 0;
    }

    public Item get(int index){
        if (index >= size()) throw new IndexOutOfBoundsException();
        return arr[index];
    }

    public void set(int index, Item item){
        if (index >= size()) throw new IndexOutOfBoundsException();
        arr[index] = item;
    }
    
    public void clear(){
        for (int i = 0; i < capacity; i++) {
            arr[i] = null;
        }
        len = 0;
    }

    public void resize(int capacity) {
        Item[] newArray = (Item[]) new Object[capacity];
        if (len >= 0) System.arraycopy(arr, 0, newArray, 0, len);
        arr = newArray;
        this.capacity = capacity;
    }

    public void add(Item item){

        // time to resize when needed
        if (len+1 > capacity){
            if (capacity == 0) resize(1);
            else resize(capacity*2);
        }

        arr[len++] = item;
    }

    public Item removeAt(int rm_index){
        if (rm_index >= size() || rm_index < 0) throw new IndexOutOfBoundsException();
        if (isEmpty()) throw new NoSuchElementException("Stack Underflow");

        Item data = arr[rm_index];

        Item[] newArr = (Item[]) new Object[capacity];
        for (int i = 0; i < len; i++) {
            if (i == rm_index) continue;
            newArr[i] = arr[i];
        }
        arr = newArr;
        len--;

        if (len > 0 && len == capacity/4) resize(capacity/2);
        return data;
    }

    public boolean remove(Item item){

        if (isEmpty()) throw new NoSuchElementException("Stack Underflow");

        for (int i = 0; i < len; i++) {
            if (arr[i].equals(item)) {
                removeAt(i);
                return true;
            }
        }
        return false;
    }

    public Item pop(){
        return removeAt(len-1);
    }

    public int indexOf(Item item){
        for (int i = 0; i < len; i++) {
            if (arr[i].equals(item)){
                return i;
            }
        }
        return -1;
    }

    public Item peek(){
        if (isEmpty()) throw new NoSuchElementException("Stack Underflow");
        return arr[len-1];
    }

    public boolean contains(Item item){
        return indexOf(item) != -1;
    }

    @Override
    public Iterator<Item> iterator(){
        return new Iterator<Item>() {

            int index = 0;

            @Override
            public boolean hasNext() {
                return index < len;
            }

            @Override
            public Item next() {
                return arr[index++];
            }
        };
    }

    public String toString(){
        if (len == 0) return "[]";
        else {
            StringBuilder sb = new StringBuilder(len).append("[");
            for (int i = 0; i < len-1; i++) {
                sb.append(arr[i] + ", ");
            }
            return sb.append(arr[len-1] + "]").toString();
        }
    }

    public static void main(String[] args) {
        ResizingArray<Integer> arr = new ResizingArray<>();

        arr.add(1);
        arr.add(2);
        arr.add(3);
        arr.add(4);
        arr.add(5);

        System.out.println(arr);
        System.out.println(arr.capacity);

        arr.pop();
        System.out.println(arr);

        arr.add(5);
        System.out.println(arr);

    }
}
