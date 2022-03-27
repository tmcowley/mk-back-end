package tmcowley.appserver.structures

import tmcowley.appserver.models.KeyPair
import tmcowley.appserver.models.Key

class WordTree : PermutationTree<Key>(Node(Key('ε'))) {

    /** get all matched words (computes from node paths) */
    fun getWords(): MutableList<String> {
        val paths: MutableList<MutableList<Node<Key>>> = this.getPaths()

        // generate the words array
        var words: MutableList<String> = mutableListOf()
        paths.forEach { path ->
            val word = (path.map { node -> node.value }.joinToString(separator = ""))
            words.add(word)
        }

        // remove duplicate words
        words = words.distinct().toMutableList()
        return words
    }

    /** insert using a key-pair (calls insert on each key of key-pair) */
    fun insertKeyPair(keyPair: KeyPair) {
        insert(mutableListOf(keyPair.leftKey, keyPair.rightKey))
    }
}