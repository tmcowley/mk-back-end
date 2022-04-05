package tmcowley.appserver

import tmcowley.appserver.models.Key
import tmcowley.appserver.models.KeyPair
import tmcowley.appserver.models.KeyboardSide
import tmcowley.appserver.utils.getFrequencyScore
import tmcowley.appserver.structures.getSentences
import tmcowley.appserver.structures.getMatchedWords

/** submit a sentence to turn an input phrase into an array of matched phrases */
fun submitSentence(sentence: String): Array<String> {
    // compute the matching sentences
    val sentences = getMatchingSentences(sentence.lowercase()).toMutableList()

    if (sentences.isEmpty()) println("Notice: no results found")
    // if (sentences.isNotEmpty()) println("input: ${sentence}, output: ${sentences.toString()}")

    rankSentences(sentences)

    return sentences.toTypedArray()
}

/** rank sentences based on frequency, syntax (if enabled) */
fun rankSentences(sentences: MutableList<String>) {
    // syntax analysis enabled -> rank according to syntax correctness (lower better)
    if (Singleton.syntaxAnalysisEnabled) sentences.sortBy { resultingSentence ->
        Singleton.langTool.countErrors(resultingSentence)
    }

    // frequency analysis enabled -> rank according to frequency (higher better)
    if (Singleton.frequencyAnalysisEnabled) sentences.sortByDescending { resultingSentence ->
        getFrequencyScore(resultingSentence)
    }
}

/** split a string into a word array */
fun splitIntoWords(sentence: String): Array<String> {
    return sentence
        .split(" ")
        .toTypedArray()
}

/** get a word in key-pair form, e.g. "word" -> [(w, o), (w, o), (r, u), (d, k)] */
fun getWordInKeyPairForm(word: String): List<KeyPair> {
    // filter out non-alphabetic characters, and non-mapping chars
    val wordAlphabetic = word
        .filter { char -> isAlphabetic(char) }
        .filter { char -> Singleton.getKeyPairOrNull(char) != null }

    // create key-pair list for word
    @Suppress("UnnecessaryVariable")
    val wordAsKeyPairs = wordAlphabetic
        .map { char -> Singleton.getKeyPairOrNull(char)!! }

    return wordAsKeyPairs
}

/** get the matching words to a given word */
fun getMatchedWords(word: String): List<String> {
    return getMatchedWords(getWordInKeyPairForm(word))
}

/** get resulting sentences from the input phrase (in word array form) */
fun getMatchingSentences(sentence: String): List<String> {

    // create word array
    val words: Array<String> = splitIntoWords(sentence)
    val nonEmptyWords = words.filter { word -> word != "" }

    // ensure word length is not exceeded
    val wordLengthExceeded = nonEmptyWords.any { word -> (word.length >= 25) }
    if (wordLengthExceeded) return mutableListOf()

    // create the list of matched words
    val listOfMatchedWords = buildList {
        nonEmptyWords.forEach { word ->

            var matchedWords: List<String> = getMatchedWords(word)

            if (matchedWords.isEmpty()) {
                val isNumber = (word.toDoubleOrNull() != null)

                // numbers should not be in non-matched form
                // non-numbers should be in non-matched form (e.g. {<word>})
                matchedWords = if (isNumber) listOf(word) else listOf("{${word}}")
            }

            // println("word: ${word}, matchedWords: ${matchedWords.toString()}")

            // add the viable words to the total list
            add(matchedWords)
        }
    }

    // no words have been computed
    // if (listOfMatchedWords.isEmpty()) System.out.println("Notice: listOfMatchedWords is empty")
    if (listOfMatchedWords.isEmpty()) return listOf()

    // compute viable sentences from text array
    return getSentences(listOfMatchedWords)
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
    return convertToForm(KeyboardSide.RIGHT, input)
}

/** convert from any form to left-hand side form */
fun convertToLeft(input: String?): String {
    return convertToForm(KeyboardSide.LEFT, input)
}

/** convert from any form to side forms */
private fun convertToForm(form: KeyboardSide, input: String?): String {
    input ?: return ""

    val inputForm = input
        .map { char ->
            if (isNotAlphabetic(char))
                char
            else
                when (form) {
                    KeyboardSide.LEFT -> (Singleton.getKeyPairOrNull(Key(char)) ?: KeyPair(char, char)).leftKey.character
                    KeyboardSide.RIGHT -> (Singleton.getKeyPairOrNull(Key(char)) ?: KeyPair(char, char)).rightKey.character
                }
        }
        .joinToString(separator = "")

    return inputForm
}
