package tmcowley.appserver.structures

final class SentenceTree : PermutationTree<String>(Node("ε")) {

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
}
