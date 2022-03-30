package tmcowley.appserver.controllers

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import org.junit.jupiter.api.Disabled
import tmcowley.appserver.SingletonControllers
import tmcowley.appserver.models.SignInForm
import tmcowley.appserver.models.SignUpForm
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class APIsPostTest {

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
        val userCode = SingletonControllers.db.createNewUserGettingCode(21, 60)
        assertNotNull(userCode)
        assertThat(SingletonControllers.db.userCodeTaken(userCode))
        return userCode
    }

    fun getSignedInRequest(): MockHttpServletRequest {
        // create a new user
        val userCode = createUser()

        return getSignedInRequest(userCode)
    }

    /** get a signed-in request */
    fun getSignedInRequest(userCode: String): MockHttpServletRequest {
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
    fun `sign up - with valid form`() {
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
    fun `sign up - with invalid form`() {
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
    fun `sign up - with invalidly serialized form`() {
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
    fun `sign in - with valid form`() {
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
    fun `sign in - with invalid form`() {
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
    fun `sign in - with invalidly serialized form`() {
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
    fun signOut() {
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
    fun isSignedIn() {
        // given

        // get signed-in user request
        val request = getSignedInRequest()

        // when
        val isSignedIn = apiInstance.isSignedIn(request)

        // then
        assertThat(isSignedIn)
    }

    @Disabled
    @Test
    fun `get next phrase - initial request`() {
        // TODO
    }

    @Disabled
    @Test
    fun `get next phrase - request at session one, phrase two`() {
        // TODO
    }

    @Disabled
    @Test
    fun `get next phrase - request at session two, phrase one`() {
        // TODO
    }

    @Test
    fun getUserCode() {
        // given

        // create new user
        val expectedUserCode = createUser()
        val request = getSignedInRequest(expectedUserCode)

        // when
        val actualUserCode = apiInstance.getUserCode(request)

        // then
        assertThat(actualUserCode).isEqualTo(expectedUserCode)
    }

    @Disabled
    @Test
    fun reportCompletedSession() {
        // TODO
    }
}