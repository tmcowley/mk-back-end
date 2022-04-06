package tmcowley.appserver.structures

// generic permutation tree
abstract class PermutationTree<T>(val root: Node<T>) {

    // global list for storing leaves
    private var leaves: MutableList<Node<T>> = mutableListOf()

    /** insert a set of values to the tree; adds each object to each leaf node */
    fun insert(childValues: Set<T>) {
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
        val leaves = getLeaves()

        val paths = buildList(leaves.size) {
            leaves.forEach { leaf ->
                // path is a list of nodes from root to leaf
                // reversed path order since we've traversed up the tree (leaf -> root)
                val path = buildList {
                    // crawl up from leaf node to root node
                    var currentNode = leaf
                    do {
                        // add current node to path
                        add(currentNode)

                        // break (if currentNode is root)
                        currentNode = currentNode.getParent() ?: break
                    } while (currentNode != root)
                }.asReversed()

                add(path)
            }
        }

        return paths
    }
}
