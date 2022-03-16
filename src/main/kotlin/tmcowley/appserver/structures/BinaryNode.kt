// package tmcowley.appserver.structures

// import tmcowley.appserver.structures.Node

// import tmcowley.appserver.objects.Key


// data class BinaryNode(val value: Key): Node<Key> {
//     var parent: Node<Key>? = null
//     var left: Node<Key>? = null
//     var right: Node<Key>? = null

//     /** check if this node is a leaf (has no children) */
//     override fun isLeaf(): Boolean {
//         return (this.left == null && this.right == null)
//     }

//     /** check if this node is not a leaf */
//     override fun isNotLeaf(): Boolean {
//         return !(this.isLeaf())
//     }

//     /** add a child node */
//     fun addChild(child: Node<Key>) {

//         this.left ?: this.left = child as BinaryNode
        

//         if (this.left ==)
//     }

//     /** get all child nodes */
//     override fun getChildren(): List<BinaryNode> {
//         if (this.isLeaf()) return listOf()
//         return listOf(this.left!!, this.right!!)
//     }

//     /** add many child nodes */
//     fun setChildren(children: MutableList<Node<Key>>) 

//     /** set the parent node */
//     fun setParent(parent: Node<Key>) {
//         this.parent = parent
//     }

//     /** get the parent node */
//     fun getParent(): Node<Key>? 
// }
