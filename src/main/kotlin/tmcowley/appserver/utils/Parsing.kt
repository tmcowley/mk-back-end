package tmcowley.appserver.utils

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import tmcowley.appserver.Singleton
import tmcowley.appserver.models.Key
import tmcowley.appserver.models.KeyPair
import java.io.File
import kotlin.test.assertNotNull

/** create a List containing phrases */
fun getPhrases(): List<String> {
    val file = "phrase-list.txt"
    val filePath = Singleton.stringDataPath + file

    return getPhrases(filePath)
}

/** create a word-frequency map */
fun getWordFrequencies(): Map<String, Int> {
    val file = "word-frequencies.csv"
    val filePath = Singleton.stringDataPath + file

    return getWordFrequencies(filePath)
}

/**
 * create a hashset of words from our dictionary (word-list.txt) and phrase list (phrase-list.txt)
 */
fun getWords(): Set<String> {
    val file = "word-list.txt"
    val filePath = Singleton.stringDataPath + file

    return getWords(filePath)
}

/** create a list for each word in the list of 5-length words (word-list-5-grams.txt) */
fun getFiveGrams(): List<String> {
    val file = "word-list-5-grams.txt"
    val filePath = Singleton.stringDataPath + file

    return getFiveGrams(filePath)
}

/** create a List containing phrases from a phrase file */
internal fun getPhrases(filePath: String): List<String> {
    val phraseSet = buildList {
        try {
            File(filePath).forEachLine { phrase ->
                add(phrase)
            }
        } catch (e: java.io.FileNotFoundException) {
            // handler
            println("Error: file: $filePath not found.")
            throw e
        }
    }

    return phraseSet
}

/** create a word-frequency map */
internal fun getWordFrequencies(filePath: String): Map<String, Int> {
    val wordFreqLookup = buildMap {
        // parse frequency csv
        try {
            csvReader().open(filePath) {
                readAllWithHeaderAsSequence().forEach { row: Map<String, String> ->
                    // add word and frequency to lookup
                    val wordRow = row["Word"]
                    assertNotNull(wordRow)
                    val word = wordRow.lowercase()

                    val frequencyRow = row["FREQcount"]
                    assertNotNull(frequencyRow)
                    val wordCount = frequencyRow.toInt()

                    put(word, wordCount)
                }
            }
        } catch (e: java.io.FileNotFoundException) {
            // handler
            println("Error: file: $filePath not found.")
            throw e
        }
    }

    return wordFreqLookup
}

/** create a hashset of words from our dictionary (word-list.txt) and phrase list (phrase-list.txt) */
internal fun getWords(filePath: String): Set<String> {
    val allWords = buildSet {
        // add words from word list
        try {
            File(filePath).forEachLine { word ->
                add(word.lowercase())
            }
        } catch (e: java.io.FileNotFoundException) {
            // handler
            println("Error: file: $filePath not found.")
            throw e
        }

        // add words from phrase list
        val phraseList: List<String> = getPhrases()
        for (phrase: String in phraseList) {
            for (word: String in phrase.split(" ")) {
                add(word.lowercase())
            }
        }
    }

    return allWords
}

/** create a list for each word in the list of 5-length words (word-list-5-grams.txt) */
internal fun getFiveGrams(filePath: String): List<String> {
    val fiveGrams = buildList {
        try {
            File(filePath).forEachLine { fiveGram ->
                add(fiveGram.lowercase())
            }
        } catch (e: java.io.FileNotFoundException) {
            // handler
            println("Error: file: $filePath not found.")
            throw e
        }
    }

    return fiveGrams
}

/** get the map linking keys to key-pairs */
fun getKeyPairMap(): Map<Key, KeyPair> {

    val topRowMapping = listOf(
        // top row: left half
        Pair(Key('q'), KeyPair('q', 'p')),
        Pair(Key('w'), KeyPair('w', 'o')),
        Pair(Key('e'), KeyPair('e', 'i')),
        Pair(Key('r'), KeyPair('r', 'u')),
        Pair(Key('t'), KeyPair('t', 'y')),

        // top row: right half
        Pair(Key('p'), KeyPair('q', 'p')),
        Pair(Key('o'), KeyPair('w', 'o')),
        Pair(Key('i'), KeyPair('e', 'i')),
        Pair(Key('u'), KeyPair('r', 'u')),
        Pair(Key('y'), KeyPair('t', 'y'))
    )

    val middleRowMapping = listOf(
        // middle row: left half
        Pair(Key('a'), KeyPair('a', 'a')),
        Pair(Key('s'), KeyPair('s', 'l')),
        Pair(Key('d'), KeyPair('d', 'k')),
        Pair(Key('f'), KeyPair('f', 'j')),
        Pair(Key('g'), KeyPair('g', 'h')),

        // middle row: right half
        Pair(Key('a'), KeyPair('a', 'a')),
        Pair(Key('l'), KeyPair('s', 'l')),
        Pair(Key('k'), KeyPair('d', 'k')),
        Pair(Key('j'), KeyPair('f', 'j')),
        Pair(Key('h'), KeyPair('g', 'h'))
    )

    val bottomRowMapping = listOf(
        // bottom row: left half
        Pair(Key('z'), KeyPair('z', 'z')),
        Pair(Key('x'), KeyPair('x', 'x')),
        Pair(Key('c'), KeyPair('c', 'c')),
        Pair(Key('v'), KeyPair('v', 'm')),
        Pair(Key('b'), KeyPair('b', 'n')),

        // bottom row: right half
        Pair(Key('z'), KeyPair('z', 'z')),
        Pair(Key('x'), KeyPair('x', 'x')),
        Pair(Key('c'), KeyPair('c', 'c')),
        Pair(Key('m'), KeyPair('v', 'm')),
        Pair(Key('n'), KeyPair('b', 'n'))
    )

    // define key -> key-pair map
    val keyPairs: Map<Key, KeyPair> = buildMap {
        // map top row
        topRowMapping.forEach { pairMap ->
            val key = pairMap.first
            val keyPair = pairMap.second
            put(key, keyPair)
        }

        // map middle row
        middleRowMapping.forEach { pairMap ->
            val key = pairMap.first
            val keyPair = pairMap.second
            put(key, keyPair)
        }

        // map bottom row
        bottomRowMapping.forEach { pairMap ->
            val key = pairMap.first
            val keyPair = pairMap.second
            put(key, keyPair)
        }
    }

    return keyPairs
}
