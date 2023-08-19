import javax.imageio.ImageTranscoder;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public class BinarySearchTree <T extends Comparable<T>> {

    private enum TreeTraversalOrder {
        IN_ORDER,
        POST_ORDER,
        PRE_ORDER,
        LEVEL_ORDER
    }

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
        return x.count;
    }

    // Add an element to this BST, returns true if the insertion is successful
    public boolean add(T value){

        int oldRootCount = (root == null) ? 0 : root.count;

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

        // update the node count of the tree rooted at node x
        x.count = 1 + size(x.left) + size(x.right);
        return x;
    }

    public void deleteMin() {
        root = deleteMin(root);
    }
    // recursively go left until a null link is reached
    // when reached, replace that link with the right tree of the node
    // update links and node counts after recursive calls
    private Node deleteMin(Node x){
        if (x.left == null) return x.right;

        x.left = deleteMin(x.left);
        x.count = 1 + size(x.left) + size(x.right);

        return x;
    }

    public boolean delete(T value) {
        int oldNodeCount = (root == null) ? 0 : root.count;

        root = delete(root, value);

        int newNodeCount = (root == null) ? 0 : root.count;

        return oldNodeCount-1 == newNodeCount;
    }
    private Node delete(Node x, T value){
        if (x == null) return null;

        // compare the value of the actual node to the target value
        int cmp = value.compareTo(x.value);

        // recurse the tree in the right direction
        if (cmp < 0) x.left = delete(x.left, value);
        if (cmp > 0) x.right = delete(x.right, value);
        // handle the deletion when the target node is reached
        else{
            if (x.left == null) return x.right;
            if (x.right == null) return x.left;

            Node rightTreeMin = getMin(x.right);
            x.value = rightTreeMin.value;
            x.count = rightTreeMin.count;
            x.right = deleteMin(x.right);
        }

        x.count = 1 + size(x.left) + size(x.right);
        return x;
    }

    public boolean contains(T value){
        return contains(value, root);
    }
    private boolean contains(T value, Node x){
        if (x == null) return false;

        int cmp = value.compareTo(x.value);
        if (cmp < 0) return contains(value, x.left);
        else if (cmp > 0) return contains(value, x.right);
        else return true;
    }

    // --------------------------------------------- traverse iterators ------------------------------------------------

    public Iterator<T> traverse(TreeTraversalOrder order) {
        return switch (order) {
            case IN_ORDER -> inOrderTraversal();
            case PRE_ORDER -> preOrderTraversal();
            case POST_ORDER -> postOrderTraversal2();
            case LEVEL_ORDER -> levelOrderTraversal();
            default -> null;
        };
    }

    private Iterator<T> preOrderTraversal() {
        final Deque<Node> stack = new ArrayDeque<>();
        stack.addFirst(root);

        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return root != null && !stack.isEmpty();
            }

            @Override
            public T next() {
                Node node = stack.removeFirst();
                if (node.right != null) stack.addFirst(node.right);
                if (node.left != null) stack.addFirst(node.left);
                return node.value;
            }
        };
    }

    private Iterator<T> postOrderTraversal() {
        final Deque<Node> stack1 = new ArrayDeque<>();
        final Deque<Node> stack2 = new ArrayDeque<>();

        stack1.addFirst(root);
        while (!stack1.isEmpty()) {
            Node node = stack1.removeFirst();

            stack2.addFirst(node);

            if (node.left != null) stack1.addFirst(node.left);
            if (node.right != null) stack1.addFirst(node.right);
        }

        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return root != null && !stack2.isEmpty();
            }

            @Override
            public T next() {
                return stack2.removeFirst().value;
            }
        };
    }

    private Iterator<T> postOrderTraversal2() {

        final Deque<Node> stack1 = new ArrayDeque<>();
        final Deque<Node> stack2 = new ArrayDeque<>();

        stack1.addFirst(root);

        while(stack1.size() != size(root)){
            Node node = stack1.peekFirst();

            if (node != null && node.left != null) stack2.addFirst(node.left);

            if (node != null && node.right != null) stack1.addFirst(node.right);
            else stack1.addFirst(stack2.removeFirst());
        }

        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return root != null && !stack1.isEmpty();
            }

            @Override
            public T next() {
                return stack1.removeFirst().value;
            }
        };
    }

    private Iterator<T> inOrderTraversal() {
        final Deque<Node> stack = new ArrayDeque<>();
        stack.addFirst(root);

        return new Iterator<T>() {
            Node trav = root;

            @Override
            public boolean hasNext() {
                return root != null && !stack.isEmpty();
            }

            @Override
            public T next() {

                while (trav != null && trav.left != null){
                    stack.addFirst(trav.left);
                    trav = trav.left;
                }

                Node node = stack.removeFirst();

                if (node.right != null) {
                    stack.addFirst(node.right);
                    trav = node.right;
                }

                return node.value;
            }
        };
    }

    private Iterator<T> levelOrderTraversal() {
        final Deque<Node> queue = new ArrayDeque<>();
        queue.addLast(root);

        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return root != null && !queue.isEmpty();
            }

            @Override
            public T next() {
                Node node = queue.removeFirst();

                if (node.left != null) queue.addLast(node.left);
                if (node.right != null) queue.addLast(node.right);

                return node.value;
            }
        };
    }

    // ------------------------------------- helper methods and in-order ops -------------------------------------------

    // get the min node from a root node x
    public T getMin(){
        return getMin(root).value;
    }
    private Node getMin(Node x) {
        while (x.left != null){
            x = x.left;
        }

        return x;
    }

    // get the max node from a root node x
    public T getMax(){
        return getMax(root).value;
    }
    private Node getMax(Node x) {
        while (x.right != null) {
            x = x.right;
        }

        return x;
    }

    // select the k smallest value of the tree
    public T select(int order) {
        if (order < 0 || order >= size()) throw new IllegalArgumentException("Argument is out of range");

        Node node = select(order, root);
        return node.value;
    }
    private Node select(int order, Node x) {

        // get the count of nodes in the subtree rooted at x.left
        int leftSize = size(x.left);

        // if the order is less than the left size, then recurse in the left subtree
        // cause the k-smallest is in that side
        if (order < leftSize) return select(order, x.left);
        // if the order is greater than the left size, then recurse in the right subtree
        // and update the order to not take into account the size of the pruned part of tree
        if (order > leftSize) return select(order - leftSize - 1, x.right);

        // if the order is equal to the size of the left size, then the current node is the target
        return x;
    }

    // find the number of nodes that are less than the given value
    public int rank (T value) {
        return rank(value, root);
    }
    private int rank (T value, Node x){
        // if node is null, the rank is 0
        if (x == null) return 0;


        int cmp = value.compareTo(x.value);

        // if the key is less than the node key, find the rank from the left tree
        if (cmp < 0) return rank(value, x.left);

        // if the key is greater than the node key, take into account the
        // longitude of the left part of the tree and find based on the
        // right subtree
        if (cmp > 0) return 1 + size(x.left) + rank(value, x.right);

        // if the key is equal to the node key, return the size of the left subtree
        // the count of the elements that are less
        return size(x.left);
    }

    // find the greatest value, less than the given value
    public T floor(T value){
        // get the node that represents the floor
        Node floor = floor(value, root);
        if (floor == null) return null;
        else return floor.value;
    }
    private Node floor(T value, Node x) {
        if (x == null) return null;

        int cmp = value.compareTo(x.value);

        // if the node value is equal to the target value, then we are done
        if (cmp == 0) return x;

        // if the value is less than the node value, then recurse in the left subtree
        if (cmp < 0) return floor(value, x.left);

        // if the value is greater than the node value, then check if there are any
        // node less or equal to the target in the right subtree
        else {
            Node t = floor(value, x.right);
            if (t == null) return x;
            else return t;
        }
    }

    // find the smallest value, greater than the given value
    public T ceil(T value){
        // get the node that represents the floor
        Node ceil = ceil(value, root);
        if (ceil == null) return null;
        else return ceil.value;
    }
    private Node ceil(T value, Node x) {
        if (x == null) return null;

        int cmp = value.compareTo(x.value);

        // if the node value is equal to the target value, then we are done
        if (cmp == 0) return x;

        // if the value is greater than the node value, then recurse in the left subtree
        // the ceil must be in the right subtree
        if (cmp > 0) return ceil(value, x.right);

        // if the value is lesser than the node value, then check if there are any
        // node greater or equal to the target in the left subtree
        else {
            Node t = ceil(value, x.left);
            if (t == null) return x;
            else return t;
        }
    }

    public static void main(String[] args) {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();

        tree.add(4);
        tree.add(2);
        tree.add(6);
        tree.add(1);
        tree.add(5);
        tree.add(7);

        /*
             4
           /   \
          2     6
         /     / \
        1     5   7

        */

        System.out.println("Tree size: " + tree.size());
        System.out.println("Min: " + tree.getMin());
        System.out.println("Max: " + tree.getMax());
        System.out.println("Contains 6: " + tree.contains(6));
        System.out.println("0-smallest value: " + tree.select(0));
        System.out.println("2-smallest value: " + tree.select(2));
        System.out.println("Rank 8: " + tree.rank(8));
        System.out.println("Rank 3: " + tree.rank(3));
        System.out.println("Floor 8: " + tree.floor(8));
        System.out.println("Floor 3: " + tree.floor(3));
        System.out.println("Ceil -4: " + tree.ceil(-4));
        System.out.println("Ceil 3: " + tree.ceil(3));

        tree.deleteMin();
        System.out.println("Delete min, new min: " + tree.getMin());

        Iterator<Integer> inOrder = tree.traverse(TreeTraversalOrder.IN_ORDER);
        Iterator<Integer> postOrder = tree.traverse(TreeTraversalOrder.POST_ORDER);
        Iterator<Integer> levelOrder = tree.traverse(TreeTraversalOrder.LEVEL_ORDER);

        StringBuffer inOrderRep = new StringBuffer().append("[");
        StringBuffer postOrderRep = new StringBuffer().append("[");
        StringBuffer levelOrderRep = new StringBuffer().append("[");

        while (inOrder.hasNext()) {
            inOrderRep.append(inOrder.next());
            postOrderRep.append(postOrder.next());
            levelOrderRep.append(levelOrder.next());

            if (inOrder.hasNext()) {
                inOrderRep.append(", ");
                postOrderRep.append(", ");
                levelOrderRep.append(", ");
            }
        }

        inOrderRep.append("]");
        postOrderRep.append("]");
        levelOrderRep.append("]");

        System.out.println("in-order traverse: " +inOrderRep.toString());
        System.out.println("post-order traverse: " + postOrderRep.toString());
        System.out.println("level-order traverse: " +levelOrderRep.toString());
    }
}
