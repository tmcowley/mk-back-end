package tmcowley.appserver.structures;

import java.util.ArrayList;
import java.util.Collections;

class SentenceTree {
    class Node (
        val value: String
    ) {

        var parent: Node? = null;
        var children: MutableList<Node> = mutableListOf();

        /**
         * check if this node is a leaf (has no children)
         * 
         * @return node leaf state
         */
        fun isLeaf(): Boolean {
            return (this.children.isEmpty());
        }
    }

    // root node
    val root: Node = Node("*");

    // leaf storing list
    var leaves: MutableList<Node> = mutableListOf();

    var sentences: MutableList<String> = mutableListOf();


    fun insert(words: MutableList<String> ) {

        val leaves: MutableList<Node> = findLeaves();

        for (leaf: Node in leaves) {

            for (word: String in words) {
                // println("adding: " + word);
                val childNode: Node = Node(word);
                childNode.parent = leaf;
                leaf.children.add(childNode);
            }
        }

        // println("added each word to each leaf");
    }

    fun findLeaves(): MutableList<Node> {
        leaves = mutableListOf();

        traverse(this.root);

        // leaves.forEach{ leaf -> println("leaf found: ${leaf.value.character}") }

        return leaves;
    }

    fun traverse(node: Node?) {
        if (node == null) return

        // node is not null

        if (node.isLeaf()) {
            leaves.add(node);
            return;
        }
        for (child: Node in node.children) {
            traverse(child);
        }

    }

    fun findWords(): MutableList<String> {

        sentences = mutableListOf();

        printPath(this.root);

        return sentences;
    }

    fun printPath(node: Node?) {

        if (node == null) return;

        // found leaf: traverse up to root
        if (node.isLeaf()) {

            // stores each word in the sentence (initially order reversed)
            var wordArray: MutableList<String> = mutableListOf();

            var currentNode: Node = node;

            do {
                // add word (node) to word array
                val thisWord: String = currentNode.value;
                wordArray.add(thisWord);

                currentNode = currentNode.parent!!;

            } while (currentNode != root);

            // reverse word order since we've traversed up the tree (leaf -> root)
            Collections.reverse(wordArray);

            // form sentence String from array of words
            var sb: StringBuilder = StringBuilder();
            var first: Boolean = true;
            for (word: String in wordArray) {
                if (first) {
                    sb.append(word);
                    first = false;
                    continue;
                }
                sb.append(" ");
                sb.append(word);
            }
            val sentence: String = sb.toString();

            // ensure sentence is unique (in results)
            if (sentences.contains(sentence)) {
                return;
            }

            // add unique sentence to resulting sentence list
            sentences.add(sentence);
            return;
        }

        for (child: Node in node.children) {
            printPath(child);
        }
    }
}
