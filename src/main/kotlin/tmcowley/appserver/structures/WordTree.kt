package tmcowley.appserver.structures

import tmcowley.appserver.objects.KeyPair
import tmcowley.appserver.objects.Key

class WordTree : PermutationTree<Key>(Node(Key('ε'))) {

    fun getWords(): MutableList<String> {
        val paths: MutableList<MutableList<Node<Key>>> = this.getPaths()

        // generate the words array
        var words: MutableList<String> = mutableListOf()
        paths.forEach { path ->
            val word = (path.map{ node -> node.value }.joinToString(separator = ""))
            words.add(word)
        }

        // remove duplicate words
        words = words.distinct().toMutableList()

        return words
    }

    fun insertKeyPair(keyPair: KeyPair) {
        insert(mutableListOf(keyPair.leftKey, keyPair.rightKey))
    }
}