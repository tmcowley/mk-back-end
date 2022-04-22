package tmcowley.appserver

import tmcowley.appserver.models.KeyPair

// for computing the n-ary Cartesian product (alternative matching algorithm)
// see: https://guava.dev/releases/23.5-jre/api/docs/com/google/common/collect/Lists.html#cartesianProduct-java.util.List...-
import com.google.common.collect.Lists.cartesianProduct

/** alternative word matching algorithm using n-ary cartesian product */
fun getMatchedWordsUsingCartesianProduct(currentWord: List<KeyPair>): Set<String> {
    if (currentWord.isEmpty()) return setOf()

    // compute cartesian product, make distinct, filter in word-list
    return cartesianProduct(
        // create word in (key-pair as list) form:
        // "word" -> [['w', 'o'], ['w', 'o'], ['r', 'u'], ['d', 'k']]
        currentWord.map { keyPair -> keyPair.toList() }
    )
        .distinct()
        .map { path -> path.joinToString("") }
        .filter { word -> Singleton.wordExists(word) }
        .toSet()
}

/** alternative sentence matching algorithm using n-ary cartesian product */
fun getSentencesUsingCartesianProduct(listOfMatchedWords: List<Set<String>>): Set<String> {
    if (listOfMatchedWords.isEmpty()) return setOf()

    // make set of possible sentences: compute cartesian product, make distinct
    return cartesianProduct(
        listOfMatchedWords.map { set -> set.toList() }
    )
        .map { path -> path.joinToString(" ") }
        .distinct()
        .toSet()
}
