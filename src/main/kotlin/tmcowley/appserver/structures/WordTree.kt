package tmcowley.appserver.structures

import tmcowley.appserver.models.KeyPair
import tmcowley.appserver.models.Key

class WordTree : CartesianProductTree<Key>(Node(Key('ε'))) {

    /** get all matched words (computes from node paths) */
    fun getWords(): Set<String> {
        val paths: List<List<Node<Key>>> = this.getPaths()

        // generate the words array, removing duplicates
        val words: Set<String> = buildSet {
            paths.forEach { path ->
                val word = (path.map { node -> node.value }.joinToString(separator = ""))
                add(word)
            }
        }

        return words
    }

    /** insert using a key-pair (calls insert on each key of key-pair) */
    fun insertKeyPair(keyPair: KeyPair) {
        insert(setOf(keyPair.leftKey, keyPair.rightKey))
    }
}