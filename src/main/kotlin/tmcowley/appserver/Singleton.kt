package tmcowley.appserver

import java.io.File
import java.io.FileInputStream
import java.util.Properties
import kotlin.random.Random
import tmcowley.appserver.objects.Key
import tmcowley.appserver.objects.KeyPair
import tmcowley.appserver.structures.getKeyPairHashMap
import tmcowley.appserver.utils.FreqTool
import tmcowley.appserver.utils.LangTool
import tmcowley.appserver.utils.parseFiveGrams
import tmcowley.appserver.utils.parsePhraseList
import tmcowley.appserver.utils.parseWordFrequencies
import tmcowley.appserver.utils.parseWordList

object Singleton {

    var keyPairs: HashMap<Key, KeyPair> = getKeyPairHashMap()

    var wordSet: HashSet<String> = parseWordList()

    val wordFreqLookup: HashMap<String, Int> = parseWordFrequencies()

    val maxLengthInDictionary: Int = wordSet.maxOfOrNull { word -> word.length } ?: 0

    val langTool: LangTool = LangTool()

    val freqTool: FreqTool = FreqTool()

    private val propertiesFile = File("src/main/resources/application.properties")
    val prop = Properties()

    val syntaxAnalysisEnabled: Boolean
    val frequencyAnalysisEnabled: Boolean

    val phraseList: List<String> = parsePhraseList()

    val phraseListLength = phraseList.size

    var fiveGrams = parseFiveGrams()

    val phrasesPerSession = 8

    init {
        println("Singleton initiated")

        // set prop using application.properties
        FileInputStream(propertiesFile).use { inStream -> prop.load(inStream) }

        syntaxAnalysisEnabled = (this.prop.get("analyseSyntax") as String).toBoolean()
        frequencyAnalysisEnabled = (this.prop.get("analyseFrequency") as String).toBoolean()
    }

    /** get the next phrase according to the current phrase number and the session */
    fun getNextPhrase(sessionNumber: Int, phraseNumber: Int): String? {

        // ensure the phrase number does not exeed the defined count
        if (phraseNumber > Singleton.phrasesPerSession) return null

        fun getRandomList(random: Random): List<Int> {
            return List(Singleton.phrasesPerSession) {
                random.nextInt(0, Singleton.phraseListLength - 1)
            }
        }

        // generate the seed by session
        val seed = sessionNumber

        // find the corresponding phrase index
        val sessionPhraseIndexes = getRandomList(Random(seed))
        val phraseIndex = sessionPhraseIndexes.get(phraseNumber + 1)

        return Singleton.phraseList.get(phraseIndex)
    }

    /** get the key-pair stored against a key */
    fun getKeyPair(key: Key): KeyPair? {
        return keyPairs.get(key)
    }

    /** check if a word exists in the word-set */
    fun wordExists(word: String): Boolean {
        return wordSet.contains(word)
    }

    /** get a random phrase */
    fun getRandomPhrase(): String {
        return phraseList.random()
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
