package tmcowley.appserver.structures

/** generic node */
class Node<V>(val value: V) {
    private var parent: Node<V>? = null
    private var children: MutableList<Node<V>> = mutableListOf()

    /** check if this node is a leaf (has no children) */
    fun isLeaf(): Boolean {
        return (this.children.isEmpty())
    }

    /** add a child node */
    fun addChild(child: Node<V>) {
        this.children.add(child)
    }

    /** get all child nodes */
    fun getChildren(): List<Node<V>> {
        return this.children
    }

    /** set the parent node */
    fun setParent(parent: Node<V>) {
        this.parent = parent
    }

    /** get the parent node */
    fun getParent(): Node<V>? {
        return this.parent
    }

    // componentN() for destructuring support

    operator fun component1(): V {
        return this.value
    }

    operator fun component2(): Node<V>? {
        return this.parent
    }

    operator fun component3(): List<Node<V>> {
        return this.children
    }
}
