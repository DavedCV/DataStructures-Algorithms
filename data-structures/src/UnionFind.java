public class UnionFind {

    // id[i] points to the parent of i, if id[i] == i then it's a root
    private int[] id;

    // used to keep track of the size of every tree with root in size[i]
    private int[] size;

    // keeps tracks of the number of components in the data structure
    private int componentsNumber;

    // initialize the union find with every root pointing to itself
    // and with size of each root as 1
    public UnionFind (int N) {

        if (n <= 0) throw new IllegalArgumentException("size <= 0 is not allowed");

        id = new int[N];
        size = new int[N];
        componentsNumber = N;

        for (int i = 0; i < N; i++) {
            id[i] = i;
            size[i] = 1;
        }
    }

    // chase parent pointer until reach root
    // after the root is reached, use path compression
    private int root (int index) {

        // when the loop finish, root will be the root
        int root = index;
        while (root != id[root]) {
            root = id[root];
        }

        // do the same loop, but update every node parent to the root itself
        while (index != id[index]) {
            int oldParentIndex = id[index];
            id[index] = root;
            index = id[oldParentIndex];
        }

        return root;
    }

    // return the size of the component that element represented at "index" belongs to
    public int componentSize(int index) {
        return size[root(index)];
    }

    public int components() {
        return componentsNumber;
    }

    // check if two elements are connected, share the same root
    public boolean connected (int index1, int index2) {
        return root(index1) == root(index2);
    }

    // change root of one segment to point to the root of the other
    // always append the group with less size to the group with greater size
    public void union (int index1, int index2) {
        int root1 = root(index1);
        int root2 = root(index2);

        if (root1 == root2) return;

        if (size[root1] > size[root2]) {
            id[root2] = root1;
            size[root1] += size[root2];
        }else {
            id[root1] = root2;
            size[root2] += size[root1];
        }

        componentsNumber--;
    }
}
