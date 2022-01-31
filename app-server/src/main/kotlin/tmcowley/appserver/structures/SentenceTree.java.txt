package tmcowley.appserver.structures;

import java.util.ArrayList;
import java.util.Collections;

public class SentenceTree {
    static class Node {

        String value;
        Node parent;
        ArrayList<Node> children;

        /**
         * construct a node
         * 
         * @param value value stored in node
         */
        Node(String value) {
            this.value = value;
            this.children = new ArrayList<Node>();
            this.parent = null;
        }

        /**
         * check if this node is a leaf (has no children)
         * 
         * @return node leaf state
         */
        public boolean isLeaf() {
            return (this.children.isEmpty());
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

            // cast to String
            String otherSentence = (String) other;

            return (this.value.equals(otherSentence));
        }
    }

    // root node
    Node root = new Node(new String("*"));

    public void insert(ArrayList<String> words) {

        ArrayList<Node> leaves = getLeaves();

        for (Node leaf : leaves) {

            // System.out.println("\nleaf detected");

            for (String word : words) {
                // System.out.println("adding: " + word);
                Node childNode = new Node(word);
                childNode.parent = leaf;
                leaf.children.add(childNode);
            }
        }

        // System.out.println("added each word to each leaf");
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
            for (Node child : node.children) {
                traverse(child);
            }
        }
    }

    ArrayList<String> sentences;

    public ArrayList<String> getWords() {

        sentences = new ArrayList<String>();

        printPath(root);

        return sentences;
    }

    public void printPath(Node node) {

        if (node == null) {
            return;
        }

        // found leaf: traverse up to root
        if (node.isLeaf()) {

            // stores each word in the sentence (initially order reversed)
            ArrayList<String> wordArray = new ArrayList<String>();

            do {
                // add word (node) to word array
                String thisWord = node.value;
                wordArray.add(thisWord);
            } while ((node = node.parent) != root && node != null);

            // reverse word order since we've traversed up the tree (leaf -> root)
            Collections.reverse(wordArray);

            // form sentence String from array of words
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (String word : wordArray) {
                if (first) {
                    sb.append(word);
                    first = false;
                    continue;
                }
                sb.append(" ");
                sb.append(word);
            }
            String sentence = sb.toString();

            // ensure sentence is unique (in results)
            if (sentences.contains(sentence)) {
                return;
            }

            // TODO: assess sentence validity w/ ML

            // add unique sentence to resulting sentence list
            sentences.add(sentence);
            return;
        }
        for (Node child : node.children) {
            printPath(child);
        }
    }
}
