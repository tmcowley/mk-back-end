package tmcowley.appserver.controllers

import tmcowley.appserver.Singleton;

import tmcowley.appserver.objects.Key
import tmcowley.appserver.objects.KeyPair

import tmcowley.appserver.structures.DataStructures;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.CrossOrigin;

import org.springframework.http.MediaType.*;

// https://kotlinlang.org/docs/annotations.html#arrays-as-annotation-parameters
@RestController
@CrossOrigin(
    origins = ["http://localhost:3000"]
)
@RequestMapping(
    value=["/api"],
    consumes=["text/plain"]
    // consumes=["application/json"]
)
class API {

    @PostMapping(
        value=["/submit"],
        consumes=["text/plain"]
    )
    fun submit(@RequestBody input: String): Array<String> {

        println("\n\n/submit endpoint called");

        val lowercaseInput = input.lowercase();

        // create input word array
        val inputWords: Array<String> = splitIntoWords(lowercaseInput);

        val resultingSentences: MutableList<String> = buildAndReadTrees(inputWords);

        // report input and output
        run {
            System.out.println("\nInput: ${input}");

            System.out.println("\nResults: ");

            if (resultingSentences.isEmpty()) {
                System.out.println("Notice: system found no resulting sentences");
            }

            for (sentence: String in resultingSentences) {
                System.out.println(sentence);
            }
        }

        return resultingSentences.toTypedArray();
    }

    fun splitIntoWords(sentence: String): Array<String> {
        return sentence.split(" ").toTypedArray();
    }

    fun buildAndReadTrees(inputWords: Array<String>): MutableList<String> {

        var listOfMatchedWords: MutableList<MutableList<String>> = mutableListOf();

        for (word: String in inputWords) {

            var wordKeyPairs: MutableList<KeyPair> = mutableListOf();

            for (char: Char in word) {

                // char is alphabetic
                if (isAlphabetic(char)) {

                    // get key for char
                    val key: Key = Key(char);

                    // get key-pair for key
                    val keyPair: KeyPair? = Singleton.getKeyPair(key);

                    // key-pair lookup fails
                    if (keyPair == null) {
                        println("Error: getKeyPair(${key.toString()}) failed")
                        continue;
                    }

                    // add the key-pair to the current word
                    wordKeyPairs.add(keyPair);
                }

                // char is non-alphabetic -> do nothing
            }

            // space bar computation (word transition)

            // store current word

            var matchedWords: MutableList<String> = Singleton.structures.getWords(wordKeyPairs);

            if (matchedWords.isEmpty()) {
                println("\nwordKeyPairs: ${wordKeyPairs}");
                println("Notice: the word '${word}' was not matched");
                continue;
            }

            // add the viable words to the total list
            listOfMatchedWords.add(matchedWords);
        }

        // trees have been built, calculate viable sentences

        // no words have been computed
        if (listOfMatchedWords.isEmpty()) {
            System.out.println("Notice: listOfMatchedWords is empty");
            return mutableListOf();
        }

        // compute viable sentences from text array
        val resultingSentences: MutableList<String> = Singleton.structures.getSentences(listOfMatchedWords);

        return resultingSentences;
    }

    fun isAlphabetic(char: Char): Boolean {
        return (char in 'a'..'z' || char in 'A'..'Z');
    }

    @PostMapping(value=["/convert/rhs"])
    fun convertToRHS(@RequestBody input: String?): String {
        // for each alphabetic char in string -> lookup keypair, get right key in keypair

        if (input == null) {
            return "";
        }

        var inputRHS: String = "";

        for (char: Char in input) {
            if (isAlphabetic(char)) {

                val key: Key = Key(char);

                val keyPair: KeyPair? = Singleton.getKeyPair(key);

                if (keyPair == null) {
                    inputRHS += char;
                    continue;
                }

                val charRHS: Char = keyPair.rightKey.character;

                inputRHS += charRHS;

                continue;
            }

            inputRHS += char;
        }

        return inputRHS;
    }

    @PostMapping(value=["/convert/lhs"])
    fun convertToLHS(@RequestBody input: String?): String {

        // for each alphabetic char in string -> lookup keypair, get right key in keypair

        if (input == null) {
            return "";
        }

        var inputLHS: String = "";

        for (char: Char in input) {
            if (isAlphabetic(char)) {

                val key: Key = Key(char);

                val keyPair: KeyPair? = Singleton.getKeyPair(key);

                if (keyPair == null) {
                    inputLHS += char;
                    continue;
                }

                val charLHS: Char = keyPair.leftKey.character;

                inputLHS += charLHS;

                continue;
            }

            inputLHS += char;
        }

        return inputLHS;
    }

    @PostMapping(value=["/test"])
    fun getAll(@RequestBody body: kotlin.Any): String {
        println("/api/test called");

        // var body: String? = null;

        try {
            println("test post mapping collected: $body")
        } catch (e: Exception) {
            println("printing request body failed");
        }

        return "/api/test called"
    }

}
