package tmcowley.appserver.structures

class SentenceTree : CartesianProductTree<String>(Node("ε")) {

    /** get the matching sentences set from a list of matching words */
    fun getSentences(listOfSetOfWords: List<Set<String>>): Set<String> {
        // insert each set, in order
        insertAll(listOfSetOfWords)

        // read the sentences (down paths of tree)
        return readSentences()
    }

    /** get all matched words (computed from node paths) */
    private fun readSentences(): Set<String> {
        // read the paths down the three (equal to the n-ary cartesian product)
        val paths: List<List<Node<String>>> = this.getCartesianProduct()

        // generate the sentence array, removing duplicates
        val sentences: Set<String> = buildSet {
            paths.forEach { path ->
                val sentence = path.joinToString(separator = " ") { node -> node.value }
                add(sentence)
            }
        }

        return sentences
    }
}
