package tmcowley.appserver.controllers

import org.junit.jupiter.api.Test

import org.assertj.core.api.Assertions.assertThat

import org.springframework.boot.test.context.SpringBootTest
import tmcowley.appserver.Singleton

@SpringBootTest
class APIsGetTests {

    val apiInstance = APIsGet()

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

    @Test
    fun `basic submission with whole number`() {
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
}