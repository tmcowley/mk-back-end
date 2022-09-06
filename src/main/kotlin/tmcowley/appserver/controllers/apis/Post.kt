package tmcowley.appserver.controllers.apis

import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.springframework.web.bind.annotation.*
import tmcowley.appserver.Singleton
import tmcowley.appserver.SingletonControllers
import tmcowley.appserver.models.SignInForm
import tmcowley.appserver.models.SignUpForm
import tmcowley.appserver.models.TrainingSessionData
import tmcowley.appserver.utils.validateSignUpForm
import tmcowley.appserver.utils.validateTrainingSessionData
import tmcowley.appserver.utils.validateUserCode
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

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
@RequestMapping(value = ["/api/v0"], consumes = ["application/json"], produces = ["application/json"])
class Post {

    private val db = SingletonControllers.db

    /**
     * sign up a user; creates a user in the db storing their age and normal (full-board) typing
     * speed returns the newly generated userCode
     */
    @PostMapping(value = ["/sign-up"])
    fun signUp(
        @RequestBody signupForm: String,
        request: HttpServletRequest
    ): String? {
        // deserialize sign-up form
        val form = try {
            Json.decodeFromString<SignUpForm>(signupForm)
        } catch (e: SerializationException) {
            println("Error: signUp(): failed to deserialize form")
            return null
        }

        if (!validateSignUpForm(form)) return null

        val userCode = db.createNewUserGettingCode(form.age, form.speed) ?: return null

        // create new session, if needed
        val session: HttpSession = request.getSession(true)
        session.setAttribute("userCode", userCode)
        session.setAttribute("sessionNumber", db.getNextSessionNumber(userCode))
        session.setAttribute("phraseNumber", 0)

        // get a random phrase from the phrase list
        return userCode
    }

    /** sign in a user; creates a session storing their userCode and current sessionNumber */
    @PostMapping(value = ["/sign-in"])
    fun signIn(@RequestBody signInForm: String, request: HttpServletRequest): Boolean {
        // deserialize sign-in form
        val form = try {
            Json.decodeFromString<SignInForm>(signInForm)
        } catch (e: SerializationException) {
            println("Error: signIn(): failed to deserialize form")
            return false
        }

        val userCode = form.userCode.lowercase()

        println("Notice: sign in attempt with user-code:${userCode}")

        // validate code format
        if (!validateUserCode(userCode)) return false

        // ensure the user exists
        val isValidUser = userCodeTaken(userCode)
        if (!isValidUser) return false

        // create new session, if needed
        val session: HttpSession = request.getSession(true)
        session.setAttribute("userCode", userCode)
        session.setAttribute("sessionNumber", db.getNextSessionNumber(form.userCode))
        session.setAttribute("phraseNumber", 0)

        return true
    }

    /** check if a user-code is assigned to a user */
    private fun userCodeTaken(userCode: String): Boolean {
        // println("userCodeTaken() called with user-code: ${userCode}")
        return db.userCodeTaken(userCode)
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

        // session phrases exhausted
        var phraseNumber = session.getAttribute("phraseNumber") as Int
        val hasCompletedTypingSession = (phraseNumber == Singleton.phrasesPerSession)
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

        val session = request.getSession(false)
        return session.getAttribute("userCode") as String
    }

    /** get the current session number */
    @PostMapping(value = ["/get-session-number"])
    fun getSessionNumber(request: HttpServletRequest): Int? {
        // ensure user is logged in
        if (isNotSignedIn(request)) return null

        val session = request.getSession(false)
        return session.getAttribute("sessionNumber") as Int
    }

    /** get the current phrase number */
    @PostMapping(value = ["/get-phrase-number"])
    fun getPhraseNumber(request: HttpServletRequest): Int? {
        // ensure user is logged in
        if (isNotSignedIn(request)) return null

        val session = request.getSession(false)
        return session.getAttribute("phraseNumber") as Int
    }

    /** get the user code attached to the session */
    @PostMapping(value = ["/report-completed-session"])
    fun reportCompletedSession(@RequestBody metricsObj: String, request: HttpServletRequest): Boolean {

        println("reportCompletedSession called")

        // ensure user is logged in
        if (isNotSignedIn(request)) println("error: is not signed in")
        if (isNotSignedIn(request)) return false

        val session = request.getSession(false)
        val userCode = session.getAttribute("userCode") as String
        val phraseNumber = session.getAttribute("phraseNumber") as Int

        val isNotLastPhrase = (phraseNumber != Singleton.phrasesPerSession)
        if (isNotLastPhrase) println("error: is not last phrase, phraseNumber: $phraseNumber")
        if (isNotLastPhrase) return false

        // collect metrics
        val sessionData = try {
            Json.decodeFromString<TrainingSessionData>(metricsObj)
        } catch (e: SerializationException) {
            println("Error: login(): failed to deserialize form")
            return false
        }

        // validate session data object
        if (!validateTrainingSessionData(sessionData)) println("training session data invalid: $sessionData")
        if (!validateTrainingSessionData(sessionData)) return false

        // store completed session in database
        db.storeCompletedSession(userCode, sessionData)

        // increment session number (if exists next session); reset phrase number
        session.setAttribute("sessionNumber", db.getNextSessionNumber(userCode))
        session.setAttribute("phraseNumber", 0)

        // output training session data

        println("Sessions for user-code: $userCode")

        // display full-keyboard typing speed
        val fullBoardTypingSpeed = db.getAllSessions(userCode)?.find { trainingSession ->
            trainingSession.number == 0
        }
        println("Full-keyboard typing speed: ${fullBoardTypingSpeed?.speed}")

        // output all half-board training sessions
        db.getAllSessions(userCode)
            ?.filterNot { trainingSession ->
                // remove the full-board typing speed entry (under number 0)
                trainingSession.number == 0
            }
            ?.forEach { trainingSession ->
                // output the training session data
                println("Training session: ${trainingSession.number} has wpm: ${trainingSession.speed}, and accuracy: ${trainingSession.accuracy}")
            }

        // report success
        return true
    }
}
