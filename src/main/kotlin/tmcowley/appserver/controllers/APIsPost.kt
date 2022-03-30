package tmcowley.appserver.controllers

import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.springframework.web.bind.annotation.*
import tmcowley.appserver.Singleton
import tmcowley.appserver.SingletonControllers
import tmcowley.appserver.models.SignInForm
import tmcowley.appserver.models.TrainingSessionData
import tmcowley.appserver.models.SignUpForm
import tmcowley.appserver.utils.validateSessionData
import tmcowley.appserver.utils.validateSignUpForm
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

@CrossOrigin(
    origins =
    ["http://localhost:3000", "https://localhost:3000", "https://www.tcowley.com", "https://tcowley.com", "https://mirrored-keyboard.vercel.app"],
    methods = [RequestMethod.POST],

    // TODO filter down from wildcard to allowCredentials
    // see: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/annotation/CrossOrigin.html#allowedHeaders
    allowedHeaders = ["*"],
//    allowedHeaders = ["Content-Type", "Accept", "X-Requested-With"]
//
    exposedHeaders = ["*"],
//    // exposedHeaders = arrayOf("set-cookie"),

    // allow client cookies
    // see: https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Access-Control-Allow-Credentials
    allowCredentials = "true",
)
@RequestMapping(value = ["/api/v0"], consumes = ["application/json"], produces = ["application/json"])
@RestController
class APIsPost {

    // -----
    // STATE-DEPENDENT APIs
    // -----

    /**
     * sign up a user; creates a user in the db storing their age and normal (full-board) typing
     * speed returns the newly generated userCode
     */
    @PostMapping(value = ["/sign-up"])
    fun signUp(
        @RequestBody signupForm: String,
        request: HttpServletRequest
    ): String? {

        val form = try {
            Json.decodeFromString<SignUpForm>(signupForm)
        } catch (e: SerializationException) {
            println("Error: signUp(): failed to deserialize form")
            return null
        }

        if (!validateSignUpForm(form)) return null

        val userCode = SingletonControllers.db.createNewUserGettingCode(form.age, form.speed) ?: return null

        // create new session, if needed
        val session: HttpSession = request.getSession(true)
        session.setAttribute("userCode", userCode)
        session.setAttribute("sessionNumber", SingletonControllers.db.getNextSessionNumber(userCode))

        // get a random phrase from the phrase list
        return userCode
    }

    /** sign in a user; creates a session storing their userCode and current sessionNumber */
    @PostMapping(value = ["/sign-in"])
    fun signIn(@RequestBody loginForm: String, request: HttpServletRequest): Boolean {

        val form = try {
            Json.decodeFromString<SignInForm>(loginForm)
        } catch (e: SerializationException) {
            println("Error: login(): failed to deserialize form")
            return false
        }

        println("Notice: sign in attempt with user-code:${form.userCode}")

        // ensure the user exists
        val isValidUser = userCodeTaken(form.userCode)
        if (!isValidUser) return false

        // create new session, if needed
        val session: HttpSession = request.getSession(true)
        session.setAttribute("userCode", form.userCode)
        session.setAttribute("sessionNumber", SingletonControllers.db.getNextSessionNumber(form.userCode))

        return true
    }

    /** check if a user-code is assigned to a user */
    private fun userCodeTaken(userCode: String): Boolean {
        // println("userCodeTaken() called with user-code: ${userCode}")
        return SingletonControllers.db.userCodeTaken(userCode)
    }

    /** sign out a user; invalidates the user session */
    @PostMapping(value = ["/sign-out"])
    fun signOut(request: HttpServletRequest) {

        // ensure user is logged in
        if (isNotSignedIn(request)) return

        // get session, invalidate
        val session = request.getSession(false)
        session.invalidate()
    }

    /** check if a user is signed-in using their request */
    @PostMapping(value = ["/is-signed-in"])
    fun isSignedIn(request: HttpServletRequest): Boolean {
        // get session
        request.getSession(false) ?: return false
        return true
    }

    /** check if a user is not signed-in using their request */
    private fun isNotSignedIn(request: HttpServletRequest): Boolean {
        return !(isSignedIn(request))
    }

    /** get the next phrase from the session's number and phrase number */
    @PostMapping(value = ["/get-next-phrase"])
    fun getNextPhrase(request: HttpServletRequest): String? {

        // ensure user is logged in
        if (isNotSignedIn(request)) return null

        // get the user session, session number
        val session: HttpSession = request.getSession(false)
        val sessionNumber = session.getAttribute("sessionNumber") as Int

        // if first session -> init phrase count
        val isFirstSession = (sessionNumber == 1)
        if (isFirstSession) session.setAttribute("phraseNumber", 1)

        // completed typing session
        var phraseNumber = session.getAttribute("phraseNumber") as Int
        val hasCompletedTypingSession = (phraseNumber >= Singleton.phrasesPerSession)
        if (hasCompletedTypingSession) {
            // need to collect metrics before progressing
            return null
        }

        // update phrase number
        session.setAttribute("phraseNumber", phraseNumber + 1)
        phraseNumber = session.getAttribute("phraseNumber") as Int

        // get new phrase from session and new phrase numbers
        return Singleton.getPhrase(sessionNumber, phraseNumber)
    }

    /** get the user code attached to the session */
    @PostMapping(value = ["/get-user-code"])
    fun getUserCode(request: HttpServletRequest): String? {

        // ensure user is logged in
        if (isNotSignedIn(request)) return null

        val session: HttpSession = request.getSession(false)
        return session.getAttribute("userCode") as String
    }

    /** get the user code attached to the session */
    @PostMapping(value = ["/report-completed-session"])
    fun reportCompletedSession(@RequestBody metricsObj: String, request: HttpServletRequest): Boolean? {

        // ensure user is logged in
        if (isNotSignedIn(request)) return null

        val session: HttpSession = request.getSession(false)
        val userCode = session.getAttribute("userCode") as String

        // collect metrics
        val sessionData = try {
            Json.decodeFromString<TrainingSessionData>(metricsObj)
        } catch (e: SerializationException) {
            println("Error: login(): failed to deserialize form")
            return false
        }

        // validate session data object
        if (!validateSessionData(sessionData)) return null

        // store completed session in database
        SingletonControllers.db.storeCompletedSession(userCode, sessionData)

        // increment session number (if exists next session)
        session.setAttribute("sessionNumber", SingletonControllers.db.getNextSessionNumber(userCode))

        // report success
        return true
    }
}
