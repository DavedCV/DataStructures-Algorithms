import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicStampedReference;

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
    // used for the iterator
    protected int modificationCount;
    // used to count the total number of used bucket inside the hash table
    // including cells marked as deleted
    protected int usedBuckets;
    // used to track the number of unique keys inside the hashtable
    protected int keyCount;

    // Arrays to keep the key and value pairs
    protected Key[] keys;
    protected Value[] values;

    // Special marker token used to indicate the deletion of a key-value pair
    // in the hashtable
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

        // ??
        capacity = (2 * capacity) + 1;
    }

    // double the size of the hash-table
    protected void resizeTable() {
        increaseCapacity();
        increaseCapacity();

        threshold = (int) (capacity * loadFactor);

        Key[] oldKeyTable = keys;
        keys = (Key[]) new Object[capacity];

        Value[] oldValueTable = values;
        values = (Value[]) new Object[capacity];

        // Reset the key count and buckets used since we are about to
        // re-insert all the keys into the hash-table.
        keyCount = usedBuckets = 0;

        for (int i = 0; i < oldKeyTable.length; i++) {
            if (oldKeyTable[i] != null && oldKeyTable[i] != TOMBSTONE) {
                insert(oldKeyTable[i], oldValueTable[i]);
            }

            oldValueTable[i] = null;
            oldKeyTable[i] = null;
        }
    }

    // clear the hashtable information
    public void clear() {
        for (int i = 0; i < capacity; i++) {
            keys[i] = null;
            values[i] = null;
        }

        keyCount = usedBuckets = 0;
        modificationCount++;
    }

    // returns the number of keys currently inside the hashtable
    public int size() {
        return keyCount;
    }

    // return the capacity of the hashtable
    public int getCapacity() {
        return capacity;
    }

    // return true/false depending on whether the hastable is empty
    public boolean isEmpty() {
        return keyCount == 0;
    }

    // returns a list of keys found in the hash table
    public List<Key> keys() {
        List<Key> hashtableKeys = new ArrayList<>();

        for (int i = 0; i < capacity; i++) {
            if (keys[i] != null && keys[i] != TOMBSTONE) hashtableKeys.add(keys[i]);
        }

        return hashtableKeys;
    }

    // returns a list of non-unique values found in the hashtable
    public List<Value> values() {
        List<Value> hashtableValues = new ArrayList<>();

        for (int i = 0; i < capacity; i++) {
            if (keys[i] != null && keys[i] != TOMBSTONE) hashtableValues.add(values[i]);
        }

        return hashtableValues;
    }

    // returns true/false on whether a given key exists within the hashtable
    private boolean hasKey(Key key) {
        if (key == null) throw new IllegalArgumentException("Null key");

        // ??
        setupProbing(key);
        final int offset = normalizeIndex(key.hashCode());

        // Start at the original hash value and probe until we find a spot where our key
        //is hit or a null element (in which case our key does nor exists)
        for (int i = offset, j = -1, x = 1 ;; i = normalizeIndex(offset + probe(x++))) {

            // Ignore deleted cells, but record the first index
            // where a deleted element cell is found to perform
            // lazy relocation later
            if (keys[i] == TOMBSTONE) {
                if (j == -1) j = i;

            // we hit a non-null key, perhaps it's the one we're looking for
            } else if (keys[i] != null) {
                if (keys[i].equals(key)) {

                    // If j != -1 this means we previously encountered a deleted cell.
                    // We can perform an optimization by swapping the entries in cells
                    // i and j so that the next time we search for this key it will be
                    // found faster. This is called lazy deletion/relocation.
                    if (j != -1) {
                        // swap the key-value pairs of positions i and j
                        keys[j] = keys[i];
                        values[j] = values[i];
                        keys[i] = TOMBSTONE;
                        values[i] = null;
                    }

                    return true;

                // else key was not found in the hashtable
                } else return false;
            }

        }

    }

    // converts a hash value to an index. Essentially, this strips the
    // negative sign and places the hash value in the domain [0, capacity)
    private int normalizeIndex(int keyHash) {
        return (keyHash & 0x7FFFFFFF) % capacity;
    }

    // PLace a key-value pair into the hash-table. If the value already exists
    // then the value is updated.
    public Value insert(Key key, Value value) {
        if (key == null) throw new IllegalArgumentException("Null key");
        if (usedBuckets >= threshold) resizeTable();

        // ??
        setupProbing(key);
        final int offset = normalizeIndex(key.hashCode());

        for (int i = offset, j = -1, x = 1 ;; i = normalizeIndex(offset + probe(x++))) {

            // the current slot was previously deleted
            if (keys[i] == TOMBSTONE) {
                if (j == -1) j = i;
            }

            // the current cell already contains a key
            else if (keys[i] != null) {

                // The key we're trying to insert already exists in the hash-table,
                // so update its value with the most recent value
                if (keys[i].equals(key)) {
                    Value oldValue = values[i];

                    if (j == -1) {
                        values[i] = value;
                    } else {
                        keys[j] = TOMBSTONE;
                        values[j] = null;
                        keys[i] = key;
                        values[i] = value;
                    }

                    modificationCount++;
                    return oldValue;
                }

                // current cell is null so an insertion/update can occur
                else {

                    // no previously encountered deleted buckets
                    if (j == -1) {
                        usedBuckets++;
                        keyCount++;
                        keys[i] = key;
                        values[i] = value;
                    }

                    // Previously seen deleted bucket. Instead of inserting
                    // the new element at i where the null element is insert
                    // it where the deleted token was found.
                    else {
                        keyCount++;
                        keys[j] = key;
                        values[j] = value;
                    }

                    modificationCount++;
                    return null;
                }
            }

        }

    }
}
