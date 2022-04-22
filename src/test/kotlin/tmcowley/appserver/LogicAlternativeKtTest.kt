package tmcowley.appserver

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal class LogicAlternativeKtTest {

    @Test
    fun `getMatchedWordsUsingCartesianProduct - basic example`() {
        // given
        val word = "word"
        val wordInKeyPairForm = getWordInKeyPairForm(word)

        // when
        val matchedWords = getMatchedWordsUsingCartesianProduct(wordInKeyPairForm)

        // then
        assertThat(matchedWords).contains(word)
    }

    @Test
    fun `getSentencesUsingCartesianProduct - basic example`() {
        // given
        val listOfMatchedWords = listOf(
            setOf("a"),
            setOf("test", "tilt")
        )
        val results = listOf(
            "a test",
            "a tilt"
        )

        // when
        val matchedSentences = getSentencesUsingCartesianProduct(listOfMatchedWords)

        // then
        results.forEach { sentence ->
            assertThat(matchedSentences).contains(sentence)
        }
    }
}