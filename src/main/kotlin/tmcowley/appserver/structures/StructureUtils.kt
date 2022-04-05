package tmcowley.appserver.structures

import tmcowley.appserver.Singleton

import tmcowley.appserver.models.Key
import tmcowley.appserver.models.KeyPair

/** get the matching words from a word in key-pair list form */
fun getMatchedWords(currentWord: List<KeyPair>): List<String> {
    if (currentWord.isEmpty()) return listOf()

    // generate word permutation tree (representing key-pair form)
    val currentWordTree = WordTree()
    currentWord.forEach { keyPair -> currentWordTree.insertKeyPair(keyPair) }

    // find matched words by traversing the tree paths
    var wordMatches = currentWordTree.getWords()

    // filter words not in the dictionary
    wordMatches = wordMatches.filter { word -> Singleton.wordExists(word) }

    return wordMatches
}

/** get the matching sentences against a list of matching words */
fun getSentences(listOfMatchedWords: List<List<String>>): List<String> {
    if (listOfMatchedWords.isEmpty()) return listOf()

    // generate sentence permutation tree
    val sentenceTree = SentenceTree()
    listOfMatchedWords.forEach { words -> sentenceTree.insert(words) }

    return sentenceTree.getSentences()
}

/** get the hash-map linking keys to key-pairs */
fun getKeyPairHashMap(): Map<Key, KeyPair> {

    val topRowMapping = listOf(
        // top row: left half
        Pair(Key('q'), KeyPair('q', 'p')),
        Pair(Key('w'), KeyPair('w', 'o')),
        Pair(Key('e'), KeyPair('e', 'i')),
        Pair(Key('r'), KeyPair('r', 'u')),
        Pair(Key('t'), KeyPair('t', 'y')),

        // top row: right half
        Pair(Key('p'), KeyPair('q', 'p')),
        Pair(Key('o'), KeyPair('w', 'o')),
        Pair(Key('i'), KeyPair('e', 'i')),
        Pair(Key('u'), KeyPair('r', 'u')),
        Pair(Key('y'), KeyPair('t', 'y'))
    )

    val middleRowMapping = listOf(
        // middle row: left half
        Pair(Key('a'), KeyPair('a', 'a')),
        Pair(Key('s'), KeyPair('s', 'l')),
        Pair(Key('d'), KeyPair('d', 'k')),
        Pair(Key('f'), KeyPair('f', 'j')),
        Pair(Key('g'), KeyPair('g', 'h')),

        // middle row: right half
        Pair(Key('a'), KeyPair('a', 'a')),
        Pair(Key('l'), KeyPair('s', 'l')),
        Pair(Key('k'), KeyPair('d', 'k')),
        Pair(Key('j'), KeyPair('f', 'j')),
        Pair(Key('h'), KeyPair('g', 'h'))
    )

    val bottomRowMapping = listOf(
        // bottom row: left half
        Pair(Key('z'), KeyPair('z', 'z')),
        Pair(Key('x'), KeyPair('x', 'x')),
        Pair(Key('c'), KeyPair('c', 'c')),
        Pair(Key('v'), KeyPair('v', 'm')),
        Pair(Key('b'), KeyPair('b', 'n')),

        // bottom row: right half
        Pair(Key('z'), KeyPair('z', 'z')),
        Pair(Key('x'), KeyPair('x', 'x')),
        Pair(Key('c'), KeyPair('c', 'c')),
        Pair(Key('m'), KeyPair('v', 'm')),
        Pair(Key('n'), KeyPair('b', 'n'))
    )

    // define key -> key-pair HM
    val keyPairs: Map<Key, KeyPair> = buildMap {
        // map top row
        topRowMapping.forEach { pairMap ->
            val key = pairMap.first
            val keyPair = pairMap.second
            put(key, keyPair)
        }

        // map middle row
        middleRowMapping.forEach { pairMap ->
            val key = pairMap.first
            val keyPair = pairMap.second
            put(key, keyPair)
        }

        // map bottom row
        bottomRowMapping.forEach { pairMap ->
            val key = pairMap.first
            val keyPair = pairMap.second
            put(key, keyPair)
        }
    }

    return keyPairs
}
