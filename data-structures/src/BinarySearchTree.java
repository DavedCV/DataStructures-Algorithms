public class BinarySearchTree <T extends Comparable<T>> {

    // internal class to represent the node unit, that stores the value and links to other nodes
    private class Node {
        private T value;
        private Node left, right;

        // instance variable to keep track of the # of nodes
        // in the subtree rooted in the node itself
        private int count;

        public Node(T value, int count) {
            this.value = value;
            this.count = count;
        }
    }

    // root node of the binary search tree
    private Node root;

    public boolean isEmpty() {
        return size() == 0;
    }
    public int size() {
        return size(root);
    }

    private int size(Node x) {
        if (x == null) return 0;
        return root.count;
    }

    // Add an element to this BST, returns true if the insertion is successful
    public boolean add(T value){

        int oldRootCount = root.count;

        // call the helper recursive method
        root = add(value, root);

        int newRootCount = root.count;

        // check if the number of nodes is changed after the call to the helper
        return oldRootCount != newRootCount;
    }

    private Node add(T value, Node x) {

        // add new node, with count 1 representing the same node in the subtree
        if (x == null) return new Node(value, 1);

        int cmp = value.compareTo(x.value);
        if (cmp < 0) x.left =  add(value, x.left);
        else if (cmp > 0) x.right = add(value, x.right);

        x.count = 1 + size(x.left) + size(x.right);
        return x;
    }

}
