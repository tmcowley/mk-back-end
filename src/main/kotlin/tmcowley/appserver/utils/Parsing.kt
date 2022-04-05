package tmcowley.appserver.utils

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.File

/** create a List containing phrases */
fun getPhrases(): List<String> {
    val path = "./resources/phrase-list.txt"
    return getPhrases(path)
}

/** create a List containing phrases from a phrase file */
internal fun getPhrases(path: String): List<String> {
    val phraseSet = buildList {
        try {
            File(path).forEachLine { phrase ->
                add(phrase)
            }
        } catch (e: java.io.FileNotFoundException) {
            // handler
            println("Error: file: $path not found.")
            throw e
        }
    }

    return phraseSet
}

/** create a word-frequency map */
fun getWordFrequencies(): Map<String, Int> {
    val path = "./resources/word-frequencies.csv"
    return getWordFrequencies(path)
}

/** create a word-frequency map */
internal fun getWordFrequencies(path: String): Map<String, Int> {
    val wordFreqLookup = buildMap {
        // parse frequency csv
        try {
            csvReader().open(path) {
                readAllWithHeaderAsSequence().forEach { row: Map<String, String> ->
                    // add word and frequency to lookup
                    val word = row["Word"]!!.lowercase()
                    val wordCount = row["FREQcount"]!!.toInt()

                    put(word, wordCount)
                }
            }
        } catch (e: java.io.FileNotFoundException) {
            // handler
            println("Error: file: $path not found.")
            throw e
        }
    }

    return wordFreqLookup
}

/**
 * create a hashset of words from our dictionary (word-list.txt) and phrase list (phrase-list.txt)
 */
fun getWords(): Set<String> {
    val path = "./resources/word-list.txt"
    return getWords(path)
}

/**
 * create a hashset of words from our dictionary (word-list.txt) and phrase list (phrase-list.txt)
 */
internal fun getWords(path: String): Set<String> {
    val allWords = buildSet {
        // add words from word list
        try {
            File(path).forEachLine { word ->
                add(word.lowercase())
            }
        } catch (e: java.io.FileNotFoundException) {
            // handler
            println("Error: file: $path not found.")
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
fun getFiveGrams(): List<String> {
    val path = "./resources/word-list-5-grams.txt"
    return getFiveGrams(path)
}

/** create a list for each word in the list of 5-length words (word-list-5-grams.txt) */
internal fun getFiveGrams(path: String): List<String> {
    val fiveGrams = buildList {
        try {
            File(path).forEachLine { fiveGram ->
                add(fiveGram.lowercase())
            }
        } catch (e: java.io.FileNotFoundException) {
            // handler
            println("Error: file: $path not found.")
            throw e
        }
    }

    return fiveGrams
}
