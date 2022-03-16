package tmcowley.appserver.structures;

import tmcowley.appserver.Singleton

import tmcowley.appserver.objects.Key
import tmcowley.appserver.objects.KeyPair

import tmcowley.appserver.structures.WordTree
import tmcowley.appserver.structures.SentenceTree

class StructureUtils

/** get the matching words from a word in key-pair list form */
fun getMatchedWords(currentWord: MutableList<KeyPair>): MutableList<String> {
    if (currentWord.isEmpty()) return mutableListOf()

    // generate word permutation tree (representing key-pair form)
    val currentWordTree: WordTree = WordTree()
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
    var sentenceTree: SentenceTree = SentenceTree()
    listOfMatchedWords.forEach { words -> sentenceTree.insert(words) }

    return sentenceTree.getSentences()
}

/** get the hash-map linking keys to key-pairs */
fun getKeyPairHashMap(): HashMap<Key, KeyPair> {

    // map key-pairs

    // top row
    // val qp: KeyPair = KeyPair(charToKeyLookup.get('q')!!, charToKeyLookup.get('q')!!);
    val qp: KeyPair = KeyPair('q', 'p');
    val wo: KeyPair = KeyPair('w', 'o');
    val ei: KeyPair = KeyPair('e', 'i');
    val ru: KeyPair = KeyPair('r', 'u');
    val ty: KeyPair = KeyPair('t', 'y');

    // middle row
    // sort out dual chars w/ shift
    val a_colon: KeyPair = KeyPair('a', 'a');
    val sl: KeyPair = KeyPair('s', 'l');
    val dk: KeyPair = KeyPair('d', 'k');
    val fj: KeyPair = KeyPair('f', 'j');
    val gh: KeyPair = KeyPair('g', 'h');

    // bottom row
    val z_f_slash: KeyPair = KeyPair('z', 'z');
    val x_dot: KeyPair = KeyPair('x', 'x');
    val c_comma: KeyPair = KeyPair('c', 'c');
    val vm: KeyPair = KeyPair('v', 'm');
    val bn: KeyPair = KeyPair('b', 'n');

    // define key -> key-pair HM
    var keyPairs: HashMap<Key, KeyPair> = hashMapOf<Key, KeyPair>();
    
    // map top row
    run {
        // top row: left half
        keyPairs.put(Key('q'), qp);
        keyPairs.put(Key('w'), wo);
        keyPairs.put(Key('e'), ei);
        keyPairs.put(Key('r'), ru);
        keyPairs.put(Key('t'), ty);

        // top row: right half
        keyPairs.put(Key('p'), qp);
        keyPairs.put(Key('o'), wo);
        keyPairs.put(Key('i'), ei);
        keyPairs.put(Key('u'), ru);
        keyPairs.put(Key('y'), ty);
    }

    // map middle row
    run {
        // middle row: left half
        keyPairs.put(Key('a'), a_colon);
        keyPairs.put(Key('s'), sl);
        keyPairs.put(Key('d'), dk);
        keyPairs.put(Key('f'), fj);
        keyPairs.put(Key('g'), gh);

        // middle row: right half
        keyPairs.put(Key('a'), a_colon);
        keyPairs.put(Key('l'), sl);
        keyPairs.put(Key('k'), dk);
        keyPairs.put(Key('j'), fj);
        keyPairs.put(Key('h'), gh);
    }

    // map bottom row
    run {
        // bottom row: left half
        keyPairs.put(Key('z'), z_f_slash);
        keyPairs.put(Key('x'), x_dot);
        keyPairs.put(Key('c'), c_comma);
        keyPairs.put(Key('v'), vm);
        keyPairs.put(Key('b'), bn);

        // bottom row: right half
        keyPairs.put(Key('z'), z_f_slash);
        keyPairs.put(Key('x'), x_dot);
        keyPairs.put(Key('c'), c_comma);
        keyPairs.put(Key('m'), vm);
        keyPairs.put(Key('n'), bn);
    }

    return keyPairs;
}
