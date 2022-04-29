package tmcowley.appserver.utils

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIOException
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

import org.springframework.boot.test.context.SpringBootTest
import tmcowley.appserver.models.Key

/** invalid file path tests for parsing */
@SpringBootTest
internal class ParsingKtTests {

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

    @Nested
    inner class MapOfKeysToKeyPairs {

        // given
        private val keysToKeyPairs = getKeyPairMap()

        /** ensure a -> (a, b) for all mappings */
        @Test
        fun `ensure key mappings include original key`() {
            // then
            keysToKeyPairs.forEach { (key, keyPair) ->
                assertThat(keyPair.contains(key))
            }
        }

        @Test
        fun `ensure full alphabet coverage`() {
            // then
            ('a' .. 'z').forEach { char ->
                val key = Key(char)
                assertThat(keysToKeyPairs.containsKey(key))
            }
        }
    }
}
