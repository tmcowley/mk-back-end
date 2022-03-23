package tmcowley.appserver.controllers

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession
import org.springframework.http.MediaType.*
import org.springframework.session.*
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import tmcowley.appserver.Singleton
import tmcowley.appserver.SingletonControllers
import tmcowley.appserver.objects.SessionData

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.SerializationException

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
        methods = arrayOf(RequestMethod.POST), 

        // TODO filter down from wildcard to allowCredentials
        // see: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/annotation/CrossOrigin.html#allowedHeaders
        allowedHeaders = arrayOf("*"), 

        exposedHeaders = arrayOf("*"),
        // exposedHeaders = arrayOf("set-cookie"),

        // allow client cookies
        // see: https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Access-Control-Allow-Credentials
        allowCredentials = "true"
)
@RequestMapping(value = arrayOf("/api/v0"), consumes = arrayOf("application/json"), produces = arrayOf("application/json"))
@RestController
class APIsPost {

    // -----
    // STATE-DEPENDENT APIs
    // -----

    /**
     * signup a user; creates a user in the db storing their age and normal (full-board) typing
     * speed returns the newly generated userCode
     */
    @PostMapping(value = arrayOf("/sign-up"))
    fun signup(
            @RequestBody signupForm: String,
            request: HttpServletRequest
    ): String? {

        var form = try { 
            Json.decodeFromString<SignupForm>(signupForm)
        } catch (e: SerializationException) { 
            println("Error: signup(): failed to deserialize form")
            return null
        }

        if (form.age < 13) return null

        val userCode = SingletonControllers.db.createNewUserGettingCode(form.age, form.speed) ?: return null

        // create new session, if needed
        val session: HttpSession = request.getSession(true)
        session.setAttribute("userCode", userCode)
        session.setAttribute("sessionNumber", SingletonControllers.db.getNextSessionNumber(userCode))

        // get a random phrase from the phrase list
        return userCode
    }

    /** login a user; creates a session storing their userCode and current sessionNumber */
    @PostMapping(value = arrayOf("/sign-in"))
    fun signin(@RequestBody loginForm: String, request: HttpServletRequest): Boolean {

        var form = try { 
            Json.decodeFromString<LoginForm>(loginForm) 
        } catch (e: SerializationException) { 
            println("Error: login(): failed to deserialize form")
            return false 
        }

        println("Notice: sign in attempt with user-code:${form.userCode}")

        // ensure the user exists
        val validUser = userCodeTaken(form.userCode) ?: return false
        if (!validUser) return false

        // create new session, if needed
        val session: HttpSession = request.getSession(true)
        session.setAttribute("userCode", form.userCode)
        session.setAttribute("sessionNumber", SingletonControllers.db.getNextSessionNumber(form.userCode))

        return true
    }

    /** check if a user-code is assigned to a user */
    private fun userCodeTaken(userCode: String): Boolean? {
        // println("userCodeTaken() called with user-code: ${userCode}")
        return SingletonControllers.db.userCodeTaken(userCode)
    }

    /** signout a user; invalidates the user session */
    @PostMapping(value = arrayOf("/sign-out"))
    fun signout(request: HttpServletRequest) {

        // ensure user is logged in
        if (!isLoggedIn(request)) return

        // get session, invalidate
        val session = request.getSession(false)
        session.invalidate()
    }

    /** check if a user is signed-in using their request */
    @PostMapping(value = arrayOf("/is-logged-in"))
    fun isLoggedIn(request: HttpServletRequest): Boolean {
        // get session
        request.getSession(false) ?: return false
        return true
    }

    /** get the next phrase from the session's number and phrase number */
    @PostMapping(value = arrayOf("/get-next-phrase"))
    fun getNextPhrase(request: HttpServletRequest): String? {

        // ensure user is logged in
        if (!isLoggedIn(request)) return null

        // get the user session, session number
        val session: HttpSession = request.getSession(false)
        val sessionNumber = session.getAttribute("sessionNumber") as Int

        // if first session -> init phrase count
        val isFirstSession = (sessionNumber == 1)
        if (isFirstSession) session.setAttribute("phraseNumber", 1)

        // completed typing session
        val phraseNumber = session.getAttribute("phraseNumber") as Int
        val hasCompletedTypingSession = (phraseNumber >= Singleton.phrasesPerSession)
        if (hasCompletedTypingSession) {
            // need to collect metrics before progressing
            return null
        }

        // get next phrase from current session
        val nextPhrase = Singleton.getNextPhrase(sessionNumber, phraseNumber) ?: return null

        // update phrase number
        session.setAttribute("phraseNumber", phraseNumber + 1)

        return nextPhrase
    }

    /** get the phrases per session count */
    @PostMapping(value = arrayOf("/get-phrases-per-session"))
    fun phrasesPerSession(): Int {
        return Singleton.phrasesPerSession
    }

    /** get the user code attached to the session */
    @PostMapping(value = arrayOf("/get-user-code"))
    fun getUserCode(request: HttpServletRequest): String? {

        // ensure user is logged in
        if (!isLoggedIn(request)) return null

        val session: HttpSession = request.getSession(false)
        return session.getAttribute("userCode") as String
    }

    /** get the user code attached to the session */
    @PostMapping(value = arrayOf("/report-completed-session"))
    fun reportCompletedSession(@RequestBody metricsObj: String, request: HttpServletRequest): Boolean? {

        // ensure user is logged in
        if (!isLoggedIn(request)) return null

        val session: HttpSession = request.getSession(false)
        val userCode = session.getAttribute("userCode") as String

        // collect metrics
        var metrics = try { 
            Json.decodeFromString<MetricsData>(metricsObj) 
        } catch (e: SerializationException) { 
            println("Error: login(): failed to deserialize form")
            return false 
        }

        // store metrics in session obj
        var sessionData = SessionData(metrics.speed, metrics.accuracy)

        // store completed session in database
        SingletonControllers.db.storeCompletedSession(userCode, sessionData)

        // increment session number (if exists next session)
        session.setAttribute("sessionNumber", SingletonControllers.db.getNextSessionNumber(userCode))

        // report success
        return true
    }
}

@Serializable
final data class LoginForm(val userCode: String)

@Serializable
final data class SignupForm(val age: Int, val speed: Int)

@Serializable
final data class MetricsData(val speed: Float, val accuracy: Float)
