package tmcowley.appserver

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import tmcowley.appserver.controllers.apis.Get
import kotlin.test.assertNotNull

/** Test class for automated evaluation */
@SpringBootTest
@Tags(
    Tag("slow")
)
@Disabled
internal class EvaluationTests {

    private val getAPIs: Get = Get()
    private val phrases = Singleton.phrases
    private val words = Singleton.words

    // word to word-matches lookup
    private val matchLookup: Map<String, Set<String>>

    init {
        // create word to word-matches hash-map
        println("Creating word to word-matches hash-map")

        matchLookup = buildMap(words.size) {
            words.forEachIndexed { index, word ->
                // progress metre
                if (index % 5_000 == 0) println("\rProgress: ${(index * 100) / words.size}%")

                // get word matches by building and reading down cartesian-product tree
                // then filtering against the dictionary
                val matches = getWords(word)
                put(word, matches)
            }
        }
    }

    @Test
    fun `rank words by match count`() {
        println("\nTesting: evaluation of words by match count started")

        // generate list of words sorted by match count
        val wordsMutable = words.toMutableList()

        // filter out non-mapped words
        wordsMutable.filter { word -> word in matchLookup }

        // words sorted by length (asc.), then match count (desc.)
        wordsMutable.sortBy { word -> word.length }
        wordsMutable.sortByDescending { word ->
            val matches = matchLookup[word]
            assertNotNull(matches)
            matches.size
        }

        // get top words by match count
        val topMatchingWords = buildList {
            wordsMutable
                .take(200)
                .distinctBy { word -> matchLookup[word] }
                .forEach { word ->
                    val matches = matchLookup[word]
                    assertNotNull(matches)
                    add(matches)
                }
        }

        println("Top 20 word sets by match count: ")
        topMatchingWords
            .take(20)
            .forEachIndexed { index, wordSet ->
                val wordSetAsString = wordSet.joinToString(prefix = "{ ", postfix = " }")
                println("Position ${index + 1}: \t${wordSet.size} matches for \t$wordSetAsString")
            }

        println()
    }

    @Test
    fun `find proportion of single-matching words`() {
        println("\nTesting: evaluation: finding proportion of single-matching words")

        // find proportion of words with a single match
        val singleMatchingWords = words.count { word ->
            val matches = matchLookup[word]
            assertNotNull(matches)
            matches.size == 1
        }
        val proportionOfSingleMatchingWords = (singleMatchingWords * 100) / words.size
        println("Proportion of single-matching words: ${proportionOfSingleMatchingWords}%")
        println()
    }

    @Test
    fun `eval algorithm with phrase set`() {
        println("\nTesting: evaluation of algorithm accuracy started")

        // count matches for each level: first, in top 3, in top 5, found
        val phraseCount = phrases.size
        val matchedAsTop = phrases.count { phrase ->
            getAPIs.submit(phrase).indexOf(phrase.lowercase()) == 0
        }
        val matchedInTop3 = phrases.count { phrase ->
            getAPIs.submit(phrase).indexOf(phrase.lowercase()) < 3
        }
        val matchedInTop5 = phrases.count { phrase ->
            getAPIs.submit(phrase).indexOf(phrase.lowercase()) < 5
        }
        val matched = phrases.count { phrase ->
            phrase.lowercase() in getAPIs.submit(phrase)
        }

        // output results
        println()
        println("Notice: Automatic evaluation complete: ")
        println("Matched in the top 1 results: ${(matchedAsTop * 100) / phraseCount}% [$matchedAsTop/$phraseCount]")
        println("Matched in the top 3 results: ${(matchedInTop3 * 100) / phraseCount}% [$matchedInTop3/$phraseCount]")
        println("Matched in the top 5 results: ${(matchedInTop5 * 100) / phraseCount}% [$matchedInTop5/$phraseCount]")
        println("Matched: ${(matched * 100) / phraseCount}% [$matched/$phraseCount]")
        println()

        // check if any phrases have not been matched
        val anyUnmatched = matched != phraseCount

        // check for non-matched phrases
        if (anyUnmatched) phrases
            .filter { phrase ->
                phrase.lowercase() !in getAPIs.submit(phrase)
            }
            .forEach { phrase ->
                println("Non-matched phrase found: $phrase")
            }

        // ensure all phrases were matched
        assertThat(anyUnmatched).isFalse
    }
}
