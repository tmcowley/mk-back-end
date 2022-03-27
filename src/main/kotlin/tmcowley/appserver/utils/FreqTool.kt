package tmcowley.appserver.utils

import tmcowley.appserver.Singleton

fun getFrequencyScore(sentence: String): Int {

    // split sentence into list of words
    val words: List<String> = sentence.split("\\s+".toRegex()).map { word ->
        word.replace("""^[,.]|[,.]$""".toRegex(), "")
    }

    // calculate the sentence frequency score
    var sentenceScore = 0
    words.forEach { word -> sentenceScore += Singleton.getWordFrequency(word) }

    return sentenceScore
}
