package tmcowley.appserver.utils

import org.assertj.core.api.Assertions.assertThatIOException
import org.junit.jupiter.api.Test

import org.springframework.boot.test.context.SpringBootTest

/** invalid file path tests for parsing */
@SpringBootTest
internal class ParsingKtTest {

    // given
    private val wrongFilePath = "not-a-file"

    @Test
    fun `get phrases with invalid file`() {
        // then
        assertThatIOException().isThrownBy {
            // when
            getPhrases(wrongFilePath)
        }
    }

    @Test
    fun `get word frequencies with invalid file`() {
        // then
        assertThatIOException().isThrownBy {
            // when
            getWordFrequencies(wrongFilePath)
        }
    }

    @Test
    fun `get words with invalid file`() {
        // then
        assertThatIOException().isThrownBy {
            // when
            getWords(wrongFilePath)
        }
    }

    @Test
    fun `get five-grams with invalid file`() {
        // then
        assertThatIOException().isThrownBy {
            // when
            getFiveGrams(wrongFilePath)
        }
    }
}
