package tmcowley.appserver

import tmcowley.appserver.structures.SentenceTree
import tmcowley.appserver.structures.WordTree
import tmcowley.appserver.utils.getFrequencyScore
import tmcowley.appserver.utils.splitIntoWords
import tmcowley.appserver.utils.isNumber

/** submit a sentence to turn an input phrase into an array of matched phrases */
fun submitSentence(sentence: String): List<String> {
    // compute the matching sentences
    val listOfMatchedWords = createListOfMatchedWords(sentence.lowercase()).toMutableList()

    // compute viable sentences
    val sentences = SentenceTree().getSentences(listOfMatchedWords).toMutableList()
    // val sentences = getSentencesUsingCartesian(listOfMatchedWords)

    if (sentences.isEmpty()) println("Notice: no results found")

    // rank sentences based on word frequency, syntax analysis (if enabled)
    rankSentences(sentences)

    return sentences
}

/** get resulting sentences from the input sentence */
private fun createListOfMatchedWords(sentence: String): List<Set<String>> {
    // create sentence word list
    val words = splitIntoWords(sentence)

    // ensure word length is not exceeded
    val wordLengthExceeded = words.any { word -> (word.length > Singleton.maxInputLength) }
    if (wordLengthExceeded) return listOf(setOf("Maximum word length of ${Singleton.maxInputLength} exceeded"))

    // create the list of matched words
    val listOfMatchedWords = buildList {
        words.forEach { word ->
            var matchedWords = getWords(word)

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

    return listOfMatchedWords
}

/** get the matching words from a word in key-pair list form */
fun getWords(word: String): Set<String> =
    // generate word cartesian-product tree
    WordTree()
        // find matched words by traversing the tree paths
        .getWords(word)
        // filter out words not in the dictionary
        .filter { matchedWord ->
            Singleton.wordExists(matchedWord)
        }
        .toSet()

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
