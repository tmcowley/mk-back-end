package tmcowley.appserver.controllers

import org.junit.jupiter.api.Test

import org.assertj.core.api.Assertions.assertThat

import org.springframework.boot.test.context.SpringBootTest

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
    fun `side conversions`() {
        assertThat(apiInstance.convertToLHS(phrase)).isEqualTo(phraseLHS)
        assertThat(apiInstance.convertToRHS(phrase)).isEqualTo(phraseRHS)
    }

    @Test
    fun `get random phrase`() {
        assertThat(apiInstance.getRandomPhrase()).isNotEmpty
    }
}