import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

/*
*   Base class for hashtables with an open addressing collision resolution method
*   such as linear probing, quadratic probing and double hashing.
* */

@SuppressWarnings("unchecked")
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

    // helps with the definition of some important variables for the probing function (necessary for double hashing)
    protected abstract void setupProbing(Key key);

    // the actual method to do the probing

    protected abstract int probe(int x);

    // Adjusts the capacity of the hash table after it's been made larger.
    // This is important to be able to override because the size of the hashtable
    // controls the functionality of the probing function.
    protected abstract void adjustCapacity();

    // increase the capacity of the hashtable
    protected void increaseCapacity() {

        capacity = (2 * capacity);
    }

    // double the size of the hash-table
    protected void resizeTable() {
        increaseCapacity();
        adjustCapacity();

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

    // converts a hash value to an index. Essentially, this strips the
    // negative sign and places the hash value in the domain [0, capacity)
    protected int normalizeIndex(int keyHash) {
        return (keyHash & 0x7FFFFFFF) % capacity;
    }

    // Finds the greatest common denominator of a and b.
    protected static int gcd(int a, int b) {
        if (b == 0) return a;
        return gcd(b, a % b);
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
                        keys[i] = TOMBSTONE;
                        values[i] = null;
                        keys[j] = key;
                        values[j] = value;
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
                    // the new element at i where the null element is, insert
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

    // Get the value associated with the input key
    // returns null if the value is null and also returns
    // null if the key does not exists
    public Value get(Key key) {
        if (key == null) throw new IllegalArgumentException("Null key");

        // ??
        setupProbing(key);
        final int offset = normalizeIndex(key.hashCode());

        // Start at the original hash value and probe until we find a spot where our key
        // is or we hit a null element in which case our element does not exist.
        for (int i = offset, j = -1, x = 1 ;; i = normalizeIndex(offset + probe(x++))) {

            // Ignore deleted cells, but record where the first index
            // of a deleted cell is found to perform lazy relocation later.
            if (keys[i] == TOMBSTONE){
                if (j == -1) j = i;
            }
            else if (keys[i] == null) return null;
            // We hit a non-null key, perhaps it's the one we're looking for.
            else {

                // The key we want is in the hash-table!
                if (keys[i].equals(key)) {

                    // If j != -1 this means we previously encountered a deleted cell.
                    // We can perform an optimization by swapping the entries in cells
                    // i and j so that the next time we search for this key it will be
                    // found faster. This is called lazy deletion/relocation.
                    if (j != -1) {
                        // Swap key-values pairs at indexes i and j.
                        keys[j] = keys[i];
                        values[j] = values[i];
                        keys[i] = TOMBSTONE;
                        values[i] = null;

                        return values[j];
                    }
                    else return values[i];
                }
            }
        }
    }

    // Removes a key from the map and returns the value.
    // NOTE: returns null if the value is null AND also returns
    // null if the key does not exists.
    public Value remove(Key key) {
        if (key == null) throw new IllegalArgumentException("Null key");

        // ??
        setupProbing(key);
        final int offset = normalizeIndex(key.hashCode());

        // Starting at the original hash probe until we find a spot where our key is
        // or we hit a null element in which case our element does not exist.
        for (int i = offset, x = 1 ;; i = normalizeIndex(offset + probe(x++))) {

            // Ignore deleted cells, but record where the first index
            // of a deleted cell is found to perform lazy relocation later.
            if (keys[i] == TOMBSTONE) continue;

            // Key was not found in hash-table.
            if (keys[i] == null) return null;

            // The key we want to remove is in the hash-table!
            if (keys[i].equals(key)) {
                keyCount--;
                modificationCount++;
                Value oldValue = values[i];
                keys[i] = TOMBSTONE;
                values[i] = null;
                return oldValue;
            }
        }
    }

    // return a string view of this hashtable
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("{");
        for (int i = 0; i < capacity; i++)
            if (keys[i] != null && keys[i] != TOMBSTONE) sb.append(keys[i] + " => " + values[i] + ", ");

        if (sb.length() > 2){
            // Remove the trailing comma and space
            sb.setLength(sb.length() - 2);
        }
        sb.append("}");

        return sb.toString();
    }

    @Override
    public Iterator<Key> iterator() {
        // Before the iteration begins record the number of modifications
        // done to the hash-table. This value should not change as we iterate
        // otherwise a concurrent modification has occurred :0
        final int MODIFICATION_COUNT = modificationCount;

        return new Iterator<>() {

            int index, keysLeft = keyCount;

            @Override
            public boolean hasNext() {
                // The contents of the table have been altered
                if (MODIFICATION_COUNT != modificationCount) throw new ConcurrentModificationException();
                return keysLeft != 0;
            }

            @Override
            public Key next() {
                while (keys[index] == null || keys[index] == TOMBSTONE) index++;
                keysLeft--;
                return keys[index++];
            }
        };
    }
}
