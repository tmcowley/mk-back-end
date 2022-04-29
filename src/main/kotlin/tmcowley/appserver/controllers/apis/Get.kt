package tmcowley.appserver.controllers.apis

import org.springframework.cache.annotation.Cacheable
import org.springframework.web.bind.annotation.*
import tmcowley.appserver.*
import tmcowley.appserver.controllers.TrainingSession
import tmcowley.appserver.controllers.User
import tmcowley.appserver.utils.convertToLeft
import tmcowley.appserver.utils.convertToRight

@RestController
@CrossOrigin(
    // enabled cross-origin urls
    origins = ["http://localhost:3000", "https://localhost:3000", "https://www.tcowley.com", "https://tcowley.com", "https://mirrored-keyboard.vercel.app"],
    methods = [RequestMethod.GET],

    // see: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/annotation
    // /CrossOrigin.html#allowedHeaders
    allowedHeaders = ["*"],
    exposedHeaders = ["*"],

    // allow client cookies
    // see: https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Access-Control-Allow-Credentials
    allowCredentials = "true"
)
@RequestMapping(value = ["/api/v0"], produces = ["application/json"])
class Get {

    /** For converting any form to full form (main computation) */
    @Cacheable(value = ["results"])
    @GetMapping(
        value = ["/submit"],
    )
    fun submit(@RequestParam("input") input: String): List<String> {
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

    /** get a map of all users to a list of their training sessions */
    @GetMapping(value = ["/get-sessions-for-each-user"])
    fun getSessionsForEachUser(): Map<User, List<TrainingSession>> =
        // map of users to their sessions
        SingletonControllers.db.getTrainingSessionsForEachUser()

    /** get a map of all user-ids to a list of their training sessions */
    @GetMapping(value = ["/get-sessions-for-each-user-by-id"])
    fun getSessionsForEachUserById(): Map<Int, List<TrainingSession>> =
        // map of users, by user-id, to their sessions
        SingletonControllers.db.getTrainingSessionsForEachUser()
            .mapKeys { (user, _) ->
                user.id.value
            }

    /** get a map of all user-codes to a list of their training sessions */
    @GetMapping(value = ["/get-sessions-for-each-user-by-user-code"])
    fun getSessionsForEachUserByCode(): Map<String, List<TrainingSession>> =
        // map of users, by user-id, to their sessions
        SingletonControllers.db.getTrainingSessionsForEachUser()
            .mapKeys { (user, _) ->
                user.userCode
            }
}
