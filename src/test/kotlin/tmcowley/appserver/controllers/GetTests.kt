package tmcowley.appserver.controllers

import org.junit.jupiter.api.Test

import org.assertj.core.api.Assertions.assertThat

import org.springframework.boot.test.context.SpringBootTest
import tmcowley.appserver.Singleton
import tmcowley.appserver.SingletonControllers
import tmcowley.appserver.controllers.apis.Get
import tmcowley.appserver.utils.reportTrainingSession
import tmcowley.appserver.utils.createUser
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties

@SpringBootTest
class GetTests {

    val apiInstance = Get()
    val db = SingletonControllers.db

    /** set the syntax frequency analysis enabled state */
    private fun setSyntaxAnalysis(state: Boolean) {
        setAnalysisProperty("syntaxAnalysisEnabled", state)
        assertThat(Singleton.syntaxAnalysisEnabled).isEqualTo(state)
    }

    /** set the internal frequency analysis enabled state */
    private fun setFrequencyAnalysis(state: Boolean) {
        setAnalysisProperty("frequencyAnalysisEnabled", state)
        assertThat(Singleton.syntaxAnalysisEnabled).isEqualTo(state)
    }

    /** set the internal analysis tool state; uses reflection */
    private fun setAnalysisProperty(name: String, state: Boolean) {
        val singletonKClass = Singleton::class
        val property = singletonKClass.memberProperties.find { property ->
            property.name == name
        }
        // ensure property is mutable
        if (property is KMutableProperty<*>) {
            property.setter.call(Singleton, state)
        }
    }

    /** reset analysis properties */
    private fun resetAnalysisProperties() {
        Singleton.resetAnalysisEnabledStates()
    }

    @Test
    fun `basic submission`() {
        // given
        val phrase = "this is a test"
        val matches = listOf("this is a test", "this is a tilt")

        // when
        val results = apiInstance.submit(phrase)

        // then
        matches.forEach { match -> assertThat(results).contains(match) }
    }

    @Test
    fun `basic submission - word`() {
        // given
        val word = "zero"
        val matches = listOf(word)

        // when
        val results = apiInstance.submit(word)

        // then
        assertThat(Singleton.wordExists(word))
        matches.forEach { match -> assertThat(results).contains(match) }
    }

    /**
     * tests all analysis combinations: {syntax, frequency}, {syntax, no frequency}, ...
     */
    @Test
    fun `basic submission with analysis combinations`() {

        val booleanStates = listOf(true, false)

        booleanStates.zip(booleanStates) { syntaxState, analysisState ->
            // given
            setSyntaxAnalysis(syntaxState)
            setFrequencyAnalysis(analysisState)

            // when, then
            `basic submission`()

            // reset properties
            resetAnalysisProperties()
        }
    }

    @Test
    fun `basic submission containing whole number`() {
        // given
        val phrase = "there are 500 people"
        val matches = listOf(phrase)

        // when
        val results = apiInstance.submit(phrase)

        // then
        matches.forEach { match -> assertThat(results).contains(match) }
    }

    @Test
    fun `basic submission containing decimal number`() {
        // given
        val phrase = "the probability is 0.5"
        val matches = listOf(phrase)

        // when
        val results = apiInstance.submit(phrase)

        // then
        matches.forEach { match -> assertThat(results).contains(match) }
    }

    @Test
    fun `basic submission containing error`() {
        // given
        @Suppress("SpellCheckingInspection")
        val phrase = "this is a mistake asdf"

        @Suppress("SpellCheckingInspection")
        val matches = listOf("this is a mistake {asdf}")

        // when
        val results = apiInstance.submit(phrase)

        // then
        matches.forEach { match -> assertThat(results).contains(match) }
    }

    @Test
    fun `side conversions - left`() {
        // given
        val phrase = "The house at the end of the street is red."

        @Suppress("SpellCheckingInspection")
        val phraseLHS = "Tge gwrse at tge ebd wf tge street es red."

        // when
        val phraseConverted = apiInstance.convertToLHS(phrase)

        // then
        assertThat(phraseConverted).isEqualTo(phraseLHS)
    }

    @Test
    fun `side conversions - right`() {
        // given
        val phrase = "The house at the end of the street is red."

        @Suppress("SpellCheckingInspection")
        val phraseRHS = "Thi houli ay yhi ink oj yhi lyuiiy il uik."

        // when
        val phraseConverted = apiInstance.convertToRHS(phrase)

        // then
        assertThat(phraseConverted).isEqualTo(phraseRHS)
    }

    @Test
    fun `get random phrase`() {
        // when
        val randomPhrase = apiInstance.getRandomPhrase()

        // then
        assertThat(randomPhrase).isNotEmpty
    }

    @Test
    fun `get status`() {
        // when
        val status = apiInstance.status()

        // then
        assertThat(status).isTrue
    }

    @Test
    fun `get phrases per session`() {
        // when
        val phrasesPerSession = apiInstance.getPhrasesPerSession()

        // then
        assertThat(phrasesPerSession).isEqualTo(Singleton.phrasesPerSession)
    }

    @Test
    fun `get user-id to sessions map - size check`() {
        // given
        val userCount = db.countUsers()

        // when
        val userIdToSessions = apiInstance.getSessionsForEachUser()

        // then
        assertThat(userIdToSessions.size).isEqualTo(userCount)
    }

    @Test
    fun `get user-id to sessions map - deep check`() {
        val userIdToSessions = apiInstance.getSessionsForEachUserById()

        // given user-id, sessions
        userIdToSessions.forEach { (userId, sessions) ->
            // then
            val expectedSessions = db.getAllSessions(userId)
            assertThat(sessions).isEqualTo(expectedSessions)
        }
    }

    @Test
    fun `getSessionsForEachUser - add some users`() {
        // given
        // at least 5 users in database
        val users = buildSet {
            repeat(5) {
                // create a new user
                val user = createUser(db)

                // add 5 training sessions to this user
                repeat(5) {
                    reportTrainingSession(db, user)
                }

                // add user to users set
                add(user)
            }
        }

        // when
        // we query the user to sessions map
        val userToSessions = apiInstance.getSessionsForEachUser()

        // then
        // ensure the map contains the 5 added users
        users.forEach { user ->
            assertThat(userToSessions.contains(user))
        }
    }

    @Test
    fun `getSessionsForEachUserById - add some users`() {
        // given
        // at least 5 users in database
        val users = buildSet {
            repeat(5) {
                // create a new user
                val user = createUser(db)

                // add 5 training sessions to this user
                repeat(5) {
                    reportTrainingSession(db, user)
                }

                // add user to users set
                add(user)
            }
        }

        // when
        // we query the user to sessions map
        val userToSessions = apiInstance.getSessionsForEachUserById()

        // then
        // ensure the map contains the 5 added users
        users.forEach { user ->
            assertThat(userToSessions.contains(user.id.value))
        }
    }

    @Test
    fun `getSessionsForEachUserByCode - add some users`() {
        // given
        // at least 5 users in database
        val users = buildSet {
            repeat(5) {
                // create a new user
                val user = createUser(db)

                // add 5 training sessions to this user
                repeat(5) {
                    reportTrainingSession(db, user)
                }

                // add user to users set
                add(user)
            }
        }

        // when
        // we query the user to sessions map
        val userToSessions = apiInstance.getSessionsForEachUserByCode()

        // then
        // ensure the map contains the 5 added users
        users.forEach { user ->
            assertThat(userToSessions.contains(user.userCode))
        }
    }
}