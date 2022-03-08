package tmcowley.appserver

import tmcowley.appserver.Singleton
import tmcowley.appserver.controllers.API

// https://junit.org/junit4/javadoc/4.8/org/junit/Assert.html
import org.junit.Assert.assertEquals;
import org.junit.Ignore;

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Disabled
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class Evaluation {

    val apiInstance: API = API()

    val phraseList: List<String> = Singleton.phraseList

    @Test
    fun `eval algorithm with phrase set`(){

        println("\nTesting: auto evaluation started")
        
        var phraseAtTop = 0
        var phraseInTop3 = 0
        var phraseInTop5 = 0
        var phraseInResults = 0
        val phraseCount = phraseList.size

        var failedPhrases: HashMap<String, Array<String>> = hashMapOf();

        for (phrase in phraseList) {

            val results: Array<String> = apiInstance.submit(phrase)
            val phraseLowercase = phrase.lowercase()

            // if (phrase.equals("tell a lie and your nose will grow")) {
            //     println()
            //     println(phraseLowercase)
            //     results.forEach { result -> print("${result}, ") }
            // }

            if (results.isEmpty()) {
                failedPhrases.put(phraseLowercase, results);

                continue
            }

            val topMatch: Boolean = phraseLowercase.equals(results[0])
            if (topMatch) {
                phraseAtTop++
                phraseInTop3++
                phraseInTop5++
                phraseInResults++

                continue
            }

            val phraseFound: Boolean = results.contains(phraseLowercase)
            if (phraseFound) {
                phraseInResults++
            } else {
                // have an unmatched phrase
                failedPhrases.put(phraseLowercase, results);

                continue
            }

            val resultsList: List<String> = results.toList()

            if (results.size < 3) continue
            val top3Match: Boolean = resultsList.subList(0, 3).contains(phraseLowercase)
            if (top3Match) {
                phraseInTop3++
                phraseInTop5++

                continue
            }

            if (results.size < 5) continue
            val top5Match: Boolean = resultsList.subList(0, 5).contains(phraseLowercase)
            if (top5Match) {
                phraseInTop5++

                continue
            }
        }

        println()
        println("Notice: Automatic evaluation complete: ")
        println("Phrase found in the top 1 results: ${(phraseAtTop * 100)/phraseCount}% [$phraseAtTop/$phraseCount]")
        println("Phrase found in the top 3 results: ${(phraseInTop3 * 100)/phraseCount}% [$phraseInTop3/$phraseCount]")
        println("Phrase found in the top 5 results: ${(phraseInTop5 * 100)/phraseCount}% [$phraseInTop5/$phraseCount]")
        println("Phrase found: ${(phraseInResults * 100)/phraseCount}% [$phraseInResults/$phraseCount]")
        println()

        val failedMatches = (phraseCount - phraseInResults)
        val warnFailedMatches = (failedMatches != 0)
        if (warnFailedMatches) {
            println("Error: some phrases unmatched: ${(failedMatches * 100)/phraseCount}% [$failedMatches/$phraseCount]")

            // list the failed phrases and the algos results
            for ((phrase, results) in failedPhrases) {
                println("Unmatched phrase found: \n${phrase}")
                println("results: \n${results}")
                println()
            }
        }
    }

}