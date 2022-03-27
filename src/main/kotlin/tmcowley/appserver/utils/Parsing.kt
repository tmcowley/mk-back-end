package tmcowley.appserver.utils

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.File

/** create a List containing phrases */
fun parsePhrases(): List<String> {
    val path = "./resources/phrase-list.txt"

    val phraseSet = mutableListOf<String>()

    try {
        File(path).forEachLine { phrase -> phraseSet.add(phrase) }
    } catch (e: java.io.FileNotFoundException) {
        // handler
        println("Error: file: $path not found.")
    }

    return phraseSet
}

/** create a word-frequency map */
fun parseWordFrequencies(): HashMap<String, Int> {
    val path = "./resources/word-frequencies.csv"

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
    }

    return wordFreqLookup
}

/**
 * create a hashset of words from our dictionary (word-list.txt) and phrase list (phrase-list.txt)
 */
fun parseWords(): HashSet<String> {

    val path = "./resources/word-list.txt"

    val allWords = hashSetOf<String>()

    // add words from words.txt
    run {
        try {
            File(path).forEachLine { word -> allWords.add(word.lowercase()) }
        } catch (e: java.io.FileNotFoundException) {
            // handler
            println("Error: file: $path not found.")
        }
    }

    // add words from phrase list
    run {
        val phraseList: List<String> = parsePhrases()
        for (phrase: String in phraseList) {
            for (word: String in phrase.split(" ")) {
                allWords.add(word.lowercase())
            }
        }
    }

    return allWords
}

/** create a list for each word in the list of 5-length words (word-list-5-grams.txt) */
fun parseFiveGrams(): List<String> {
    val path = "./resources/word-list-5-grams.txt"

    val fiveGrams = mutableListOf<String>()

    try {
        File(path).forEachLine { fiveGram -> fiveGrams.add(fiveGram.lowercase()) }
    } catch (e: java.io.FileNotFoundException) {
        // handler
        println("Error: file: $path not found.")
    }

    return fiveGrams
}
