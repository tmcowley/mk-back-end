package tmcowley.appserver

import tmcowley.appserver.Singleton
import tmcowley.appserver.controllers.APIsGet
import tmcowley.appserver.getMatchedWords

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Disabled
// import org.junit.jupiter.api.Assertions
import org.springframework.boot.test.context.SpringBootTest

@Disabled
@SpringBootTest
class Evaluation {

    val getAPIs: APIsGet = APIsGet()
    val phraseList: List<String> = Singleton.phraseList
    val words = Singleton.wordSet.toMutableList()

    // word to word-matches lookup
    val matchLookup: HashMap<String, MutableList<String>>

    init{
        // create word to word-matches hash-map
        println("Creating word to word-matches hash-map")
        matchLookup = HashMap<String, MutableList<String>>()
        Singleton.wordSet.forEach {
            word -> matchLookup.put(word, getMatchedWords(word))
        }
    }

    @Test
    fun `rank words by match count`() {

        println("\nTesting: evaluation of words by match count started")

        // generate list of words sorted by match count
        words.sortBy { word -> - word.length }
        words.sortBy { word -> matchLookup.get(word)?.size }
        words.reverse()

        // get top 20 words by match count
        println("Top 20 words by match count: ")
        words.take(20).forEachIndexed { index, word -> 
            val matchedWords = matchLookup.get(word) ?: return@forEachIndexed
            println("Position ${index + 1}: ${word} matching ${matchedWords.size}: ${matchedWords.toString()}")
        }
        println()
    }

    @Test
    fun `find proportion of single-matching words`(){

        println("\nTesting: evaluation: finding proportion of single-matching words")

        // find proportion of words with a single match
        val singleMatchingWords = words.count {
            word -> (matchLookup.get(word)?.size == 1)
        }
        val proportionOfSingleMatchingWords = ((singleMatchingWords * 100) / Singleton.wordSet.size)
        println("Proportion of single-matching words: ${proportionOfSingleMatchingWords}%")
        println()
    }

    @Test
    fun `eval algorithm with phrase set`(){

        println("\nTesting: evaluation of algorithm accuracy started")

        val phraseCount = phraseList.size
        val matchedAsTop = phraseList.count {
            phrase -> (getAPIs.submit(phrase).indexOf(phrase.lowercase()) == 0)
        }
        val matchedInTop3 = phraseList.count {
            phrase -> (getAPIs.submit(phrase).indexOf(phrase.lowercase()) < 3)
        }
        val matchedInTop5 = phraseList.count {
            phrase -> (getAPIs.submit(phrase).indexOf(phrase.lowercase()) < 5)
        }
        val matched = phraseList.count {
            phrase -> (getAPIs.submit(phrase).contains(phrase.lowercase()))
        }

        println()
        println("Notice: Automatic evaluation complete: ")
        println("Matched in the top 1 results: ${(matchedAsTop * 100)/phraseCount}% [$matchedAsTop/$phraseCount]")
        println("Matched in the top 3 results: ${(matchedInTop3 * 100)/phraseCount}% [$matchedInTop3/$phraseCount]")
        println("Matched in the top 5 results: ${(matchedInTop5 * 100)/phraseCount}% [$matchedInTop5/$phraseCount]")
        println("Matched: ${(matched * 100)/phraseCount}% [$matched/$phraseCount]")
        println()

        val nonMatched = phraseList.filter {
            phrase -> (!getAPIs.submit(phrase).contains(phrase.lowercase()))
        }
        nonMatched.forEach { phrase -> 
            println("Non-matched phrase found: ${phrase}")
        }
    }

}