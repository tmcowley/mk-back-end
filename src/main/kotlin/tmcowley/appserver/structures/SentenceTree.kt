package tmcowley.appserver.structures

class SentenceTree : CartesianProductTree<String>(Node("ε")) {

    fun getSentences(): Set<String> {
        val paths: List<List<Node<String>>> = this.getPaths()

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
