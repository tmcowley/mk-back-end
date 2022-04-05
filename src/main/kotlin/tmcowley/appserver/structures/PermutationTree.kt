package tmcowley.appserver.structures

// generic tree
abstract class PermutationTree<T>(val root: Node<T>) {

    // global list for storing leaves
    private var leaves: MutableList<Node<T>> = mutableListOf()

    // global list for storing paths (sentences/ words)
    private var paths: MutableList<List<Node<T>>> = mutableListOf()

    /** insert a list of objects to the tree (as values, not nodes); adds each object to each leaf node */
    fun insert(childValues: List<T>) {
        // add each child to each leaf
        val leaves = getLeaves()
        leaves.forEach { leaf -> childValues.forEach { childValue -> insert(childValue, leaf) } }
    }

    /** add a child underneath a parent node */
    private fun insert(childValue: T, parent: Node<T>) {
        val child = Node(childValue)
        child.setParent(parent)
        parent.addChild(child)
    }

    /** get the leaf nodes of the tree */
    fun getLeaves(): List<Node<T>> {
        // reset global leaves list
        this.leaves = mutableListOf()

        findLeaves()
        // leaves.forEach{ leaf -> println("leaf found: ${leaf.value.character}") }

        return this.leaves.toList()
    }

    /** find the tree leaves, traversing down from root node */
    private fun findLeaves() {
        return findLeaves(this.root)
    }

    /** find the tree leaves, traversing down from the given node */
    private fun findLeaves(node: Node<T>?) {
        if (node == null) return
        if (node.isLeaf()) {
            leaves.add(node)
            return
        }

        // traverse down, from each child
        node.getChildren().forEach { child -> findLeaves(child) }
    }

    /** get the paths down the tree */
    fun getPaths(): List<List<Node<T>>> {
        // reset global paths list
        this.paths = mutableListOf()

        findPaths()

        return this.paths.toList()
    }

    /** find the paths down the tree (traversing down from root) */
    private fun findPaths() {
        return findPaths(this.root)
    }

    /** find the paths down the tree (traversing down from node) */
    private fun findPaths(node: Node<T>?) {
        if (node == null) return

        // found leaf: traverse up to root
        if (node.isLeaf()) {
            // stores each word in the sentence (initially order reversed)
            val path: MutableList<Node<T>> = mutableListOf()

            // crawl up from leaf node to root node
            var currentNode: Node<T> = node
            do {
                // add current node to path
                path.add(currentNode)

                // break (if currentNode is root)
                currentNode = currentNode.getParent() ?: break
            } while (currentNode != root)

            // reverse path order since we've traversed up the tree (leaf -> root)
            path.reverse()

            this.paths.add(path.toList())

            return
        }

        // run on each child
        node.getChildren().forEach { child -> findPaths(child) }
    }
}
