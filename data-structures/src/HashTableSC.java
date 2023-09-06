import java.util.*;

class Entry<Key, Value> {

    Key key;
    Value value;
    int hash;

    public Entry(Key key, Value value) {
        this.key = key;
        this.value = value;

        // use builtin method to compute the hashcode, or use overrided hashcode
        this.hash = key.hashCode();
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
    private int capacity ;
    // capacity * max load factor
    private int threshold;
    // actual number of entries in the table
    private int size;
    // table
    private LinkedList<Entry<Key, Value>>[] table;

    public HashTableSC() {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public HashTableSC(int capacity) {
        this(capacity, DEFAULT_LOAD_FACTOR);
    }

    public HashTableSC(int capacity, double maxLoadFactor) {
        if (capacity < 0)
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
        int bucketIndex = normalizeIndex(key.hashCode());
        return seekBucketEntry(bucketIndex, key) != null;
    }

    // finds and returns a particular entry in a given bucket if it exists, otherwise returns null
    private Entry<Key, Value> seekBucketEntry(int bucketIndex, Key key) {

        LinkedList<Entry<Key, Value>> tableBucket = table[bucketIndex];
        if (tableBucket == null) return null;

        for (Entry<Key, Value> entry : tableBucket)
            if (entry.key.equals(key)) return entry;

        return null;
    }

    // ad a value to the hash table
    public Value add(Key key, Value value) {
        if (key == null) throw new IllegalArgumentException("Null Key");

        Entry<Key, Value> newEntry = new Entry<>(key, value);
        int bucketIndex = normalizeIndex(newEntry.hash);

        return bucketInsertEntry(bucketIndex, newEntry);
    }

    // add a new entry to a bucket in the table if the entry does not already exists
    // in the given bucket, but if it does, update value
    // return null if there aren´t previous value, or previous entry value if the data is updated
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

    // return the stored value of the key provided
    // returns null if the key doesn't exist, or null if the value is exactly null
    public Value get (Key key) {
        if (key == null) return null;

        int bucketIndex = normalizeIndex(key.hashCode());
        Entry<Key, Value> entry = seekBucketEntry(bucketIndex, key);

        if (entry != null) return entry.value;

        return null;
    }

    // remove a entry from the table given a key
    // return the value of the removed entry, or null if that entry not exists
    public Value remove(Key key) {
        if (key == null) return null;
        int bucketIndex = normalizeIndex(key.hashCode());
        return bucketRemoveEntry(bucketIndex, key);
    }

    private Value bucketRemoveEntry(int bucketIndex, Key key) {
        LinkedList<Entry<Key, Value>> bucket = table[bucketIndex];

        Entry<Key, Value> entry = seekBucketEntry(bucketIndex, key);

        if (entry == null) return null;

        bucket.remove(entry);
        size--;
        return entry.value;
    }

    // resizes the internal table holding buckets of entries
    private void resizeTable() {

        capacity *= 2;
        threshold = (int) (capacity * maxLoadFactor);

        LinkedList<Entry<Key, Value>>[] newTable = new LinkedList[capacity];

        for (LinkedList<Entry<Key, Value>> list : table) {
            if (list != null) {
                for (Entry<Key, Value> entry : list) {
                    int bucketIndex = normalizeIndex(entry.hash);
                    LinkedList<Entry<Key, Value>> bucket = newTable[bucketIndex];
                    if (bucket == null) newTable[bucketIndex] = bucket = new LinkedList<>();
                    bucket.add(entry);
                }

                list.clear();
            }
        }

        // avoid memory leak
        table = newTable;
    }

    // return all the keys of the hash table
    public List<Key> keys() {

        List<Key> keys = new ArrayList<>(size);
        for (LinkedList<Entry<Key, Value>> bucket : table)
            if (bucket != null)
                for (Entry<Key, Value> entry : bucket)
                    keys.add(entry.key);

        return keys;
    }

    // return all the values within the hash table
    public List<Value> values() {

        List<Value> values = new ArrayList<>(size);
        for (LinkedList<Entry<Key, Value>> bucket : table)
            if (bucket != null)
                for (Entry<Key, Value> entry : bucket)
                    values.add(entry.value);

        return values;
    }

    @Override
    public Iterator<Key> iterator() {

        final int elementCount = size();

        return new Iterator<>() {

            int bucketIndex = 0;
            Iterator<Entry<Key, Value>> bucketIterator = (table[bucketIndex] == null) ? null : table[bucketIndex].iterator();

            @Override
            public boolean hasNext() {

                // no iterator or current iterator is empty
                if (bucketIterator == null || !bucketIterator.hasNext()) {

                    // search next buckets until a valid iterator is found
                    while (++bucketIndex < capacity) {

                        if (table[bucketIndex] != null) {

                            Iterator<Entry<Key, Value>> nextIterator = table[bucketIndex].iterator();
                            // make sure the iterator actually has elements
                            if (nextIterator.hasNext()) {
                                bucketIterator = nextIterator;
                                break;
                            }
                        }
                    }
                }

                return bucketIndex < capacity;
            }

            @Override
            public Key next() {
                return bucketIterator.next().key;
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (int i = 0; i < capacity; i++) {
            if (table[i] == null) continue;
            for (Entry<Key, Value> entry : table[i]) sb.append(entry).append(", ");
        }
        sb.append("}");
        return sb.toString();
    }
}
