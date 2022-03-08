package tmcowley.appserver

import tmcowley.appserver.objects.Key
import tmcowley.appserver.objects.KeyPair
import tmcowley.appserver.controllers.DatabaseController
import tmcowley.appserver.structures.getWords
import tmcowley.appserver.structures.getSentences
import tmcowley.appserver.structures.getKeyPairHashMap


import tmcowley.appserver.utils.FreqTool
import tmcowley.appserver.utils.LangTool
import tmcowley.appserver.utils.parsePhraseList
import tmcowley.appserver.utils.parseWordList
import tmcowley.appserver.utils.parseWordFrequencies
import tmcowley.appserver.utils.parseFiveGrams



import java.util.Collections
import java.util.Properties

import java.io.FileOutputStream
import java.io.FileInputStream
import java.io.File

import kotlin.random.Random


object Singleton {

    var keyPairs: HashMap<Key, KeyPair> = getKeyPairHashMap()

    var wordSet: HashSet<String> = parseWordList()

    val wordFreqLookup: HashMap<String, Int> = parseWordFrequencies()

    val maxLengthInDictionary: Int = wordSet.maxOfOrNull { it.length }!!

    val langTool: LangTool = LangTool();

    val freqTool: FreqTool = FreqTool();

    private val propertiesFile = File("src/main/resources/application.properties");
    val prop = Properties()

    val phraseList: List<String> = parsePhraseList()

    val phraseListLength = phraseList.size

    var fiveGrams = parseFiveGrams()

    val phrasesPerSession = 8
    
    init {
        println("Singleton initiated")

        // set prop using application.properties
        FileInputStream(propertiesFile).use { inStream -> prop.load(inStream) }
    }

    fun getNextPhrase(sessionNumber: Int, phraseNumber: Int): String? {

        if (phraseNumber > Singleton.phrasesPerSession) return null

        fun getRandomList(random: Random): List<Int> {
            return List(Singleton.phrasesPerSession) { random.nextInt(0, Singleton.phraseListLength - 1) }
        }

        // generate the seed by session
        val seed = sessionNumber

        // find the corresponding phrase index
        val sessionPhraseIndexes = getRandomList(Random(seed))
        val phraseIndex = sessionPhraseIndexes.get(phraseNumber + 1)

        return Singleton.phraseList.get(phraseIndex)
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

    private fun getRandomFiveGram(): String {
        return fiveGrams.random();   
    }

    fun getRandomUserCode(): String {
        return ("${getRandomFiveGram()}-${getRandomFiveGram()}-${getRandomFiveGram()}")
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