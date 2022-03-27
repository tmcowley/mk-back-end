package tmcowley.appserver.structures

/** generic, variadic node */
class Node<V>(val value: V) {
    private var parent: Node<V>? = null
    private var children: MutableList<Node<V>> = mutableListOf()

    /** check if this node is a leaf (has no children) */
    fun isLeaf(): Boolean {
        return (this.children.isEmpty())
    }

    /** check if this node is not a leaf */
    fun isNotLeaf(): Boolean {
        return !(this.isLeaf())
    }

    /** add a child node */
    fun addChild(child: Node<V>) {
        this.children.add(child)
    }

    /** get all child nodes */
    fun getChildren(): List<Node<V>> {
        return this.children
    }

    /** add many child nodes */
    private fun setChildren(children: MutableList<Node<V>>) {
        this.children = children
    }

    /** set the parent node */
    fun setParent(parent: Node<V>) {
        this.parent = parent
    }

    /** get the parent node */
    fun getParent(): Node<V>? {
        return this.parent
    }

    // componentN() for supporting destructuring 

    operator fun component1(): V {
        return this.value
    }

    operator fun component2(): Node<V>? {
        return this.parent
    }

    operator fun component3(): MutableList<Node<V>> {
        return this.children
    }
}
