package tmcowley.appserver.structures

import tmcowley.appserver.models.KeyPair
import tmcowley.appserver.models.Key

class WordTree : PermutationTree<Key>(Node(Key('ε'))) {

    /** get all matched words (computes from node paths) */
    fun getWords(): List<String> {
        val paths: List<List<Node<Key>>> = this.getPaths()

        // generate the words array, removing duplicates
        val words: List<String> = buildList {
            paths.forEach { path ->
                val word = (path.map { node -> node.value }.joinToString(separator = ""))
                add(word)
            }
        }.distinct()

        return words
    }

    /** insert using a key-pair (calls insert on each key of key-pair) */
    fun insertKeyPair(keyPair: KeyPair) {
        insert(mutableListOf(keyPair.leftKey, keyPair.rightKey))
    }
}