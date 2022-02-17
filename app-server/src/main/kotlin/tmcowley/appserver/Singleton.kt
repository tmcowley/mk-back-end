package tmcowley.appserver

import tmcowley.appserver.objects.Key
import tmcowley.appserver.objects.KeyPair
import tmcowley.appserver.structures.DataStructures

import tmcowley.appserver.utils.FreqTool
import tmcowley.appserver.utils.LangTool

import java.util.Collections
import java.util.Properties

import java.io.FileOutputStream
import java.io.FileInputStream
import java.io.File


object Singleton {

    val structures: DataStructures = DataStructures()

    var keyPairs: HashMap<Key, KeyPair> = structures.getKeyPairHashMap()

    var wordSet: HashSet<String> = structures.getWordSet()

    val wordFreqLookup: HashMap<String, Int> = structures.getWordFrequencies()

    val maxLengthInDictionary: Int = wordSet.maxOfOrNull { it.length }!!

    val langTool: LangTool = LangTool();

    val freqTool: FreqTool = FreqTool();

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

    fun getWordFrequency(word: String): Int {
        val wordFreq: Int? = wordFreqLookup.get(word)

        // case no frequency mapped to word
        if (wordFreq == null) {
            return 0;
        }

        return wordFreq;
    }
}