package tmcowley.appserver.controllers

import org.junit.jupiter.api.Test

import org.assertj.core.api.Assertions.assertThat

import org.springframework.boot.test.context.SpringBootTest
import tmcowley.appserver.Singleton
import tmcowley.appserver.SingletonControllers
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties

@SpringBootTest
class APIsGetTests {

    val apiInstance = APIsGet()
    val db = SingletonControllers.db

    val phrase: String = "The house at the end of the street is red."

    @Suppress("SpellCheckingInspection")
    val phraseLHS: String = "Tge gwrse at tge ebd wf tge street es red."

    @Suppress("SpellCheckingInspection")
    val phraseRHS: String = "Thi houli ay yhi ink oj yhi lyuiiy il uik."

    // -----

    @Test
    fun `basic submission`() {
        val phrase = "this is a test"
        val matches = listOf("this is a test", "this is a tilt")
        val results = apiInstance.submit(phrase)

        matches.forEach { match -> assertThat(results).contains(match) }
    }

    private fun setSyntaxAnalysis(state: Boolean) {
        setAnalysisProperty("syntaxAnalysisEnabled", state)
        assertThat(Singleton.syntaxAnalysisEnabled).isEqualTo(state)
    }

    private fun setFrequencyAnalysis(state: Boolean) {
        setAnalysisProperty("frequencyAnalysisEnabled", state)
        assertThat(Singleton.syntaxAnalysisEnabled).isEqualTo(state)
    }

    private fun setAnalysisProperty(name: String, state: Boolean) {
        // use reflection to manually enable syntax analysis
        val singletonKClass = Singleton::class
        val property = singletonKClass.memberProperties.find { property ->
            property.name == name
        }
        if (property is KMutableProperty<*>) {
            property.setter.call(Singleton, state)
        }
    }

    /** reset analysis properties */
    private fun resetAnalysisProperties() {
        Singleton.resetAnalysisEnabledStates()
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
    fun `basic submission with whole number`() {

        assertThat(Singleton.syntaxAnalysisEnabled).isFalse

        val phrase = "there are 500 people"
        val matches = listOf(phrase)
        val results = apiInstance.submit(phrase)

        matches.forEach { match -> assertThat(results).contains(match) }
    }

    @Test
    fun `basic submission with decimal number`() {
        val phrase = "the probability is 0.5"
        val matches = listOf(phrase)
        val results = apiInstance.submit(phrase)

        matches.forEach { match -> assertThat(results).contains(match) }
    }

    @Test
    fun `basic submission with error`() {

        @Suppress("SpellCheckingInspection")
        val phrase = "this is a mistake asdf"

        @Suppress("SpellCheckingInspection")
        val matches = listOf("this is a mistake {asdf}")
        val results = apiInstance.submit(phrase)

        matches.forEach { match -> assertThat(results).contains(match) }
    }

    @Test
    fun `side conversions`() {
        assertThat(apiInstance.convertToLHS(phrase)).isEqualTo(phraseLHS)
        assertThat(apiInstance.convertToRHS(phrase)).isEqualTo(phraseRHS)
    }

    @Test
    fun `get random phrase`() {
        assertThat(apiInstance.getRandomPhrase()).isNotEmpty
    }

    @Test
    fun `get status`() {
        assertThat(apiInstance.status()).isTrue
    }

    @Test
    fun `get phrases per session`() {
        assertThat(apiInstance.getPhrasesPerSession()).isEqualTo(Singleton.phrasesPerSession)
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
        // given
        val userIdToSessions = apiInstance.getSessionsForEachUser()

        userIdToSessions.forEach { (userId, sessions) ->
            // then
            val expectedSessions = db.getAllSessions(userId)
            assertThat(sessions).isEqualTo(expectedSessions)
        }
    }
}