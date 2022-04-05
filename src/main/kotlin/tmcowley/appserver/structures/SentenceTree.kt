package tmcowley.appserver.structures

class SentenceTree : PermutationTree<String>(Node("ε")) {

    fun getSentences(): List<String> {
        val paths: List<List<Node<String>>> = this.getPaths()

        // generate the sentence array, removing duplicates
        val sentences: List<String> = buildList {
            paths.forEach { path ->
                val sentence = path.joinToString(separator = " ") { node -> node.value }
                add(sentence)
            }
        }.distinct()

        return sentences
    }
}
