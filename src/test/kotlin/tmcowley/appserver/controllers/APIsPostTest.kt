package tmcowley.appserver.controllers

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import org.junit.jupiter.api.Nested
import tmcowley.appserver.Singleton
import tmcowley.appserver.SingletonControllers
import tmcowley.appserver.models.SignInForm
import tmcowley.appserver.models.SignUpForm
import tmcowley.appserver.models.TrainingSessionData
import tmcowley.appserver.utils.validateSessionData
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class APIsPostTest {

    private val db = SingletonControllers.db
    private val apiInstance = APIsPost()

    /** get a fresh (unseen by server) request */
    private fun getFreshRequest(): MockHttpServletRequest {
        val freshRequest = MockHttpServletRequest()
        assertThat(apiInstance.isSignedIn(freshRequest)).isFalse
        return freshRequest
    }

    /** create a new user, get the user-code */
    private fun createUser(): String {
        // create new user, get user-code
        val userCode = db.createNewUserGettingCode(21, 60)
        assertNotNull(userCode)
        assertThat(db.userCodeTaken(userCode))
        return userCode
    }

    /** get a signed-in request with a fresh user */
    private fun getSignedInRequest(): MockHttpServletRequest {
        // create a new user
        val userCode = createUser()

        // sign-in user, get request
        val request = getSignedInRequest(userCode)

        // ensure session established correctly
        val session = request.getSession(false)
        assertNotNull(session)
        assertThat(session.getAttribute("sessionNumber")).isEqualTo(1)
        assertThat(session.getAttribute("phraseNumber")).isEqualTo(0)

        return request
    }

    /** get a signed-in request, with user-code */
    private fun getSignedInRequest(userCode: String): MockHttpServletRequest {
        // create sign in form
        val form = SignInForm(userCode)
        val formSerialized = Json.encodeToString(form)

        // get a fresh request
        val request = getFreshRequest()

        // sign in
        val signInSuccessful = apiInstance.signIn(formSerialized, request)

        // ensure signed in
        val isSignedIn = apiInstance.isSignedIn(request)
        assertThat(signInSuccessful)
        assertThat(isSignedIn)

        return request
    }

    @Test
    fun `sign-up - with valid form`() {
        // given
        val form = SignUpForm(21, 60)
        val formSerialized = Json.encodeToString(form)
        val request = getFreshRequest()

        // when
        val userCode = apiInstance.signUp(formSerialized, request)
        val isSignedIn = apiInstance.isSignedIn(request)

        // then
        assertNotNull(userCode)
        assertThat(isSignedIn)
    }

    @Test
    fun `sign-up - with invalid form`() {
        // given
        val form = SignUpForm(-10, 60)
        val formSerialized = Json.encodeToString(form)
        val request = getFreshRequest()

        // when
        val userCode = apiInstance.signUp(formSerialized, request)
        val isSignedIn = apiInstance.isSignedIn(request)

        // then
        assertNull(userCode)
        assertThat(isSignedIn).isFalse
    }

    @Test
    fun `sign-up - with invalidly serialized form`() {
        // given

        // serialization is deliberately invalid
        val formSerialized = "age: 21, speed: 60"
        val request = getFreshRequest()

        // when
        val userCode = apiInstance.signUp(formSerialized, request)
        val isSignedIn = apiInstance.isSignedIn(request)

        // then
        assertNull(userCode)
        assertThat(isSignedIn).isFalse
    }

    @Test
    fun `sign-in - with valid form`() {
        // given
        val userCode = createUser()
        val form = SignInForm(userCode)
        val formSerialized = Json.encodeToString(form)
        val request = getFreshRequest()

        // when
        val signInSuccessful = apiInstance.signIn(formSerialized, request)
        val isSignedIn = apiInstance.isSignedIn(request)

        // then
        assertThat(signInSuccessful)
        assertThat(isSignedIn)
    }

    @Test
    fun `sign-in - with invalid form`() {
        // given
        val userCode = "not-a-user"
        val form = SignInForm(userCode)
        val formSerialized = Json.encodeToString(form)
        val request = getFreshRequest()

        // when
        val signInSuccessful = apiInstance.signIn(formSerialized, request)
        val isSignedIn = apiInstance.isSignedIn(request)

        // then
        assertThat(signInSuccessful).isFalse
        assertThat(isSignedIn).isFalse
    }

    @Test
    fun `sign-in - with invalidly serialized form`() {
        // given
        val userCode = "not-a-user"

        // serialization is deliberately invalid
        val formSerialized = "userCode: $userCode"
        val request = getFreshRequest()

        // when
        val signInSuccessful = apiInstance.signIn(formSerialized, request)
        val isSignedIn = apiInstance.isSignedIn(request)

        // then
        assertThat(signInSuccessful).isFalse
        assertThat(isSignedIn).isFalse
    }

    @Test
    fun `sign-out`() {
        // given

        // get signed-in user request
        val request = getSignedInRequest()

        // when
        apiInstance.signOut(request)

        // then
        val isSignedIn = apiInstance.isSignedIn(request)
        assertThat(isSignedIn).isFalse
    }

    @Test
    fun `is signed-in`() {
        // given

        // get signed-in user request
        val request = getSignedInRequest()

        // when
        val isSignedIn = apiInstance.isSignedIn(request)

        // then
        assertThat(isSignedIn)
    }

    @Test
    fun `get next phrase - initial request`() {
        // given
        // new user signed in
        val request = getSignedInRequest()

        // when
        val phrase = apiInstance.getNextPhrase(request)
        val session = request.getSession(false)
        assertNotNull(session)
        assertThat(session.getAttribute("sessionNumber")).isEqualTo(1)
        assertThat(session.getAttribute("phraseNumber")).isEqualTo(1)

        // then
        assertNotNull(phrase)
        assertThat(phrase).isNotEmpty
        assertThat(phrase).isEqualTo(Singleton.getPhrase(1, 1))
    }

    @Test
    fun `get next phrase - request session one, phrase two`() {
        // given
        // existing user signed in
        val request = getSignedInRequest()
        val session = request.getSession(false)
        assertNotNull(session)
        session.setAttribute("sessionNumber", 1)
        session.setAttribute("phraseNumber", 1)

        // when
        val phrase = apiInstance.getNextPhrase(request)

        // then
        assertNotNull(phrase)
        assertThat(phrase).isNotEmpty
        assertThat(phrase).isEqualTo(Singleton.getPhrase(1, 2))

        // ensure second phrase does not collide with initial
        assertThat(phrase).isNotEqualTo(apiInstance.getNextPhrase(getSignedInRequest()))
    }

    @Test
    fun `get next phrase - request out of phrase bounds`() {
        // given
        // existing user signed in
        val request = getSignedInRequest()
        val session = request.getSession(false)
        assertNotNull(session)
        session.setAttribute("sessionNumber", 1)
        session.setAttribute("phraseNumber", Singleton.phrasesPerSession)

        // when
        // query (upper limit + 1)th phrase
        val phrase = apiInstance.getNextPhrase(request)

        // then
        assertNull(phrase)
    }

    @Test
    fun `get user-code`() {
        // given

        // create new user
        val expectedUserCode = createUser()
        val request = getSignedInRequest(expectedUserCode)

        // when
        val actualUserCode = apiInstance.getUserCode(request)

        // then
        assertThat(actualUserCode).isEqualTo(expectedUserCode)
    }

    @Test
    fun `get session number`() {
        // given signed-in user at session number n
        val request = getSignedInRequest()
        val session = request.getSession(false)
        assertNotNull(session)
        val expectedSessionNumber = 5
        session.setAttribute("sessionNumber", expectedSessionNumber)

        // when
        val sessionNumber = apiInstance.getSessionNumber(request)

        // then
        assertThat(sessionNumber).isEqualTo(expectedSessionNumber)
    }

    @Test
    fun `get phrase number`() {
        // given signed-in user at phrase number n
        val request = getSignedInRequest()
        val session = request.getSession(false)
        assertNotNull(session)
        val expectedPhraseNumber = Singleton.phrasesPerSession - 1
        session.setAttribute("phraseNumber", expectedPhraseNumber)

        // when
        val phraseNumber = apiInstance.getPhraseNumber(request)

        // then
        assertThat(phraseNumber).isEqualTo(expectedPhraseNumber)
    }

    @Nested
    inner class ReportCompletedSession {

        @Test
        fun `report completed session - passing`() {
            // given

            // signed-in new user, on session 1 final phrase
            val userCode = createUser()
            val request = getSignedInRequest(userCode)
            val session = request.getSession(false)
            assertNotNull(session)
            session.setAttribute("sessionNumber", 1)
            session.setAttribute("phraseNumber", Singleton.phrasesPerSession)

            // with valid metrics:
            val metrics = TrainingSessionData(60f, 100f)
            val metricsSerialized = Json.encodeToString(metrics)
            assertThat(validateSessionData(metrics))

            // when

            // we report completed session
            val trainingSessionAdded = apiInstance.reportCompletedSession(metricsSerialized, request)

            // then

            // ensure session added successfully
            assertThat(trainingSessionAdded)

            // check db
            val trainingSessions = db.getAllSessions(userCode)
            assertNotNull(trainingSessions)
            val trainingSession = trainingSessions.firstOrNull()
            assertNotNull(trainingSession)
            val speedsMatch = trainingSession.speed == metrics.speed
            val accuraciesMatch = trainingSession.accuracy == metrics.accuracy
            val trainingSessionMatchesEntry = (speedsMatch && accuraciesMatch)
            assertThat(trainingSessionMatchesEntry)

            // check attributes set accordingly
            val sessionNumber = session.getAttribute("sessionNumber")
            val phraseNumber = session.getAttribute("phraseNumber")

            assertThat(sessionNumber).isEqualTo(2)
            assertThat(phraseNumber).isEqualTo(0)
        }

        @Test
        fun `report completed session - failing - user not signed in`() {
            // given

            // non-signed-in request
            val request = getFreshRequest()

            // with valid metrics:
            val metrics = TrainingSessionData(60f, 100f)
            val metricsSerialized = Json.encodeToString(metrics)
            assertThat(validateSessionData(metrics))

            // when

            // we report completed session
            val trainingSessionAdded = apiInstance.reportCompletedSession(metricsSerialized, request)

            // then

            // ensure session addition fails
            assertThat(trainingSessionAdded).isFalse
        }

        @Test
        fun `report completed session - failing - form encoding invalid`() {
            // given

            // signed-in new user, on session 1 final phrase
            val userCode = createUser()
            val request = getSignedInRequest(userCode)
            val session = request.getSession(false)
            assertNotNull(session)
            session.setAttribute("sessionNumber", 1)
            session.setAttribute("phraseNumber", Singleton.phrasesPerSession)

            // with valid metrics (intentionally invalidly formatted)
            val metrics = TrainingSessionData(60f, 100f)
            val metricsIncorrectlySerialized = metrics.toString()

            // we report completed session
            val trainingSessionAdded = apiInstance.reportCompletedSession(metricsIncorrectlySerialized, request)

            // then

            // ensure session addition fails
            assertThat(trainingSessionAdded).isFalse
        }

        @Test
        fun `report completed session - failing - metrics invalid`() {
            // given

            // signed-in new user, on session 1 final phrase
            val userCode = createUser()
            val request = getSignedInRequest(userCode)
            val session = request.getSession(false)
            assertNotNull(session)
            session.setAttribute("sessionNumber", 1)
            session.setAttribute("phraseNumber", Singleton.phrasesPerSession)

            // with invalid metrics:
            val metrics = TrainingSessionData(60f, 101f)
            val metricsSerialized = Json.encodeToString(metrics)
            assertThat(validateSessionData(metrics)).isFalse

            // we report completed session
            val trainingSessionAdded = apiInstance.reportCompletedSession(metricsSerialized, request)

            // then

            // ensure session addition fails
            assertThat(trainingSessionAdded).isFalse
        }
    }
}