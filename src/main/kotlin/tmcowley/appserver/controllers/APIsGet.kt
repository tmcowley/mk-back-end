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

import tmcowley.appserver.submitSentence
import tmcowley.appserver.convertToLeft
import tmcowley.appserver.convertToRight

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
                        "https://localhost:3000",

                        // for hosting
                        "https://www.tcowley.com/",
                        "https://tcowley.com/",
                        "https://mirrored-keyboard.vercel.app/"
                ),
        methods = arrayOf(RequestMethod.GET), 

        // TODO filter down from wildcard to allowCredentials
        // see: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/annotation/CrossOrigin.html#allowedHeaders
        allowedHeaders = arrayOf("*"), 

        exposedHeaders = arrayOf("*"),
        // exposedHeaders = arrayOf("set-cookie"),

        // allow client cookies
        // see: https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Access-Control-Allow-Credentials
        allowCredentials = "true"
)
@RequestMapping(value = arrayOf("/api/v0"), produces = arrayOf("application/json"))
@RestController
class APIsGet {

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
            value = arrayOf("/convert-lhs"),
    )
    fun convertToLHS(@RequestParam("input") input: String?): String {
        // println("/post/convert/lhs convertToLHS called with input: ${input}")

        // for each alphabetic char in string -> lookup keypair, get left key in keypair
        return convertToLeft(input)
    }

    /** For converting any form to rigth-hand form */
    @Cacheable
    @GetMapping(
            value = arrayOf("/convert-rhs"),
    )
    fun convertToRHS(@RequestParam("input") input: String?): String {
        // println("/post/convert/rhs convertToRHS called with input: ${input}")

        // for each alphabetic char in string -> lookup keypair, get right key in keypair
        return convertToRight(input)
    }

    /** API status query endpoint */
    @GetMapping(value = arrayOf("/get-status"))
    fun status(): Boolean {
        // return true when active
        return true
    }

    /** Random phrase query endpoint */
    @GetMapping(value = arrayOf("/get-random-phrase"))
    fun getRandomPhrase(): String {
        // get a random phrase from the phrase list
        return Singleton.getRandomPhrase()
    }
}
