package tmcowley.appserver.utils

import java.io.File;

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import kotlin.text.toIntOrNull

fun parsePhraseList(): List<String> {
    val path = "./resources/phrases2.txt";

    var phraseSet = mutableListOf<String>()

    try{
        File(path).forEachLine { 
            phraseSet.add(it);
        }
    } catch (e: java.io.FileNotFoundException) {
        // handler
        println("Error: file: ${path} not found.");
    }

    return phraseSet
}

fun parseWordFrequencies(): HashMap<String, Int> {
    val path = "./resources/word-frequencies.csv";

    var wordFreqLookup = HashMap<String, Int>()

    try{
        // parse csv
        csvReader().open(path) {
            readAllWithHeaderAsSequence().forEach { row: Map<String, String> ->
                // add word and frequency to lookup
                wordFreqLookup.put(row.get("Word")!!.lowercase(), row.get("FREQcount")!!.toInt())
            }
        }
    } catch (e: java.io.FileNotFoundException) {
        // handler
        println("Error: file: ${path} not found.");
    }

    return wordFreqLookup
}

fun parseWordList(): HashSet<String> {

    val path = "./resources/words.txt";

    var allWords = hashSetOf<String>();

    // add words from words.txt
    run {
        try{
            File(path).forEachLine { 
                allWords.add(it);
            }
        } catch (e: java.io.FileNotFoundException) {
            // handler
            println("Error: file: ${path} not found.");
        }
    }

    // TODO: debug (names, places not added to dictionary (e.g. "canada", "mary"))
    // maybe the phrase list is empty? 

    // add words from phrase list
    run {
        val phraseList: List<String> = parsePhraseList();
        for (phrase: String in phraseList) {
            for (word: String in phrase.split(" ")) {
                allWords.add(word.lowercase());
            }
        }
    }

    return allWords;
}
