package tmcowley.appserver.controllers

import org.springframework.cache.annotation.Cacheable
import org.springframework.web.bind.annotation.*
import tmcowley.appserver.Singleton
import tmcowley.appserver.convertToLeft
import tmcowley.appserver.convertToRight
import tmcowley.appserver.submitSentence

// // https://kotlinlang.org/docs/annotations.html#arrays-as-annotation-parameters
@CrossOrigin(
    origins =
    ["http://localhost:3000", "https://localhost:3000", "https://www.tcowley.com/", "https://tcowley.com/", "https://mirrored-keyboard.vercel.app/"],
    methods = [RequestMethod.GET],

    // allow client cookies
    // see: https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Access-Control-Allow-Credentials
    allowCredentials = "true"

    // TODO filter down from wildcard to allowCredentials
    // see: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/annotation/CrossOrigin.html#allowedHeaders
    // allowedHeaders = ["*"],

    // exposedHeaders = ["*"],
    // exposedHeaders = arrayOf("set-cookie"),
)
@RequestMapping(value = ["/api/v0"], produces = ["application/json"])
@RestController
class APIsGet {

    /** For converting any form to full form (main computation) */
    @Cacheable(value = ["results"])
    @GetMapping(
        value = ["/submit"],
    )
    fun submit(@RequestParam("input") input: String): Array<String> {
        // println("\n\n/submit endpoint called")
        return submitSentence(input)
    }

    /** For converting any form to left-hand form */
    @Cacheable(value = ["left-forms"])
    @GetMapping(
        value = ["/convert-lhs"],
    )
    fun convertToLHS(@RequestParam("input") input: String?): String {
        // println("/post/convert/lhs convertToLHS called with input: ${input}")

        // for each alphabetic char in string -> lookup keypair, get left key in keypair
        return convertToLeft(input)
    }

    /** For converting any form to right-hand form */
    @Cacheable(value = ["right-forms"])
    @GetMapping(
        value = ["/convert-rhs"],
    )
    fun convertToRHS(@RequestParam("input") input: String?): String {
        // println("/post/convert/rhs convertToRHS called with input: ${input}")

        // for each alphabetic char in string -> lookup keypair, get right key in keypair
        return convertToRight(input)
    }

    /** API status query endpoint */
    @GetMapping(value = ["/get-status"])
    fun status(): Boolean {
        // return true when active
        return true
    }

    /** random phrase query endpoint */
    @GetMapping(value = ["/get-random-phrase"])
    fun getRandomPhrase(): String {
        // get a random phrase from the phrase list
        return Singleton.getRandomPhrase()
    }

    /** get the phrases per session count */
    @GetMapping(value = ["/get-phrases-per-session"])
    fun getPhrasesPerSession(): Int = Singleton.phrasesPerSession
}
