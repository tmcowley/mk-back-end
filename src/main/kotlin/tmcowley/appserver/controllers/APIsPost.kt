package tmcowley.appserver.controllers

import org.springframework.cache.annotation.Cacheable
import org.springframework.http.MediaType.*
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
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
        methods = arrayOf(RequestMethod.POST)
)
@RequestMapping(value = arrayOf("/post"), produces = arrayOf("application/json"))
@RestController
class APIsPost {

    // -----
    // STATE-DEPENDENT APIs
    // -----

    /**
     * signup a user;
     * creates a user in the db storing their age and normal (full-board) typing speed
     * returns the newly generated userCode
     */
    @PostMapping(value = arrayOf("/signup"))
    fun signup(@RequestParam("age") age: Int, @RequestParam("typingSpeed") typingSpeed: Int, request: HttpServletRequest): String? {

        if (age < 13) return null

        val userCode = SingletonControllers.db.createNewUser(age, typingSpeed)

        userCode ?: return null

        // create new session, if needed
        val session: HttpSession = request.getSession(true)
        session.setAttribute("userCode", userCode)
        session.setAttribute("sessionNumber", SingletonControllers.db.getNextSession(userCode))

        // get a random phrase from the phrase list
        return userCode
    }

    /**
     * login a user; 
     * creates a session storing their userCode and current sessionNumber
     */
    @PostMapping(value = arrayOf("/login"))
    fun login(@RequestParam("userCode") userCode: String, request: HttpServletRequest): Boolean {
        val validUser = SingletonControllers.db.userCodeTaken(userCode);

        if (!validUser) return false

        // create new session, if needed
        val session: HttpSession = request.getSession(true)
        session.setAttribute("userCode", userCode)
        session.setAttribute("sessionNumber", SingletonControllers.db.getNextSession(userCode))

        return true
    }

    /**
     * check if a user code exists
     */
    @PostMapping(value = arrayOf("/userCodeExists"))
    fun userCodeExists(@RequestParam("userCode") userCode: String): Boolean {
        return SingletonControllers.db.userCodeTaken(userCode);
    }

    /**
     * signout a user; 
     * invalidates the user session
     */
    @PostMapping(value = arrayOf("/signout"))
    fun signout(request: HttpServletRequest) {
        // get session
        val session: HttpSession? = request.getSession(false)

        session?.invalidate()
    }

    /**
     * get the next phrase from the session's number and phrase number
     */
    @PostMapping(value = arrayOf("/getNextPhrase"))
    fun getNextPhrase(request: HttpServletRequest): String? {

        // create new session, if needed
        val session: HttpSession = request.getSession(false)
        val sessionNumber = session.getAttribute("sessionNumber") as Int
        val isFirstSession = (sessionNumber == 1)

        // if first session -> init phrase count
        if (isFirstSession) {
            session.setAttribute("phraseNumber", 1)
        }

        // completed typing session
        val phraseNumber = session.getAttribute("phraseNumber") as Int
        val hasCompletedTypingSession = (phraseNumber >= Singleton.phrasesPerSession)
        if (hasCompletedTypingSession) {
            // need to collect metrics before progressing
            return null
        }

        // get next phrase from current session
        val nextPhrase = Singleton.getNextPhrase(sessionNumber, phraseNumber) ?: return null

        session.setAttribute("phraseNumber", phraseNumber + 1)

        return nextPhrase
    }

    /**
     * get the phrases per session count
     */
    @PostMapping(value = arrayOf("/phrasesPerSession"))
    fun phrasesPerSession(): Int {
        return Singleton.phrasesPerSession
    }

}