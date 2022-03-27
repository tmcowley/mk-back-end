package tmcowley.appserver.controllers

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Disabled

import org.assertj.core.api.Assertions.assertThat

import org.springframework.boot.test.context.SpringBootTest

@Disabled
@SpringBootTest
class APIsGetTests {

    val apiInstance: APIsGet = APIsGet()

    val phrase: String = "The house at the end of the street is red."

    @SuppressWarnings("SpellCheckingInspection")
    val phraseLHS: String = "Tge gwrse at tge ebd wf tge street es red."

    @SuppressWarnings("SpellCheckingInspection")
    val phraseRHS: String = "Thi houli ay yhi ink oj yhi lyuiiy il uik."

    @Disabled
    @Test
            /** for general debugging */
    fun `for debugging`() {
    }

    // -----

    @Test
    fun `context loads`() {
    }

    @Test
    fun `basic submission`() {
        val phrase = "this is a test"
        val matches = listOf("tges es a test", "this is a test")
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