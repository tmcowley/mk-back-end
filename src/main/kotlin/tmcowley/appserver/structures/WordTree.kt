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

        /** check if this node is a leaf (has no children) */
        fun isLeaf(): Boolean {
            return (this.left == null && this.right == null);
        }

        /** check if this node is not a leaf */
        fun isNotLeaf(): Boolean {
            return !(this.isLeaf())
        }
    }

    var leaves: MutableList<Node> = mutableListOf();

    val root: Node = Node(Key('*'));

    fun insert(keyPair: KeyPair) {
        val leftKey: Key = keyPair.leftKey;
        val rightKey: Key = keyPair.rightKey;

        val leaves: MutableList<Node> = this.findLeaves();

        leaves.forEach { leaf -> 
            val leftChild: Node = Node(leftKey);
            val rightChild: Node = Node(rightKey);

            leaf.left = leftChild;
            leaf.right = rightChild;

            leftChild.parent = leaf;
            rightChild.parent = leaf;
        }
    }

    /**
     * find the leaf nodes of the tree
     */
    fun findLeaves(): MutableList<Node> {
        // reset global leaf list
        leaves = mutableListOf();
        
        // collect leaf nodes in leaves (globally)
        traverse(root);

        // leaves.forEach{ leaf -> println("leaf found: ${leaf.value.character}") }

        return leaves;
    }

    fun traverse(node: Node?) {
        if (node == null) return

        if (node.isNotLeaf()) {
            traverse(node.left)
            traverse(node.right)
            return
        }

        leaves.add(node);
    }

    fun traverseInOrder() {
        traverseInOrder(this.root);
    }

    fun traverseInOrder(node: Node?) {
        if (node == null) return

        traverseInOrder(node.left);
        print(" " + node.value.character);
        traverseInOrder(node.right);
    }

    var words: MutableList<String> = mutableListOf();

    fun findWords(): MutableList<String> {
        // reset global words list
        words = mutableListOf()

        printPath(root)

        return words
    }

    fun printPath(n: Node?) {
        if (n == null) return

        if (n.isNotLeaf()) {
            printPath(n.left)
            printPath(n.right)
            return
        }

        // n is a leaf

        var potentialWord: String = "";

        // crawl up from leaf node to root node
        var currentNode: Node = n;
        do {
            potentialWord += currentNode.value;

            currentNode = currentNode.parent!!;
            // print(n.value);
            // print(" ");
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
    }
}