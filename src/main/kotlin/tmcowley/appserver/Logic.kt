package tmcowley.appserver

import tmcowley.appserver.models.Key
import tmcowley.appserver.models.KeyPair
import tmcowley.appserver.utils.getFrequencyScore
import tmcowley.appserver.structures.getSentences
import tmcowley.appserver.structures.getMatchedWords

private enum class Form {
    LEFT,
    RIGHT
}

/** submit a sentence to turn an input phrase into an array of matched phrases */
fun submitSentence(sentence: String): Array<String> {

    val lowercaseInput = sentence.lowercase()

    // compute the matching sentences
    val resultingSentences: MutableList<String> = getMatchingSentences(lowercaseInput)
    // println("input: ${sentence}, output: ${resultingSentences.toString()}")
    if (resultingSentences.isEmpty()) println("Notice: no results found")

    // syntax analysis enabled -> rank according to syntax correctness (lower better)
    if (Singleton.syntaxAnalysisEnabled) resultingSentences.sortBy { resultingSentence ->
        Singleton.langTool.countErrors(resultingSentence)
    }

    // frequency analysis enabled -> rank according to frequency (higher better)
    if (Singleton.frequencyAnalysisEnabled) resultingSentences.sortByDescending { resultingSentence ->
        getFrequencyScore(resultingSentence)
    }

    return resultingSentences.toTypedArray()
}

/** split a string into a word array */
fun splitIntoWords(sentence: String): Array<String> {
    return sentence.split(" ").toTypedArray()
}

fun getWordInKeyPairForm(word: String): MutableList<KeyPair> {
    // create key-pair list for word
    val wordKeyPairs: MutableList<KeyPair> = mutableListOf()
    for (char: Char in word) {

        // char is non-alphabetic -> do nothing
        if (isNotAlphabetic(char)) continue

        // char is alphabetic

        // get key-pair for key with char
        val keyPair = Singleton.getKeyPairOrNull(char) ?: continue

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
    val nonEmptyWords = words.filterNot { word -> word == "" }

    // ensure word length is not exceeded
    val wordLengthExceeded = nonEmptyWords.any { word -> (word.length >= 25) }
    if (wordLengthExceeded) return mutableListOf()

    // create the list of matched words
    val listOfMatchedWords: MutableList<MutableList<String>> = mutableListOf()
    nonEmptyWords.forEach { word ->

        var matchedWords: MutableList<String> = getMatchedWords(word)

        if (matchedWords.isEmpty()) {
            val isNumber = (word.toDoubleOrNull() != null)

            // numbers should not be in non-matched form
            // non-numbers should be in non-matched form (e.g. {<word>})
            matchedWords = if (isNumber) mutableListOf(word) else mutableListOf("{${word}}")
        }

        // println("word: ${word}, matchedWords: ${matchedWords.toString()}")

        // add the viable words to the total list
        listOfMatchedWords.add(matchedWords)
    }

    // no words have been computed
    // if (listOfMatchedWords.isEmpty()) System.out.println("Notice: listOfMatchedWords is empty")
    if (listOfMatchedWords.isEmpty()) return mutableListOf()

    // compute viable sentences from text array
    val resultingSentences = getSentences(listOfMatchedWords)

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
    input ?: return ""

    val inputForm = (input.map { char ->
        if (isNotAlphabetic(char)) char
        else (
                when (form) {
                    Form.LEFT -> (Singleton.getKeyPairOrNull(Key(char)) ?: KeyPair(char, char)).leftKey.character
                    Form.RIGHT -> (Singleton.getKeyPairOrNull(Key(char)) ?: KeyPair(char, char)).rightKey.character
                }
                )
    }.joinToString(separator = ""))

    return inputForm
}
