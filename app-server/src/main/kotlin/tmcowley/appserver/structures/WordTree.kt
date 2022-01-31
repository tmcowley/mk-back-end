package tmcowley.appserver.structures;

import tmcowley.appserver.App;
import tmcowley.appserver.Singleton;

import tmcowley.appserver.objects.Key;
import tmcowley.appserver.objects.KeyPair;

class WordTree() {

    data class Node(
        val value: Key
    ) {

        var parent: Node? = null;
        var left: Node? = null;
        var right: Node? = null;

        /**
         * check if this node is a leaf (has no children)
         * 
         * @return node leaf state
         */
        fun isLeaf(): Boolean {
            return (this.left == null && this.right == null);
        }
    }

    var leaves: MutableList<Node> = mutableListOf();

    val root: Node = Node(Key('*'));

    fun insert(keyPair: KeyPair) {
        val key1: Key = keyPair.key1;
        val key2: Key = keyPair.key2;

        val leaves: MutableList<Node> = this.findLeaves();

        for (leaf: Node in leaves) {

            val leftChild: Node = Node(key1);
            val rightChild: Node = Node(key2);

            leaf.left = leftChild;
            leaf.right = rightChild;

            leftChild.parent = leaf;
            rightChild.parent = leaf;
        }
    }

    // formerly getLeaves()
    fun findLeaves(): MutableList<Node> {

        // reset global leaf list
        leaves = mutableListOf();
        
        // collect leaf nodes in leaves (globally)
        traverse(root);

        // for (Node leaf : leaves) {
        // System.out.println("leaf found: " + leaf.value.character);
        // }

        return leaves;
    }

    fun traverse(node: Node?) {
        if (node != null) {
            if (node.isLeaf()) {
                leaves.add(node);
                return;
            }
            traverse(node.left);
            traverse(node.right);
        }
    }

    fun traverseInOrder() {
        traverseInOrder(this.root);
    }

    fun traverseInOrder(node: Node?) {
        if (node != null) {
            traverseInOrder(node.left);
            System.out.print(" " + node.value.character);
            traverseInOrder(node.right);
        }
    }

    var words: MutableList<String> = mutableListOf();

    // formerly getWords()
    fun findWords(): MutableList<String> {

        // reset global words list
        words = mutableListOf();

        printPath(root);

        return words;
    }

    fun printPath(n: Node?) {
        if (n == null) {
            return;
        }

        if (n.isLeaf()) {
            var potentialWord: String = "";

            // crawl up from leaf node to root node
            var currentNode: Node = n;
            do {
                potentialWord += currentNode.value;

                currentNode = currentNode.parent!!;
                // System.out.print(n.value);
                // System.out.print(" ");
            } while (currentNode != root);


            potentialWord = StringBuilder(potentialWord).reverse().toString();

            // don't store if word is duplicate
            if (words.contains(potentialWord)) {
                // println("word is duplicate")
                return;
            }

            // ignore word if it does not exist in the dictionary
            if (!Singleton.wordExists(potentialWord)) {
                // println("word is not in dictionary")
                return;
            }

            // println("adding word to words")
            words.add(potentialWord);

            return;
        }

        printPath(n.left);
        printPath(n.right);
    }


}