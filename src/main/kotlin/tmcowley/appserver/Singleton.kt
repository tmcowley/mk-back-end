package tmcowley.appserver

import java.io.File
import java.io.FileInputStream
import java.util.Properties
import kotlin.random.Random
import tmcowley.appserver.models.Key
import tmcowley.appserver.models.KeyPair
import tmcowley.appserver.structures.getKeyPairHashMap
import tmcowley.appserver.utils.LangTool
import tmcowley.appserver.utils.parseFiveGrams
import tmcowley.appserver.utils.parsePhrases
import tmcowley.appserver.utils.parseWordFrequencies
import tmcowley.appserver.utils.parseWords

object Singleton {

    // word and phrase lists

    var words: HashSet<String> = parseWords()
    val phrases: List<String> = parsePhrases()
    var fiveGrams = parseFiveGrams()

    // properties and constants

    private val propertiesFile = File("src/main/resources/application.properties")
    val prop = Properties()

    val syntaxAnalysisEnabled: Boolean
    val frequencyAnalysisEnabled: Boolean
    const val phrasesPerSession = 8
    val maxWord = words.maxByOrNull { word -> word.length }

    // maps

    var keyPairs: HashMap<Key, KeyPair> = getKeyPairHashMap()
    private val wordFreqLookup: HashMap<String, Int> = parseWordFrequencies()


    // tools/ util-classes

    val langTool: LangTool = LangTool()


    init {
        println("Singleton initiated")

        // set prop using application.properties
        FileInputStream(propertiesFile).use { inStream -> prop.load(inStream) }

        // set constants from system properties
        syntaxAnalysisEnabled = (this.prop["analyseSyntax"] as String).toBoolean()
        frequencyAnalysisEnabled = (this.prop["analyseFrequency"] as String).toBoolean()
    }

    /** get the next phrase according to the current phrase number and the session */
    fun getNextPhrase(sessionNumber: Int, phraseNumber: Int): String? {

        // ensure the phrase number does not exceed the defined count
        if (phraseNumber > phrasesPerSession) return null

        fun getRandomList(random: Random): List<Int> {
            return List(phrasesPerSession) {
                random.nextInt(0, phrases.size - 1)
            }
        }

        // find the corresponding phrase index
        val sessionPhraseIndexes = getRandomList(Random(seed = sessionNumber))
        val phraseIndex = sessionPhraseIndexes[phraseNumber + 1]

        return phrases[phraseIndex]
    }

    /** get the key-pair stored against a key */
    fun getKeyPairOrNull(key: Key): KeyPair? {
        // lookup mapped keypair (e.g. q -> (q, p))
        return keyPairs[key]
    }

    /** get the key-pair stored against a key (storing char) */
    fun getKeyPairOrNull(character: Char): KeyPair? {
        return getKeyPairOrNull(Key(character))
    }

    /** check if a word exists in the word-set */
    fun wordExists(word: String): Boolean {
        return words.contains(word)
    }

    /** get a random phrase */
    fun getRandomPhrase(): String {
        return phrases.random()
    }

    /** get a random five-length word */
    private fun getRandomFiveGram(): String {
        return fiveGrams.random()
    }

    /** get a random user-code (three dash-joined five-length words) */
    fun getRandomUserCode(): String {
        return ("${getRandomFiveGram()}-${getRandomFiveGram()}-${getRandomFiveGram()}")
    }

    /** get the frequency score of a word */
    fun getWordFrequency(word: String): Int {
        val wordFreq: Int? = wordFreqLookup[word]
        return wordFreq ?: 0
    }
}
