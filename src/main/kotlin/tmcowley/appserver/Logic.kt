package tmcowley.appserver

import tmcowley.appserver.models.Key
import tmcowley.appserver.models.KeyPair
import tmcowley.appserver.models.KeyboardSide
import tmcowley.appserver.structures.SentenceTree
import tmcowley.appserver.structures.WordTree
import tmcowley.appserver.utils.getFrequencyScore
import kotlin.test.assertNotNull

/** submit a sentence to turn an input phrase into an array of matched phrases */
fun submitSentence(sentence: String): List<String> {
    // compute the matching sentences
    val sentences = getMatchingSentences(sentence.lowercase()).toMutableList()

    if (sentences.isEmpty()) println("Notice: no results found")
    // else println("input: $sentence, output: $sentences")

    // rank sentences based on word frequency, syntax analysis (if enabled)
    rankSentences(sentences)

    return sentences
}

/** get the matching sentences set from a list of matching words */
private fun getSentences(listOfMatchedWords: List<Set<String>>): Set<String> {
    if (listOfMatchedWords.isEmpty()) return setOf()

    // generate sentence permutation tree
    val sentenceTree = SentenceTree()
    listOfMatchedWords.forEach { matchingWordSet -> sentenceTree.insert(matchingWordSet) }

    return sentenceTree.getSentences()
}

/** get the matching words from a word in key-pair list form */
fun getMatchedWords(currentWord: List<KeyPair>): Set<String> {
    if (currentWord.isEmpty()) return setOf()

    // generate word permutation tree (representing key-pair form)
    val currentWordTree = WordTree()
    currentWord.forEach { keyPair -> currentWordTree.insertKeyPair(keyPair) }

    // find matched words by traversing the tree paths
    var wordMatches = currentWordTree.getWords()

    // filter out words not in the dictionary
    wordMatches = wordMatches.filter { word -> Singleton.wordExists(word) }.toSet()

    return wordMatches
}

/** rank sentences based on frequency, syntax (if enabled) */
private fun rankSentences(sentences: MutableList<String>) {
    // syntax analysis enabled -> rank according to syntax correctness (lower better)
    if (Singleton.syntaxAnalysisEnabled) sentences.sortBy { resultingSentence ->
        Singleton.langTool.countErrors(resultingSentence)
    }

    // frequency analysis enabled -> rank according to frequency (higher better)
    if (Singleton.frequencyAnalysisEnabled) sentences.sortByDescending { resultingSentence ->
        getFrequencyScore(resultingSentence)
    }
}

/** split a sentence into a list of non-empty words */
private fun splitIntoWords(sentence: String): List<String> {
    return sentence
        .split(" ")
        .filter { word -> word != "" }
}

/** get a word in key-pair form, e.g. "word" -> [(w, o), (w, o), (r, u), (d, k)] */
fun getWordInKeyPairForm(word: String): List<KeyPair> {
    // filter out non-alphabetic characters, and non-mapping chars
    val wordAlphabetic = word
        .filter { char -> isAlphabetic(char) }
        .filter { char -> Singleton.getKeyPairOrNull(char) != null }

    // create key-pair list for word (word as key-pairs)
    return wordAlphabetic
        .map { char ->
            val keyPair = Singleton.getKeyPairOrNull(char)
            assertNotNull(keyPair)
            keyPair
        }
}

/** get the matching words to a given word */
fun getMatchedWords(word: String): Set<String> {
    return getMatchedWords(getWordInKeyPairForm(word))

//    return getMatchedWordsUsingCartesian(getWordInKeyPairForm(word))
}

/** get resulting sentences from the input sentence */
private fun getMatchingSentences(sentence: String): Set<String> {
    // create sentence word list
    val words = splitIntoWords(sentence)

    // ensure word length is not exceeded
    val wordLengthExceeded = words.any { word -> (word.length > Singleton.maxInputLength) }
    if (wordLengthExceeded) return setOf("Maximum word length of ${Singleton.maxInputLength} exceeded")

    // create the list of matched words
    val listOfMatchedWords = buildList {
        words.forEach { word ->
            // get matching words
            var matchedWords: Set<String> = getMatchedWords(word)

            // numbers should not be in non-matched form
            // non-numbers should be in non-matched form (e.g. {<word>})
            if (matchedWords.isEmpty()) {
                matchedWords = if (isNumber(word)) setOf(word) else setOf("{$word}")
            }

            add(matchedWords)
        }
    }

    // no results found
    if (listOfMatchedWords.isEmpty()) println("Notice: $sentence returned zero matches")

    // compute viable sentences
    return getSentences(listOfMatchedWords)
//    return getSentencesUsingCartesian(listOfMatchedWords)
}

/** check if the given string is a number */
private fun isNumber(str: String): Boolean {
    return (str.toDoubleOrNull() != null)
}

/** check if a character is in the alphabet */
private fun isAlphabetic(char: Char): Boolean {
    return (char in ('a' .. 'z') || char in ('A' .. 'Z'))
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
