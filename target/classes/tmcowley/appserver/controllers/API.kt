package tmcowley.appserver.controllers

import org.springframework.cache.annotation.Cacheable
import org.springframework.http.MediaType.*
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

import org.springframework.web.bind.annotation.RequestParam
import tmcowley.appserver.Singleton
import tmcowley.appserver.objects.Key
import tmcowley.appserver.objects.KeyPair
import tmcowley.appserver.utils.FreqTool
import tmcowley.appserver.utils.LangTool

import org.springframework.stereotype.Controller

// // https://kotlinlang.org/docs/annotations.html#arrays-as-annotation-parameters
@CrossOrigin(
    origins = arrayOf("http://localhost:3000"),
    methods = arrayOf(RequestMethod.GET)
)
@RequestMapping(
    value = arrayOf("/get"), 
    produces = arrayOf("application/json")
)
@RestController
class API {

    init {}

    /**
     * For converting any form to full form (main computation)
     */
    @Cacheable
    @GetMapping(
        value = arrayOf("/submit"),
    )
    fun submit(@RequestParam("input") input: String): Array<String> {
        // println("\n\n/submit endpoint called")
        return submitSentence(input)
    }

    /**
     * For converting any form to left-hand form
     */
    @Cacheable
    @GetMapping(
        value = arrayOf("/convert/lhs"),
    )
    fun convertToLHS(@RequestParam("input") input: String?): String {
        // println("/post/convert/lhs convertToLHS called with input: ${input}")

        // for each alphabetic char in string -> lookup keypair, get left key in keypair
        return convertFullToLHS(input)
    }

    /**
     * For converting any form to rigth-hand form
     */
    @Cacheable
    @GetMapping(
        value = arrayOf("/convert/rhs"),
    )
    fun convertToRHS(@RequestParam("input") input: String?): String {
        // println("/post/convert/rhs convertToRHS called with input: ${input}")

        // for each alphabetic char in string -> lookup keypair, get right key in keypair
        return convertFullToRHS(input)
    }

    /**
     * API status query endpoint
     */
    @GetMapping(value = arrayOf("/status"))
    fun status(): Boolean {
        // return true when active
        return true
    }

    /**
     * Random phrase query endpoint
     */
    @GetMapping(value = arrayOf("/random-phrase"))
    fun getRandomPhrase(): String {
        // get a random phrase from the phrase list
        return Singleton.getRandomPhrase()
    }

    fun submitSentence(input: String): Array<String> {
        val lowercaseInput = input.lowercase()

        // create input word array
        val inputWords: Array<String> = splitIntoWords(lowercaseInput)

        val resultingSentences: MutableList<String> = buildAndReadTrees(inputWords)

        // report input and output
        run {
            System.out.println("\nInput: ${input}")
            System.out.println("\nResults: ")

            if (resultingSentences.isEmpty()) {
                System.out.println("Notice: system found no resulting sentences")
            }

            for (sentence: String in resultingSentences) {
                System.out.println(sentence)
            }
        }

        val syntaxAnalysisEnabled: Boolean = (Singleton.prop.get("analyseSyntax") as String).toBoolean();
        val frequencyAnalysisEnabled: Boolean = (Singleton.prop.get("analyseFrequency") as String).toBoolean();


        // syntax analysis enabled -> perform analysis
        if (syntaxAnalysisEnabled) {
            // rank sentences based on syntax
            resultingSentences.sortWith(SentenceSyntaxComparator)

            // var i = 0
            // for (sentence: String in resultingSentences) {
            //     println("i: ${i}")
            //     println("sentence: ${sentence}")
            //     println("error count: ${Singleton.langtool.countErrors(sentence)}")
            //     println()
            //     i++
            // }

            // filtering: pick top 5, remove any lower ranking
            // ...
        } else {
            // syntax analysis disabled
            println("Notice: Syntax analysis disabled")
        }     

        // frequency analysis enabled -> perform analysis
        if (frequencyAnalysisEnabled) {
            resultingSentences.sortWith(SentenceFrequencyComparator)
        } else {
            // syntax analysis disabled
            println("Notice: Frequency analysis disabled")
        }

        return resultingSentences.toTypedArray()
    }

