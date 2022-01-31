package tmcowley.appserver

import tmcowley.appserver.objects.Key
import tmcowley.appserver.objects.KeyPair
import tmcowley.appserver.structures.DataStructures

object Singleton {

    val structures: DataStructures = DataStructures()

    var keyPairs: HashMap<Key, KeyPair> = structures.getKeyPairHashMap()

    var wordSet: HashSet<String> = structures.getWordSet()

    init {
        println("Singleton initiated")
    }

    fun getKeyPair(key: Key): KeyPair? {
        return keyPairs.get(key);
    }

    fun wordExists(word: String): Boolean {
        return wordSet.contains(word);
    }
}