package tmcowley.appserver

import tmcowley.appserver.Singleton
import tmcowley.appserver.objects.Key
import tmcowley.appserver.objects.KeyPair
import tmcowley.appserver.structures.getSentences
import tmcowley.appserver.structures.getWords
import tmcowley.appserver.utils.FreqTool
import tmcowley.appserver.utils.LangTool

class Logic

/** submit a sentence to turn an input phrase into an array of matched phrases */
fun submitSentence(sentence: String): Array<String> {

    val syntaxAnalysisEnabled = Singleton.syntaxAnalysisEnabled
    val frequencyAnalysisEnabled = Singleton.frequencyAnalysisEnabled

    val lowercaseInput = sentence.lowercase()

    // compute the matching sentences
    var resultingSentences: MutableList<String> = getMatchingSentences(lowercaseInput)

    // report input and output
    // System.out.println("\nInput: ${input}")
    // System.out.println("\nResults: ")

    if (resultingSentences.isEmpty()) System.out.println("Notice: no results found")

    // syntax analysis enabled -> rank according to syntax correctness
    if (syntaxAnalysisEnabled) resultingSentences.sortWith(SentenceSyntaxComparator)

    // frequency analysis enabled -> rank according to frequency
    if (frequencyAnalysisEnabled) resultingSentences.sortWith(SentenceFrequencyComparator)

    // reverse results
    resultingSentences.reverse()

    return resultingSentences.toTypedArray()
}

/** split a string into a word array */
fun splitIntoWords(sentence: String): Array<String> {
    return sentence.split(" ").toTypedArray()
}

/** get the matching words of a given word */
fun getMatchedWords(word: String): MutableList<String> {

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
        if (keyPair == null) {
            println("Error: getKeyPair(${key.toString()}) failed")
            continue
        }

        // add the key-pair to the current word
        wordKeyPairs.add(keyPair)
    }

    return getWords(wordKeyPairs)
}

/** get resulting sentences from the input phrase (in word array form) */
fun getMatchingSentences(sentence: String): MutableList<String> {

    // create word array
    val words: Array<String> = splitIntoWords(sentence)
    val nonEmptyWords = words.filterNot{ 
        word -> word == ""
    }

    var listOfMatchedWords: MutableList<MutableList<String>> = mutableListOf()
    nonEmptyWords.forEach { word -> 

        var matchedWords: MutableList<String> = getMatchedWords(word)
        if (matchedWords.isEmpty()) {
            // println("\nwordKeyPairs: ${wordKeyPairs}")
            println("Notice: the word '${word}' was not matched")
            matchedWords = mutableListOf("{${word}}")
            // return@forEach
        }

        // add the viable words to the total list
        listOfMatchedWords.add(matchedWords)
    }

    // trees have been built, calculate viable sentences

    // no words have been computed
    if (listOfMatchedWords.isEmpty()) {
        System.out.println("Notice: listOfMatchedWords is empty")
        return mutableListOf()
    }

    // compute viable sentences from text array
    val resultingSentences: MutableList<String> = getSentences(listOfMatchedWords)

    return resultingSentences
}

/** check if a character is in the alphabet */
fun isAlphabetic(char: Char): Boolean {
    return (char in 'a'..'z' || char in 'A'..'Z')
}

/** check if a character is not alphabetic */
fun isNotAlphabetic(char: Char): Boolean {
    return (!isAlphabetic(char))
}

/** convert from full-board form to right-hand side form */
fun convertFullToRHS(input: String?): String {
    if (input == null) return ""

    var inputRHS = ""

    for (char: Char in input) {

        if (isNotAlphabetic(char)) {
            inputRHS += char
            continue
        }

        // have an alphabetic character

        val keyPair: KeyPair? = Singleton.getKeyPair(Key(char))

        if (keyPair == null) {
            inputRHS += char
            continue
        }

        val charRHS: Char = keyPair.rightKey.character

        inputRHS += charRHS

    }

    return inputRHS
}

/** convert from full-board form to left-hand side form */
fun convertFullToLHS(input: String?): String {
    if (input == null) return ""

    var inputLHS: String = ""

    for (char: Char in input) {

        if (isNotAlphabetic(char)) {
            inputLHS += char
            continue
        }

        // have an alphabetic character

        val keyPair: KeyPair? = Singleton.getKeyPair(Key(char))

        if (keyPair == null) {
            inputLHS += char
            continue
        }

        val charLHS: Char = keyPair.leftKey.character

        inputLHS += charLHS
    }

    return inputLHS
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

class SentenceFrequencyComparator {
companion object : Comparator<String> {
    val freqTool: FreqTool = Singleton.freqTool
    override fun compare(first: String, second: String): Int {

        // calculate syntax correctness scores
        val firstScore: Int = freqTool.sentence(first)
        val secondScore: Int = freqTool.sentence(second)

        // println("{${first}} has frequency: ${firstScore}")
        // println("{${second}} has frequency: ${secondScore}")

        when {
            // higher score is better
            firstScore != secondScore -> return (firstScore - secondScore)
            else -> return (0)
        }
    }
}
}