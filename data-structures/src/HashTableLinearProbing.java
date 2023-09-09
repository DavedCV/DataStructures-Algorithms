/**
 * An implementation of a hash-table using open addressing with linear probing as a collision
 * resolution method.
 */
public class HashTableLinearProbing<Key, Value> extends HashTableOpenAddressingBase<Key, Value>{

    // This is the linear constant used in the linear probing, it can be
    // any positive number. The table capacity will be adjusted so that
    // the GCD(capacity, LINEAR_CONSTANT) = 1 so that all buckets can be probed.
    private static final int LINEAR_CONSTANT = 1;

    public HashTableLinearProbing() {
        super();
    }

    public HashTableLinearProbing(int capacity) {
        super(capacity);
    }

    public HashTableLinearProbing(int capacity, double loadFactor) {
        super(capacity, loadFactor);
    }

    // Finds the greatest common denominator of a and b.
    protected static int gcd(int a, int b) {
        if (b == 0) return a;
        return gcd(b, a % b);
    }

    // with linear probing is not necessary to do any setup
    @Override
    protected void setupProbing(Key key) {}

    @Override
    protected int probe(int x) {
        return x * LINEAR_CONSTANT;
    }

    // Adjust the capacity so that the linear constant and
    // the table capacity are relatively prime.
    // This with the objective of avoid short cycles with the probing function
    @Override
    protected void adjustCapacity() {
        while (gcd(LINEAR_CONSTANT, capacity) != 1)
            capacity++;
    }

}
