package tmcowley.appserver.structures

import tmcowley.appserver.models.Key
import tmcowley.appserver.models.KeyPair
import tmcowley.appserver.utils.getWordInKeyPairForm

class WordTree : CartesianProductTree<Key>(Node(Key('ε'))) {

    /** get the matching words from a word in key-pair list form */
    fun getWords(word: String): Set<String> =
        getAllWords(word).toSet()

    /** get the matching words from a word in key-pair list form */
    fun getAllWords(word: String): List<String> {
        // ensure empty word does not build empty tree
        if (word.isEmpty()) return listOf()

        // convert word to key-pair form
        val wordAsKeyPairList: List<KeyPair> = getWordInKeyPairForm(word)

        // convert key-pairs to sets of keys, e.g. [Key('q'), Key('p')] -> {Key('q'), Key('p')}
        val wordAsKeyPairSetList = wordAsKeyPairList.map { keyPair ->
            keyPair.toSet()
        }

        // insert each key-pair set, in order
        insertAll(wordAsKeyPairSetList)

        // read the words (down paths of tree)
        return readWords()
    }

    /** get all matched words (computed from node paths) */
    private fun readWords(): List<String> {
        // read the paths down the three (equal to the n-ary cartesian product)
        val paths: List<List<Node<Key>>> = this.getCartesianProduct()

        // generate the words array, removing duplicates
        val words: List<String> = buildList {
            paths.forEach { path ->
                val word = path
                    .map { node ->
                        node.value
                    }
                    .joinToString(separator = "")
                add(word)
            }
        }

        return words
    }
}
