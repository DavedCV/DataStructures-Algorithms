import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

class Entry<Key, Value> {

    Key key;
    Value value;
    int hash;

    public Entry(Key key, Value value) {
        this.key = key;
        this.value = value;

        // use builtin method to compute the hashcode, or use overrided hashcode
        this.hash = Key.hashCode();
    }

    public boolean equals(Entry<Key, Value> other) {
        if (hash != other.hash) return false;
        return key.equals(other.key);
    }

    public String toString() {
        return key + " => " + value;
    }

}
public class HashTableSC<Key, Value> implements Iterable<Key> {

    private static final int DEFAULT_CAPACITY = 3;

    // used to keep track of the table size
    private static final double DEFAULT_LOAD_FACTOR = .75;

    // used to keep track of load factor, in order to manage table resizing
    private double maxLoadFactor;
    // how big is the table
    private int capacity = 0;
    // capacity * max load factor
    private int threshold = 0;
    // actual number of entries in the table
    private int size = 0;
    // table
    private LinkedList<Entry<Key, Value>>[] table;

    public HashTableSC() {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public HashTableSC(int capacity) {
        this(capacity, DEFAULT_LOAD_FACTOR);
    }

    public HashTableSC(int capacity, double maxLoadFactor) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal Capacity");
        if (maxLoadFactor <= 0 || Double.isNaN(maxLoadFactor) || Double.isInfinite(maxLoadFactor))
            throw new IllegalArgumentException("Illegal load factor");

        this.maxLoadFactor = maxLoadFactor;
        this.capacity = capacity;
        this.threshold = (int) (this.maxLoadFactor * this.capacity);
        table = new LinkedList[this.capacity];
    }

    // return the number of elements in the hash table
    public int size() {
        return size;
    }

    // return if the hash table is empty
    public boolean empty() {
        return size == 0;
    }

    // convert the hash value to an index of the table.
    // strips the negative sign and places the hash value in the domain [0, capacity)
    private int normalizeIndex(int keyHash) {
        return (keyHash & 0x7FFFFFFF) % capacity;
    }

    // clears al the contents of the hash table
    public void clear() {
        Arrays.fill(table, null);
        size = 0;
    }

    // returns true depending on whether a key is in the hash table
    public boolean hasKey(Key key) {
        int tableBucketIndex = normalizeIndex(key.hashCode());
        return seekBucketEntry(tableBucketIndex, key) != null;
    }

    // finds and returns a particular entry in a given bucket if it exists, otherwise returns null
    private Entry<Key, Value> seekBucketEntry(int tableBucketIndex, Key key) {

        LinkedList<Entry<Key, Value>> tableBucket = table[tableBucketIndex];
        if (tableBucket == null) return null;

        for (Entry<Key, Value> entry : tableBucket)
            if (entry.key.equals(key)) return entry;

        return null;
    }

    public Value add(Key key, Value value) {
        if (key == null) throw new IllegalArgumentException("Null Key");

        Entry<Key, Value> newEntry = new Entry<>(key, value);
        int bucketIndex = normalizeIndex(newEntry.hash);

        return bucketInsertEntry(bucketIndex, newEntry);
    }

    // add a new entry to a bucket in the table if the entry does not already exists
    // in the given bucket, but if it does, update value
    // return null or previous entry value if the data is updated
    private Value bucketInsertEntry(int bucketIndex, Entry<Key, Value> newEntry) {
        LinkedList<Entry<Key, Value>> tableBucket = table[bucketIndex];
        if (tableBucket == null)
            table[bucketIndex] = tableBucket = new LinkedList<>();

        Entry<Key, Value> existentEntry = seekBucketEntry(bucketIndex, newEntry.key);

        if (existentEntry == null) {
            tableBucket.add(newEntry);
            if (++size > threshold) resizeTable();
            return null;
        } else {
            Value oldValue = existentEntry.value;
            existentEntry.value = newEntry.value;
            return oldValue;
        }

    }

    @Override
    public Iterator<Key> iterator() {
        return null;
    }
}
