package tmcowley.appserver.utils

import tmcowley.appserver.Singleton
import tmcowley.appserver.models.KeyPair
import kotlin.test.assertNotNull

// -----
// utility methods
// -----

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

/** split a sentence into a list of non-empty words */
fun splitIntoWords(sentence: String): List<String> {
    return sentence
        .split(" ")
        .filter { word -> word != "" }
}

/** check if the given string is a number */
fun isNumber(str: String): Boolean {
    return (str.toDoubleOrNull() != null)
}

/** check if a character is in the alphabet */
fun isAlphabetic(char: Char): Boolean {
    return (char in ('a' .. 'z') || char in ('A' .. 'Z'))
}

/** check if a character is not alphabetic */
fun isNotAlphabetic(char: Char): Boolean {
    return (!isAlphabetic(char))
}
