package tmcowley.appserver

import java.io.File
import java.io.FileInputStream
import java.util.Properties
import kotlin.random.Random
import tmcowley.appserver.objects.Key
import tmcowley.appserver.objects.KeyPair
import tmcowley.appserver.structures.getKeyPairHashMap
import tmcowley.appserver.utils.LangTool
import tmcowley.appserver.utils.parseFiveGrams
import tmcowley.appserver.utils.parsePhrases
import tmcowley.appserver.utils.parseWordFrequencies
import tmcowley.appserver.utils.parseWords

final object Singleton {

    // word and phrase lists

    var words: HashSet<String> = parseWords()
    val phrases: List<String> = parsePhrases()
    var fiveGrams = parseFiveGrams()

    // properties and constants

    private val propertiesFile = File("src/main/resources/application.properties")
    val prop = Properties()

    val syntaxAnalysisEnabled: Boolean
    val frequencyAnalysisEnabled: Boolean
    val phrasesPerSession = 8
    val maxWord = words.maxByOrNull { word -> word.length }

    // maps

    var keyPairs: HashMap<Key, KeyPair> = getKeyPairHashMap()
    val wordFreqLookup: HashMap<String, Int> = parseWordFrequencies()


    // tools/ util-classes

    val langTool: LangTool = LangTool()


    init {
        println("Singleton initiated")

        // set prop using application.properties
        FileInputStream(propertiesFile).use { inStream -> prop.load(inStream) }

        // set constants from system properties
        syntaxAnalysisEnabled = (this.prop.get("analyseSyntax") as String).toBoolean()
        frequencyAnalysisEnabled = (this.prop.get("analyseFrequency") as String).toBoolean()
    }

    /** get the next phrase according to the current phrase number and the session */
    fun getNextPhrase(sessionNumber: Int, phraseNumber: Int): String? {

        // ensure the phrase number does not exeed the defined count
        if (phraseNumber > Singleton.phrasesPerSession) return null

        fun getRandomList(random: Random): List<Int> {
            return List(Singleton.phrasesPerSession) {
                random.nextInt(0, Singleton.phrases.size - 1)
            }
        }

        // generate the seed by session
        val seed = sessionNumber

        // find the corresponding phrase index
        val sessionPhraseIndexes = getRandomList(Random(seed))
        val phraseIndex = sessionPhraseIndexes.get(phraseNumber + 1)

        return Singleton.phrases.get(phraseIndex)
    }

    /** get the key-pair stored against a key */
    fun getKeyPair(key: Key): KeyPair? {
        return keyPairs.get(key)
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

    /** get a random user-code (three dash-joined five-lenght words) */
    fun getRandomUserCode(): String {
        return ("${getRandomFiveGram()}-${getRandomFiveGram()}-${getRandomFiveGram()}")
    }

    /** get the frequency score of a word */
    fun getWordFrequency(word: String): Int {
        val wordFreq: Int? = wordFreqLookup.get(word)
        return wordFreq ?: 0
    }
}
