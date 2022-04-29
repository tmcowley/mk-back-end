package tmcowley.appserver.structures

/** 
 * generic cartesian product tree;
 * 
 * facilitates finding the cartesian product of the added sets; 
 * new nodes are added at each leaf node; 
 * the paths in the tree correspond to elements of the cartesian product of all added sets; 
 * 
 * cartesian product example for a key-pair list:
 * word: key
 * key-pair list: [{d, k}, {e, i}, {t, y}]
 * cartesian product: {d, k} x {e, i} x {t, y} = [
 *  (d, e, t), (d, e, y), (d, i, t), (d, i, y), 
 *  (k, e, t), (k, e, y), (k, i, t), (k, i, y)
 * ]
 */
abstract class CartesianProductTree<T>(val root: Node<T>) {

    // global list for storing leaves
    private var leaves: MutableList<Node<T>> = mutableListOf()

    /** insert a set of values to the tree; adds each object to each leaf node */
    protected fun insert(childValues: Set<T>) {
        // add each child to each leaf
        getLeaves()
            .forEach { leaf ->
                childValues.forEach { childValue ->
                    insert(childValue, leaf)
                }
            }
    }

    /**
     * insert a list of set of values to the tree;
     * for each set in the list, adds each set item to each leaf nodes */
    protected fun insertAll(allChildValues: List<Set<T>>) {
        allChildValues.forEach { childValues ->
            insert(childValues)
        }
    }

    /** add a child underneath a parent node */
    private fun insert(childValue: T, parent: Node<T>) {
        val child = Node(childValue)
        child.setParent(parent)
        parent.addChild(child)
    }

    /** get the leaf nodes of the tree; public access-modifier for testing */
    private fun getLeaves(): List<Node<T>> {
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
        // base cases
        if (node == null) return
        if (node.isLeaf()) {
            leaves.add(node)
            return
        }

        // recursive case:
        // traverse down, from each child
        node.getChildren().forEach { child -> findLeaves(child) }
    }

    /** get the paths down the tree */
    protected fun getCartesianProduct(): List<List<Node<T>>> {
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