    fun analyseSyntax(resultingSentences: MutableList<String>): MutableList<String> {

        // var i = 0
        // for (sentence: String in resultingSentences) {
        //     println("i: ${i}")
        //     println("sentence: ${sentence}")
        //     println("error count: ${Singleton.langtool.countErrors(sentence)}")
        //     println()
        //     i++
        // }

        // filtering: pick top 5, remove any lower ranking
        // ...

        return resultingSentences
    }

    fun splitIntoWords(sentence: String): Array<String> {
        return sentence.split(" ").toTypedArray()
    }

    fun buildAndReadTrees(inputWords: Array<String>): MutableList<String> {

        var listOfMatchedWords: MutableList<MutableList<String>> = mutableListOf()

        for (word: String in inputWords) {

            var wordKeyPairs: MutableList<KeyPair> = mutableListOf()

            for (char: Char in word) {

                // char is alphabetic
                if (isAlphabetic(char)) {

                    // get key for char
                    val key: Key = Key(char)

                    // get key-pair for key
                    val keyPair: KeyPair? = Singleton.getKeyPair(key)

                    // key-pair lookup fails
                    if (keyPair == null) {
                        println("Error: getKeyPair(${key.toString()}) failed")
                        continue
                    }

                    // add the key-pair to the current word
                    wordKeyPairs.add(keyPair)
                }

                // char is non-alphabetic -> do nothing
            }

            // space bar computation (word transition)

            // store current word

            var matchedWords: MutableList<String> = Singleton.structures.getWords(wordKeyPairs)

            if (matchedWords.isEmpty()) {
                println("\nwordKeyPairs: ${wordKeyPairs}")
                println("Notice: the word '${word}' was not matched")
                continue
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
        val resultingSentences: MutableList<String> =
                Singleton.structures.getSentences(listOfMatchedWords)

        return resultingSentences
    }

    fun isAlphabetic(char: Char): Boolean {
        return (char in 'a'..'z' || char in 'A'..'Z')
    }

    fun convertFullToRHS(input: String?): String {
        if (input == null) {
            return ""
        }

        var inputRHS: String = ""

        for (char: Char in input) {
            if (isAlphabetic(char)) {

                val key: Key = Key(char)

                val keyPair: KeyPair? = Singleton.getKeyPair(key)

                if (keyPair == null) {
                    inputRHS += char
                    continue
                }

                val charRHS: Char = keyPair.rightKey.character

                inputRHS += charRHS

                continue
            }

            inputRHS += char
        }

        return inputRHS
    }

    fun convertFullToLHS(input: String?): String {
        if (input == null) {
            return ""
        }

        var inputLHS: String = ""

        for (char: Char in input) {
            if (isAlphabetic(char)) {

                val key: Key = Key(char)

                val keyPair: KeyPair? = Singleton.getKeyPair(key)

                if (keyPair == null) {
                    inputLHS += char
                    continue
                }

                val charLHS: Char = keyPair.leftKey.character

                inputLHS += charLHS

                continue
            }

            inputLHS += char
        }

        return inputLHS
    }
}

class SentenceSyntaxComparator {
    companion object : Comparator<String> {
        val langtool: LangTool = Singleton.langTool
        override fun compare(first: String, second: String): Int {

            // calculate syntax correctness scores
            val firstScore: Int = langtool.countErrors(first)
            val secondScore: Int = langtool.countErrors(second)

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

            println("{${first}} has frequency: ${firstScore}")
            println("{${second}} has frequency: ${secondScore}")

            when {
                // higher score is better
                firstScore != secondScore -> return (firstScore - secondScore)
                else -> return (0)
            }
        }
    }
}
