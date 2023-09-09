/**
 * An implementation of a hash-table using open addressing with quadratic probing as a collision
 * resolution method.
 *
 * In this implementation we are using the following probing function: H(k, x) = h(k) + f(x) mod
 * 2^n
 *
 * Where h(k) is the hash for the given key, f(x) = (x + x^2) / 2 and n is a natural number. We
 * are using this probing function because it is guaranteed to find an empty cell (i.e it generates
 * all the numbers in the range [0, 2^n) without repetition for the first 2^n numbers).
 *
 */

public class HashTableQuadraticProbing<Key, Value> extends HashTableOpenAddressingBase<Key, Value> {

    public HashTableQuadraticProbing() {
        super();
    }

    public HashTableQuadraticProbing(int capacity) {
        super(capacity);
    }

    public HashTableQuadraticProbing(int capacity, double loadFactor) {
        super(capacity, loadFactor);
    }

    // Given a number this method finds the next
    // power of two above this value.
    private static int nextPowerOfTwo(int n) {
        // left shift bitwise manipulation by 1 position
        return Integer.highestOneBit(n) << 1;
    }

    // no setup required for quadratic probing
    @Override
    protected void setupProbing(Key key) {}

    @Override
    protected int probe(int x) {
        // Quadratic probing function (x^2+x)/2
        // bitwise manipulation right shift one position to divide by 2
        return (x * x + x) >> 1;
    }

    // Adjust the capacity of the hashtable to be a power of two.
    @Override
    protected void adjustCapacity() {
        int pow2 = Integer.highestOneBit(capacity);
        if (capacity == pow2) return;
        increaseCapacity();
    }

    // Increase the capacity of the hashtable to the next power of two.
    @Override
    protected void increaseCapacity() {
        capacity = nextPowerOfTwo(capacity);
    }

}
