package tmcowley.appserver.structures;

import java.util.ArrayList;

import keyboard.App;
import keyboard.Key;
import keyboard.KeyPair;

public class WordTree {
    static class Node {

        Key value;
        Node parent;
        Node left, right;

        /**
         * construct a node
         * 
         * @param value Key stored in node
         */
        Node(Key value) {
            this.value = value;
            this.left = null;
            this.right = null;
            this.parent = null;
        }

        /**
         * check if this node is a leaf (has no children)
         * 
         * @return node leaf state
         */
        public boolean isLeaf() {
            return (this.left == null && this.right == null);
        }

        @Override
        public boolean equals(Object other) {

            // ensure other key is not null
            if (other == null) {
                return false;
            }

            // ensure 'other' Object is a Node
            if (!(other instanceof Node)) {
                return false;
            }

            // cast other Object to Node
            Node otherNode = (Node) other;

            Key otherKey = otherNode.value;

            return (this.value.equals(otherKey));
        }
    }

    Node root = new Node(new Key('*'));

    public void insert(KeyPair keyPair) {
        Key key1 = keyPair.key1;
        Key key2 = keyPair.key2;

        ArrayList<Node> leaves = getLeaves();

        for (Node leaf : leaves) {

            Node leftChild = new Node(key1);
            Node rightChild = new Node(key2);

            leaf.left = leftChild;
            leaf.right = rightChild;

            leftChild.parent = leaf;
            rightChild.parent = leaf;
        }
    }

    ArrayList<Node> leaves;

    public ArrayList<Node> getLeaves() {
        leaves = new ArrayList<Node>();

        traverse(root);

        // for (Node leaf : leaves) {
        // System.out.println("leaf found: " + leaf.value.character);
        // }

        return leaves;
    }

    public void traverse(Node node) {
        if (node != null) {
            if (node.isLeaf()) {
                leaves.add(node);
                return;
            }
            traverse(node.left);
            traverse(node.right);
        }
    }

    public void traverseInOrder() {
        traverseInOrder(root);
    }

    public void traverseInOrder(Node node) {
        if (node != null) {
            traverseInOrder(node.left);
            System.out.print(" " + node.value.character);
            traverseInOrder(node.right);
        }
    }

    ArrayList<String> words;

    public ArrayList<String> getWords() {

        words = new ArrayList<String>();

        printPath(root);

        return words;
    }

    public void printPath(Node n) {
        if (n == null) {
            return;
        }
        if (n.isLeaf()) {
            String potentialWord = "";
            do {
                potentialWord += n.value;
                // System.out.print(n.value);
                // System.out.print(" ");
            } while ((n = n.parent) != root && n != null);
            // System.out.println("");

            potentialWord = new StringBuilder(potentialWord).reverse().toString();

            if (words.contains(potentialWord)) {
                return;
            }

            App instance = App.getInstance();

            if (!instance.wordExists(potentialWord)) {
                return;
            }

            words.add(potentialWord);

            return;
        }
        printPath(n.left);
        printPath(n.right);
    }
}
