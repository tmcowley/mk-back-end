package tmcowley.appserver

import tmcowley.appserver.objects.Key
import tmcowley.appserver.objects.KeyPair
import tmcowley.appserver.structures.DataStructures
import tmcowley.appserver.utils.Langtool

import java.util.Collections
import java.util.Properties

import java.io.FileOutputStream
import java.io.FileInputStream
import java.io.File


object Singleton {

    val structures: DataStructures = DataStructures()

    var keyPairs: HashMap<Key, KeyPair> = structures.getKeyPairHashMap()

    var wordSet: HashSet<String> = structures.getWordSet()

    val maxLengthInDictionary: Int = wordSet.maxOfOrNull { it.length }!!

    val langtool: Langtool = Langtool();

    private val propertiesFile = File("src/main/resources/application.properties");
    val prop = Properties()

    val phraseList = structures.getPhraseList()
    
    init {
        println("Singleton initiated")

        // set prop using application.properties
        FileInputStream(propertiesFile).use { prop.load(it) }
    }

    fun getKeyPair(key: Key): KeyPair? {
        return keyPairs.get(key);
    }

    fun wordExists(word: String): Boolean {
        return wordSet.contains(word);
    }

    fun getRandomPhrase(): String {
        return phraseList.random();
    }
}