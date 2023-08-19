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

    private Node getMin(Node x) {
        while (x.left != null){
            x = x.left;
        }

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

        return new Iterator<T>() {
            Node trav = root;

            @Override
            public boolean hasNext() {
                return root != null && !stack.isEmpty();
            }

            @Override
            public T next() {

                while (trav != null){
                    stack.addFirst(trav);
                    trav = trav.left;
                }

                Node node = stack.removeFirst();

                if (node.right != null) trav = node.right;

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

    public static void main(String[] args) {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();

        tree.add(4);
        tree.add(2);
        tree.add(6);
        tree.add(1);
        tree.add(5);
        tree.add(7);

        Iterator<Integer> iterator = tree.traverse(TreeTraversalOrder.POST_ORDER);
        while(iterator.hasNext()){
            System.out.println(iterator.next());
        }

    }
}
