package tmcowley.appserver.utils

import tmcowley.appserver.Singleton

import org.springframework.cache.annotation.Cacheable

class FreqTool {

    init {
        Singleton
    }

    @Cacheable
    fun sentence(sentence: String): Int {

        // split sentence into list of words
        val words: List<String> =
                sentence.split("\\s+".toRegex()).map { word ->
                    word.replace("""^[,\.]|[,\.]$""".toRegex(), "")
                }

        var sentenceScore: Int = 0

        for (word: String in words) {
            // get word score
            val wordScore: Int = Singleton.getWordFrequency(word);
            sentenceScore += wordScore
        }

        return sentenceScore
    }
}
