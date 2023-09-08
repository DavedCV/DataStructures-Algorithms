/*
*   Base class for hashtables with an open addressing collision resolution method
*   such as linear probing, quadratic probing and double hashing.
* */
public abstract class HashTableOpenAddressingBase<Key, Value> implements Iterable<Key> {

    // used to set the maximum load factor of the hashtable
    protected double loadFactor;
    // used to register the total size of the hashtable
    protected int capacity;
    // used to maintain the capacity threshold of the hashtable
    protected int threshold;
    // used to count the total number of used bucket inside the hash table
    // including cells marked as deleted
    protected int usedBuckets;
    // used to track the number of unique keys inside the hashtable
    protected int keyCount;

    // Arrays to keep the key and value pairs
    protected Key[] keys;
    protected Value[] values;

    // Special marker token used to indicate the deletion of a key-value pair
    protected final Key TOMBSTONE = (Key) (new Object());

    // default hashtable parameters
    private static final int DEFAULT_CAPACITY = 7;
    private static final double DEFAULT_LOAD_FACTOR = 0.65;

    // constructors
    protected HashTableOpenAddressingBase() {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    protected HashTableOpenAddressingBase(int capacity) {
        this(capacity, DEFAULT_LOAD_FACTOR);
    }

    protected HashTableOpenAddressingBase(int capacity, double loadFactor) {
        if (capacity <= 0) throw new IllegalArgumentException("Illegal capacity: " + capacity);

        if (loadFactor <= 0 || Double.isNaN(loadFactor) || Double.isInfinite(loadFactor))
            throw new IllegalArgumentException("Illegal loadFactor: " + loadFactor);

        this.loadFactor = loadFactor;
        this.capacity = Math.max(DEFAULT_CAPACITY, capacity);
        adjustCapacity();
        threshold = (int) (capacity * loadFactor);

        keys = (Key[]) new Object[this.capacity];
        values = (Value[]) new Object[this.capacity];
    }

    // the following methods dictate how the probing is to actually
    // occur for whatever open addressing scheme is implemented
    protected abstract void setupProbing(Key key);

    protected abstract int probe(int x);

    // Adjusts the capacity of the hash table after it's been made larger.
    // This is important to be able to override because the size of the hashtable
    // controls the functionality of the probing function.
    protected abstract void adjustCapacity();

    // increase the capacity of the hashtable
    protected void increaseCapacity() {
        capacity = (2 * capacity) + 1;
    }

    public void clear() {

    }

}
