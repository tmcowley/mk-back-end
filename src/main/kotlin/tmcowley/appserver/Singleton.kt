package tmcowley.appserver

import java.io.File
import java.io.FileInputStream
import java.util.Properties
import kotlin.random.Random
import tmcowley.appserver.models.Key
import tmcowley.appserver.models.KeyPair
import tmcowley.appserver.utils.LangTool
import tmcowley.appserver.utils.getWords
import tmcowley.appserver.utils.getPhrases
import tmcowley.appserver.utils.getFiveGrams
import tmcowley.appserver.utils.getKeyPairMap
import tmcowley.appserver.utils.getWordFrequencies

object Singleton {

    // word and phrase lists

    var words: Set<String> = getWords()
    val phrases: List<String> = getPhrases()
    private var fiveGrams = getFiveGrams()

    // properties and constants

    private const val resourcesPath = "src/main/resources/"
    private val propertiesFile = File(resourcesPath + "application.properties")
    val prop = Properties()

    // note: stringDataPath typically "src/main/resources/string-data/"
    const val stringDataPath = resourcesPath + "string-data/"

    // mutable to allow setting with reflection (when testing)
    var syntaxAnalysisEnabled: Boolean
    var frequencyAnalysisEnabled: Boolean

    const val maxInputLength = 25
    const val phrasesPerSession = 8
    val maxWord = words.maxByOrNull { word -> word.length }

    // maps

    private var keyPairs: Map<Key, KeyPair> = getKeyPairMap()
    private val wordFreqLookup= getWordFrequencies()


    // tools/ util-classes

    val langTool: LangTool = LangTool()


    init {
        println("Singleton initiated")

        // set prop using application.properties
        FileInputStream(propertiesFile).use { inStream -> prop.load(inStream) }

        // set from system properties
        syntaxAnalysisEnabled = (this.prop["analyseSyntax"] as String).toBoolean()
        frequencyAnalysisEnabled = (this.prop["analyseFrequency"] as String).toBoolean()
    }

    /**
     * get the phrase from the phrase and session numbers
     * sessions: [1, ∞)
     * phrases: [1, 8]
     * */
    fun getPhrase(sessionNumber: Int, phraseNumber: Int): String? {
        // ensure session number is positive
        if (sessionNumber <= 0) return null

        // ensure the phrase number does not exceed the defined count
        if (phraseNumber <= 0) return null
        if (phraseNumber > phrasesPerSession) return null

        fun getRandomList(random: Random): List<Int> {
            return List(phrasesPerSession) {
                random.nextInt(0, phrases.size - 1)
            }
        }

        // find the corresponding phrase index
        val sessionPhraseIndexes = getRandomList(Random(seed = sessionNumber))
        val phraseIndex = sessionPhraseIndexes[phraseNumber-1]

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

    /** for resetting analysis enabled states from properties (used in testing) */
    fun resetAnalysisEnabledStates() {
        // set from system properties
        syntaxAnalysisEnabled = (this.prop["analyseSyntax"] as String).toBoolean()
        frequencyAnalysisEnabled = (this.prop["analyseFrequency"] as String).toBoolean()
    }
}
