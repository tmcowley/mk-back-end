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
    val phraseSet = mutableListOf<String>()

    try {
        File(path).forEachLine { phrase -> phraseSet.add(phrase) }
    } catch (e: java.io.FileNotFoundException) {
        // handler
        println("Error: file: $path not found.")
        throw e
    }

    return phraseSet
}

/** create a word-frequency map */
fun getWordFrequencies(): HashMap<String, Int> {
    val path = "./resources/word-frequencies.csv"
    return getWordFrequencies(path)
}

/** create a word-frequency map */
internal fun getWordFrequencies(path: String): HashMap<String, Int> {
    val wordFreqLookup = HashMap<String, Int>()

    // parse frequency csv
    try {
        csvReader().open(path) {
            readAllWithHeaderAsSequence().forEach { row: Map<String, String> ->
                // add word and frequency to lookup
                wordFreqLookup[row["Word"]!!.lowercase()] = row["FREQcount"]!!.toInt()
            }
        }
    } catch (e: java.io.FileNotFoundException) {
        // handler
        println("Error: file: $path not found.")
        throw e
    }

    return wordFreqLookup
}

/**
 * create a hashset of words from our dictionary (word-list.txt) and phrase list (phrase-list.txt)
 */
fun getWords(): HashSet<String> {
    val path = "./resources/word-list.txt"
    return getWords(path)
}

/**
 * create a hashset of words from our dictionary (word-list.txt) and phrase list (phrase-list.txt)
 */
internal fun getWords(path: String): HashSet<String> {
    val allWords = hashSetOf<String>()

    // add words from words.txt
    run {
        try {
            File(path).forEachLine { word -> allWords.add(word.lowercase()) }
        } catch (e: java.io.FileNotFoundException) {
            // handler
            println("Error: file: $path not found.")
            throw e
        }
    }

    // add words from phrase list
    run {
        val phraseList: List<String> = getPhrases()
        for (phrase: String in phraseList) {
            for (word: String in phrase.split(" ")) {
                allWords.add(word.lowercase())
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
    val fiveGrams = mutableListOf<String>()

    try {
        File(path).forEachLine { fiveGram -> fiveGrams.add(fiveGram.lowercase()) }
    } catch (e: java.io.FileNotFoundException) {
        // handler
        println("Error: file: $path not found.")
        throw e
    }

    return fiveGrams
}
