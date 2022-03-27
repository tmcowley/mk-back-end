package tmcowley.appserver.structures

class SentenceTree : PermutationTree<String>(Node("ε")) {

    fun getSentences(): MutableList<String> {
        val paths: MutableList<MutableList<Node<String>>> = this.getPaths()

        // generate the sentence array
        var sentences: MutableList<String> = mutableListOf()
        paths.forEach { path ->
            val sentence = path.joinToString(separator = " ") { node -> node.value }
            sentences.add(sentence)
        }

        // remove non-unique sentences
        sentences = sentences.distinct().toMutableList()

        return sentences
    }
}
