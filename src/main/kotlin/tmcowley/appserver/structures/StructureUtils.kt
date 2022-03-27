package tmcowley.appserver.structures

import tmcowley.appserver.Singleton

import tmcowley.appserver.models.Key
import tmcowley.appserver.models.KeyPair

/** get the matching words from a word in key-pair list form */
fun getMatchedWords(currentWord: MutableList<KeyPair>): MutableList<String> {
    if (currentWord.isEmpty()) return mutableListOf()

    // generate word permutation tree (representing key-pair form)
    val currentWordTree = WordTree()
    currentWord.forEach { keyPair -> currentWordTree.insertKeyPair(keyPair) }

    // find matched words by traversing the tree paths
    var wordMatches = currentWordTree.getWords()

    // filter words not in the dictionary
    wordMatches = (wordMatches.filter { word -> Singleton.wordExists(word) }.toMutableList())

    return wordMatches
}

/** get the matching sentences against a list of matching words */
fun getSentences(listOfMatchedWords: MutableList<MutableList<String>>): MutableList<String> {
    if (listOfMatchedWords.isEmpty()) return mutableListOf()

    // generate sentence permutation tree
    val sentenceTree = SentenceTree()
    listOfMatchedWords.forEach { words -> sentenceTree.insert(words) }

    return sentenceTree.getSentences()
}

/** get the hash-map linking keys to key-pairs */
fun getKeyPairHashMap(): HashMap<Key, KeyPair> {

    // map key-pairs

    // top row
    val qp = KeyPair('q', 'p')
    val wo = KeyPair('w', 'o')
    val ei = KeyPair('e', 'i')
    val ru = KeyPair('r', 'u')
    val ty = KeyPair('t', 'y')

    // middle row
    val aAndColon = KeyPair('a', 'a')
    val sl = KeyPair('s', 'l')
    val dk = KeyPair('d', 'k')
    val fj = KeyPair('f', 'j')
    val gh = KeyPair('g', 'h')

    // bottom row
    val zAndForwardSlash = KeyPair('z', 'z')
    val xAndDot = KeyPair('x', 'x')
    val cAndComma = KeyPair('c', 'c')
    val vm = KeyPair('v', 'm')
    val bn = KeyPair('b', 'n')

    // define key -> key-pair HM
    val keyPairs = hashMapOf<Key, KeyPair>()

    // map top row
    run {
        // top row: left half
        keyPairs[Key('q')] = qp
        keyPairs[Key('w')] = wo
        keyPairs[Key('e')] = ei
        keyPairs[Key('r')] = ru
        keyPairs[Key('t')] = ty

        // top row: right half
        keyPairs[Key('p')] = qp
        keyPairs[Key('o')] = wo
        keyPairs[Key('i')] = ei
        keyPairs[Key('u')] = ru
        keyPairs[Key('y')] = ty
    }

    // map middle row
    run {
        // middle row: left half
        keyPairs[Key('a')] = aAndColon
        keyPairs[Key('s')] = sl
        keyPairs[Key('d')] = dk
        keyPairs[Key('f')] = fj
        keyPairs[Key('g')] = gh

        // middle row: right half
        keyPairs[Key('a')] = aAndColon
        keyPairs[Key('l')] = sl
        keyPairs[Key('k')] = dk
        keyPairs[Key('j')] = fj
        keyPairs[Key('h')] = gh
    }

    // map bottom row
    run {
        // bottom row: left half
        keyPairs[Key('z')] = zAndForwardSlash
        keyPairs[Key('x')] = xAndDot
        keyPairs[Key('c')] = cAndComma
        keyPairs[Key('v')] = vm
        keyPairs[Key('b')] = bn

        // bottom row: right half
        keyPairs[Key('z')] = zAndForwardSlash
        keyPairs[Key('x')] = xAndDot
        keyPairs[Key('c')] = cAndComma
        keyPairs[Key('m')] = vm
        keyPairs[Key('n')] = bn
    }

    return keyPairs
}
