package tmcowley.appserver

import tmcowley.appserver.Singleton
import tmcowley.appserver.objects.Key
import tmcowley.appserver.objects.KeyPair
import tmcowley.appserver.structures.getSentences
import tmcowley.appserver.structures.getMatchedWords
import tmcowley.appserver.utils.FreqTool
import tmcowley.appserver.utils.LangTool

import tmcowley.appserver.utils.getFrequencyScore

enum class Form {
    LEFT,
    RIGHT
}

class Logic

/** submit a sentence to turn an input phrase into an array of matched phrases */
fun submitSentence(sentence: String): Array<String> {

    val lowercaseInput = sentence.lowercase()

    // compute the matching sentences
    var resultingSentences: MutableList<String> = getMatchingSentences(lowercaseInput)
    // println("input: ${sentence}, output: ${resultingSentences.toString()}")
    if (resultingSentences.isEmpty()) System.out.println("Notice: no results found")

    // syntax analysis enabled -> rank according to syntax correctness
    if (Singleton.syntaxAnalysisEnabled) resultingSentences.sortWith(SentenceSyntaxComparator)

    // frequency analysis enabled -> rank according to frequency
    if (Singleton.frequencyAnalysisEnabled) resultingSentences.sortBy{ 
        resultingSentence -> getFrequencyScore(resultingSentence) 
    }

    // reverse results
    resultingSentences.reverse()
    return resultingSentences.toTypedArray()
}

/** split a string into a word array */
fun splitIntoWords(sentence: String): Array<String> {
    return sentence.split(" ").toTypedArray()
}

fun getWordInKeyPairForm(word: String): MutableList<KeyPair> {
    // create key-pair list for word
    var wordKeyPairs: MutableList<KeyPair> = mutableListOf()
    for (char: Char in word) {

        // char is non-alphabetic -> do nothing
        if (isNotAlphabetic(char)) continue

        // char is alphabetic

        // get key-pair for key with char
        val key: Key = Key(char)
        val keyPair: KeyPair? = Singleton.getKeyPair(key)

        // key-pair lookup fails
        keyPair ?: println("Error: getKeyPair(${key}) failed")
        if (keyPair == null) continue

        // add the key-pair to the current word
        wordKeyPairs.add(keyPair)
    }

    return wordKeyPairs
}

/** get the matching words to a given word */
fun getMatchedWords(word: String): MutableList<String> {
    return getMatchedWords(getWordInKeyPairForm(word))
}

/** get resulting sentences from the input phrase (in word array form) */
fun getMatchingSentences(sentence: String): MutableList<String> {

    // create word array
    val words: Array<String> = splitIntoWords(sentence)
    val nonEmptyWords = words.filterNot{ word -> word == "" }

    // ensure word length is not exceeded
    val wordLengthExceeded = nonEmptyWords.any { word -> (word.length >= 25) }
    if (wordLengthExceeded) return mutableListOf()

    // create the list of matched words
    var listOfMatchedWords: MutableList<MutableList<String>> = mutableListOf()
    nonEmptyWords.forEach { word -> 

        var matchedWords: MutableList<String> = getMatchedWords(word)
        if (matchedWords.isEmpty()) matchedWords = mutableListOf("{${word}}")

        // println("word: ${word}, matchedWords: ${matchedWords.toString()}")

        // add the viable words to the total list
        listOfMatchedWords.add(matchedWords)
    }

    // no words have been computed
    // if (listOfMatchedWords.isEmpty()) System.out.println("Notice: listOfMatchedWords is empty")
    if (listOfMatchedWords.isEmpty()) return mutableListOf()

    // compute viable sentences from text array
    val resultingSentences: MutableList<String> = getSentences(listOfMatchedWords)

    return resultingSentences
}

/** check if a character is in the alphabet */
private fun isAlphabetic(char: Char): Boolean {
    return (char in ('a' until 'z') || char in ('A' until 'Z'))
}

/** check if a character is not alphabetic */
private fun isNotAlphabetic(char: Char): Boolean {
    return (!isAlphabetic(char))
}

/** convert from any form to right-hand side form */
fun convertToRight(input: String?): String {
    return convertToForm(Form.RIGHT, input)
}

/** convert from any form to left-hand side form */
fun convertToLeft(input: String?): String {
    return convertToForm(Form.LEFT, input)
}

/** convert from any form to side forms */
private fun convertToForm(form: Form, input: String?): String {
    if (input == null) return ""

    var inputForm = (input.map { char ->
        if (isNotAlphabetic(char) || Singleton.getKeyPair(Key(char)) == null) 
            char
        else (
            if (form == Form.LEFT) (Singleton.getKeyPair(Key(char))?.leftKey?.character) 
            else if (form == Form.RIGHT) (Singleton.getKeyPair(Key(char))?.rightKey?.character) 
            else char
        )
    }.joinToString(separator = ""))

    return inputForm
}

class SentenceSyntaxComparator {
companion object : Comparator<String> {
    val langtool: LangTool = Singleton.langTool
    override fun compare(first: String, second: String): Int {

        // calculate syntax correctness scores
        val firstScore: Int = langtool.countErrors(first)
        val secondScore: Int = langtool.countErrors(second)

        // println("{${first}} has syntax correctness score: ${firstScore}")
        // println("{${second}} has syntax correctness score: ${secondScore}")

        when {
            // lower score is better
            firstScore != secondScore -> return (secondScore - firstScore)
            else -> return (0)
        }
    }
}
}

// class SentenceFrequencyComparator {
// companion object : Comparator<String> {
//     val freqTool: FreqTool = Singleton.freqTool
//     override fun compare(first: String, second: String): Int {

//         // calculate syntax correctness scores
//         val firstScore: Int = getFrequencyScore(first)
//         val secondScore: Int = getFrequencyScore(second)

//         // println("{${first}} has frequency: ${firstScore}")
//         // println("{${second}} has frequency: ${secondScore}")

//         when {
//             // higher score is better
//             firstScore != secondScore -> return (firstScore - secondScore)
//             else -> return (0)
//         }
//     }
// }
// }