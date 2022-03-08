package tmcowley.appserver.controllers

import org.springframework.cache.annotation.Cacheable
import org.springframework.http.MediaType.*
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import tmcowley.appserver.Singleton
import tmcowley.appserver.objects.Key
import tmcowley.appserver.objects.KeyPair
import tmcowley.appserver.structures.getSentences
import tmcowley.appserver.structures.getWords
import tmcowley.appserver.utils.FreqTool
import tmcowley.appserver.utils.LangTool
import tmcowley.appserver.SingletonControllers

import tmcowley.appserver.submitSentence
import tmcowley.appserver.convertFullToLHS
import tmcowley.appserver.convertFullToRHS


import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

import org.springframework.session.*

import kotlin.random.Random

// // https://kotlinlang.org/docs/annotations.html#arrays-as-annotation-parameters
@CrossOrigin(
        origins =
                arrayOf(
                        // for local development
                        "http://localhost:3000",

                        // for hosting
                        "https://www.tcowley.com/",
                        "https://tcowley.com/",
                        "https://mirrored-keyboard.vercel.app/"
                ),
        methods = arrayOf(RequestMethod.GET)
)
@RequestMapping(value = arrayOf("/get"), produces = arrayOf("application/json"))
@RestController
class API {

    val syntaxAnalysisEnabled: Boolean = (Singleton.prop.get("analyseSyntax") as String).toBoolean()
    val frequencyAnalysisEnabled: Boolean =
            (Singleton.prop.get("analyseFrequency") as String).toBoolean()

    init {
        println()

        if (syntaxAnalysisEnabled) println("Notice: Syntax analysis enabled")
        else println("Notice: Syntax analysis disabled")

        if (frequencyAnalysisEnabled) println("Notice: Frequency analysis enabled")
        else println("Notice: Frequency analysis disabled")
    }

    /** For converting any form to full form (main computation) */
    @Cacheable
    @GetMapping(
            value = arrayOf("/submit"),
    )
    fun submit(@RequestParam("input") input: String): Array<String> {
        // println("\n\n/submit endpoint called")
        return submitSentence(input)
    }

    /** For converting any form to left-hand form */
    @Cacheable
    @GetMapping(
            value = arrayOf("/convert/lhs"),
    )
    fun convertToLHS(@RequestParam("input") input: String?): String {
        // println("/post/convert/lhs convertToLHS called with input: ${input}")

        // for each alphabetic char in string -> lookup keypair, get left key in keypair
        return convertFullToLHS(input)
    }

    /** For converting any form to rigth-hand form */
    @Cacheable
    @GetMapping(
            value = arrayOf("/convert/rhs"),
    )
    fun convertToRHS(@RequestParam("input") input: String?): String {
        // println("/post/convert/rhs convertToRHS called with input: ${input}")

        // for each alphabetic char in string -> lookup keypair, get right key in keypair
        return convertFullToRHS(input)
    }

    /** API status query endpoint */
    @GetMapping(value = arrayOf("/status"))
    fun status(): Boolean {
        // return true when active
        return true
    }

    /** Random phrase query endpoint */
    @GetMapping(value = arrayOf("/random-phrase"))
    fun getRandomPhrase(): String {
        // get a random phrase from the phrase list
        return Singleton.getRandomPhrase()
    }

    // -----
    // STATE-DEPENDENT APIs (to be moved to POST requests)
    // -----

    // @GetMapping(value = arrayOf("/getNextPhrase"))
    // fun getNextPhrase(request: HttpServletRequest): String? {

    //     // create new session, if needed
    //     val session: HttpSession = request.getSession(false)
    //     val sessionNumber = session.getAttribute("sessionNumber") as Int
    //     val isFirstSession = (sessionNumber == 1)

    //     // if first session -> init phrase count
    //     if (isFirstSession) {
    //         session.setAttribute("phraseNumber", 1)
    //     }

    //     // completed typing session
    //     val phraseNumber = session.getAttribute("phraseNumber") as Int
    //     val hasCompletedTypingSession = (phraseNumber >= Singleton.phrasesPerSession)
    //     if (hasCompletedTypingSession) {
    //         // need to collect metrics before progressing
    //         return null
    //     }

    //     // get next phrase from current session
    //     val nextPhrase = Singleton.getNextPhrase(sessionNumber, phraseNumber) ?: return null

    //     session.setAttribute("phraseNumber", phraseNumber + 1)

    //     return nextPhrase
    // }

    // @GetMapping(value = arrayOf("/phrasesPerSession"))
    // fun phrasesPerSession(): Int {
    //     return Singleton.phrasesPerSession
    // }

    // @GetMapping(value = arrayOf("/signup"))
    // fun signup(@RequestParam("age") age: Int, @RequestParam("typingSpeed") typingSpeed: Int, request: HttpServletRequest): String? {

    //     if (age < 13) return null

    //     val userCode = SingletonControllers.db.createNewUser(age, typingSpeed)

    //     userCode ?: return null

    //     // create new session, if needed
    //     val session: HttpSession = request.getSession(true)
    //     session.setAttribute("userCode", userCode)
    //     session.setAttribute("sessionNumber", SingletonControllers.db.getNextSession(userCode))

    //     // get a random phrase from the phrase list
    //     return userCode
    // }

    // @GetMapping(value = arrayOf("/login"))
    // fun login(@RequestParam("userCode") userCode: String, request: HttpServletRequest): Boolean {
    //     val validUser = SingletonControllers.db.userCodeTaken(userCode);

    //     if (!validUser) return false

    //     // create new session, if needed
    //     val session: HttpSession = request.getSession(true)
    //     session.setAttribute("userCode", userCode)
    //     session.setAttribute("sessionNumber", SingletonControllers.db.getNextSession(userCode))

    //     return true
    // }
}
