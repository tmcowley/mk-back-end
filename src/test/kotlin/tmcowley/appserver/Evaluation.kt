package tmcowley.appserver

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import tmcowley.appserver.controllers.APIsGet

/** Test class for automated evaluation */
@Disabled
@Tags(
    Tag("slow")
)
@SpringBootTest
internal class Evaluation {

    private val getAPIs: APIsGet = APIsGet()
    private val phrases = Singleton.phrases
    private val wordsAsList = Singleton.words
    private val words = Singleton.words.toMutableList()

    // word to word-matches lookup
    private val matchLookup: Map<String, List<String>>

    init {
        // create word to word-matches hash-map
        println("Creating word to word-matches hash-map")

        matchLookup = buildMap(wordsAsList.size) {
            wordsAsList.forEach { word ->
                put(word, getMatchedWords(word))
            }
        }
    }

    @Test
    fun `rank words by match count`() {
        println("\nTesting: evaluation of words by match count started")

        // generate list of words sorted by match count
        words.sortBy { word -> word.length }
        words.sortByDescending { word -> matchLookup[word]?.size }

        // get top 20 words by match count
        println("Top 20 words by match count: ")
        words.take(20).forEachIndexed { index, word ->
            val matchedWords = matchLookup[word] ?: return@forEachIndexed
            println("Position ${index + 1}: $word matching ${matchedWords.size}: $matchedWords")
        }
        println()
    }

    @Test
    fun `find proportion of single-matching words`() {
        println("\nTesting: evaluation: finding proportion of single-matching words")

        // find proportion of words with a single match
        val singleMatchingWords = words.count { word ->
            (matchLookup[word]?.size == 1)
        }
        val proportionOfSingleMatchingWords = ((singleMatchingWords * 100) / words.size)
        println("Proportion of single-matching words: ${proportionOfSingleMatchingWords}%")
        println()
    }

    @Test
    fun `eval algorithm with phrase set`() {
        println("\nTesting: evaluation of algorithm accuracy started")

        // count matches for each level: first, in top 3, in top 5, found
        val phraseCount = phrases.size
        val matchedAsTop = phrases.count { phrase ->
            (getAPIs.submit(phrase).indexOf(phrase.lowercase()) == 0)
        }
        val matchedInTop3 = phrases.count { phrase ->
            (getAPIs.submit(phrase).indexOf(phrase.lowercase()) < 3)
        }
        val matchedInTop5 = phrases.count { phrase ->
            (getAPIs.submit(phrase).indexOf(phrase.lowercase()) < 5)
        }
        val matched = phrases.count { phrase ->
            (getAPIs.submit(phrase).contains(phrase.lowercase()))
        }

        // output results
        println()
        println("Notice: Automatic evaluation complete: ")
        println("Matched in the top 1 results: ${(matchedAsTop * 100) / phraseCount}% [$matchedAsTop/$phraseCount]")
        println("Matched in the top 3 results: ${(matchedInTop3 * 100) / phraseCount}% [$matchedInTop3/$phraseCount]")
        println("Matched in the top 5 results: ${(matchedInTop5 * 100) / phraseCount}% [$matchedInTop5/$phraseCount]")
        println("Matched: ${(matched * 100) / phraseCount}% [$matched/$phraseCount]")
        println()

        // check for non-matched phrases
        phrases
            .filter { phrase ->
                (!getAPIs.submit(phrase).contains(phrase.lowercase()))
            }
            .forEach { phrase ->
                println("Non-matched phrase found: $phrase")
            }
    }
}
