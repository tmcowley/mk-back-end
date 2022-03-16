package tmcowley.appserver.structures;

interface NodeInterface<V> {
    /** check if this node is a leaf (has no children) */
    fun isLeaf(): Boolean 

    /** check if this node is not a leaf */
    fun isNotLeaf(): Boolean 

    /** add a child node */
    fun addChild(child: Node<V>)

    /** get all child nodes */
    fun getChildren(): List<Node<V>> 

    /** add many child nodes */
    fun setChildren(children: MutableList<Node<V>>) 

    /** set the parent node */
    fun setParent(parent: Node<V>) 

    /** get the parent node */
    fun getParent(): Node<V>? 
}
