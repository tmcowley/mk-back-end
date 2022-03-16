package tmcowley.appserver.structures

class SentenceTree : PermutationTree<String>(Node("ε")) {

    fun getSentences(): MutableList<String> {
        val paths: MutableList<MutableList<Node<String>>> = this.getPaths()

        // generate the sentence array
        var sentences: MutableList<String> = mutableListOf()
        paths.forEach { path ->
            val sentence = path.map { node -> node.value }.joinToString(separator = " ")
            sentences.add(sentence)
        }

        // remove non-unique sentences
        sentences = sentences.distinct().toMutableList()

        return sentences
    }

    // -----

    /** find the paths down the tree (traversing down from node) */
    // private fun findPathsOld(node: Node<String>?) {
    //     if (node == null) return

    //     // found leaf: traverse up to root
    //     if (node.isLeaf()) {

    //         // stores each word in the sentence (initially order reversed)
    //         var wordArray: MutableList<String> = mutableListOf()

    //         var currentNode: Node<String> = node

    //         do {
    //             // add word (node) to word array
    //             val thisWord: String = currentNode.value
    //             wordArray.add(thisWord)

    //             // traverse to the parent - as this is never root the parent is never null
    //             currentNode = currentNode.getParent() ?: break
    //         } while (currentNode != root)

    //         // reverse word order since we've traversed up the tree (leaf -> root)
    //         Collections.reverse(wordArray)

    //         // form sentence String from array of words
    //         var sb: StringBuilder = StringBuilder()
    //         var first: Boolean = true
    //         for (word in wordArray) {
    //             if (first) {
    //                 sb.append(word)
    //                 first = false
    //                 continue
    //             }
    //             sb.append(" ")
    //             sb.append(word)
    //         }
    //         val sentence: String = sb.toString()

    //         // ensure path (in results) is unique
    //         if (paths.contains(sentence)) return

    //         // add unique sentence to resulting sentence list
    //         paths.add(sentence)
    //         return
    //     }

    //     // run on each child
    //     node.getChildren().forEach { child -> findPaths(child) }
    // }
}
