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

                        // for hosting
                        "https://www.tcowley.com/",
                        "https://tcowley.com/",
                        "https://mirrored-keyboard.vercel.app/"
                ),
        methods = arrayOf(RequestMethod.POST)
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

        val userCode = SingletonControllers.db.createNewUser(form.age, form.speed)

        userCode ?: return null

        // create new session, if needed
        val session: HttpSession = request.getSession(true)
        session.setAttribute("userCode", userCode)
        session.setAttribute("sessionNumber", SingletonControllers.db.getNextSession(userCode))

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

        println(form.userCode)

        // ensure the user exists
        val validUser = validateUserCode(form.userCode)
        if (!validUser) return false

        // create new session, if needed
        val session: HttpSession = request.getSession(true)
        session.setAttribute("userCode", form.userCode)
        session.setAttribute("sessionNumber", SingletonControllers.db.getNextSession(form.userCode))

        return true
    }

    private fun validateUserCode(userCode: String): Boolean {
        println("validateUserCode() called with user-code: ${userCode}")
        return SingletonControllers.db.userCodeTaken(userCode)
    }

    /** signout a user; invalidates the user session */
    @PostMapping(value = arrayOf("/sign-out"))
    fun signout(request: HttpServletRequest) {
        // get session, invalidate
        val session: HttpSession? = request.getSession(false)
        session?.invalidate()
    }

    @PostMapping(value = arrayOf("/is-logged-in"))
    fun isLoggedIn(request: HttpServletRequest): Boolean {
        // get session
        val session: HttpSession? = request.getSession(false)
        if (session == null) return false

        return true
    }

    /** get the next phrase from the session's number and phrase number */
    @PostMapping(value = arrayOf("/get-next-phrase"))
    fun getNextPhrase(request: HttpServletRequest): String? {

        // get the user session
        val session: HttpSession? = request.getSession(false)
        if (session == null) return null

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

    /** get the phrases per session count */
    @PostMapping(value = arrayOf("/get-phrases-per-session"))
    fun phrasesPerSession(): Int {
        return Singleton.phrasesPerSession
    }

    /** get the user code attached to the session */
    @PostMapping(value = arrayOf("/get-user-code"))
    fun getUserCode(request: HttpServletRequest): String? {

        // get the user session
        val session: HttpSession? = request.getSession(false)
        if (session == null) return null

        return session.getAttribute("userCode") as String
    }

    /** get the user code attached to the session */
    @PostMapping(value = arrayOf("/report-completed-session"))
    fun reportCompletedSession(@RequestBody metricsObj: String, request: HttpServletRequest): Boolean? {

        // get the user session
        val session: HttpSession? = request.getSession(false)
        if (session == null) return null

        // collect metrics
        var metrics = try { 
            Json.decodeFromString<MetricsData>(metricsObj) 
        } catch (e: SerializationException) { 
            println("Error: login(): failed to deserialize form")
            return false 
        }

        // get training session number from session
        val sessionNumber = session.getAttribute("sessionNumber") as Int

        // store metrics in session obj
        var sessionData = SessionData(sessionNumber, metrics.speed, metrics.accuracy)

        // store completed session in database
        // ...

        // incrememnt session number (if exists next session)

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

