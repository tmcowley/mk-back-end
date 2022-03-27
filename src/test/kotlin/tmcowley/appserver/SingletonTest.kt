package tmcowley.appserver

import org.junit.jupiter.api.Test

// for assertions with smart-casts (nullability inferred)
import kotlin.test.assertNotNull
import kotlin.test.assertNull

import org.springframework.boot.test.context.SpringBootTest

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import tmcowley.appserver.models.Key
import tmcowley.appserver.models.KeyPair

@SpringBootTest
class SingletonTest {

    init {
        Singleton
    }

    @Test
    fun `user code generation`() {
        // given, when
        val userCode = Singleton.getRandomUserCode()

        // then
        assertThat(userCode).isNotEmpty
    }

    @Test
    fun `find longest phrase length`() {
        val longestPhrase = Singleton.phrases.maxByOrNull { phrase -> phrase.length } ?: ""
        println("\nLongest phrase length: ${longestPhrase.length}\n")
    }

    @Test
    fun `get longest word in word list`() {
        println("\nLongest word length: ${Singleton.maxWord?.length}\n")
    }

    @Test
    fun `get key pair with Char`() {
        // test alphabetic char

        val alphabeticKeyPair = Singleton.getKeyPairOrNull('q')
        val qMapping = KeyPair('q', 'p')

        assertNotNull(alphabeticKeyPair)
        assertThat(alphabeticKeyPair).isEqualTo(qMapping)

        // test non-alphabetic char

        val nonAlphabeticKeyPair = Singleton.getKeyPairOrNull('<')

        // non-alphabetic character should not be mapped
        assertNull(nonAlphabeticKeyPair)
    }

    @Test
    fun `get key pair with Key`() {
        // test alphabetic key

        val alphabeticKeyPair = Singleton.getKeyPairOrNull(Key('q'))
        val qMapping = KeyPair('q', 'p')

        assertNotNull(alphabeticKeyPair)
        assertThat(alphabeticKeyPair).isEqualTo(qMapping)

        // test non-alphabetic key

        val nonAlphabeticKeyPair = Singleton.getKeyPairOrNull(Key('<'))

        // non-alphabetic character should not be mapped
        assertNull(nonAlphabeticKeyPair)
    }

    @Nested
    @DisplayName("get phrase")
    inner class GetPhrase {
        private val invalidSessionNumbers = listOf(-100, -1, 0)
        private val validSessionNumbers = listOf(1, 5, 50)
        private val validSessionNumber = validSessionNumbers[0]

        private val invalidPhraseNumbers = listOf(-100, -1, 0, 9)
        private val validPhraseNumbers = listOf(1, 5, 8)
        private val validPhraseNumber = validPhraseNumbers[0]

        @Test
        fun `test invalid args`() {
            invalidSessionNumbers.forEach { invalidSessionNumber ->
                val nextPhrase = Singleton.getPhrase(invalidSessionNumber, validPhraseNumber)
                assertNull(nextPhrase)
            }

            invalidPhraseNumbers.forEach { invalidPhraseNumber ->
                val nextPhrase = Singleton.getPhrase(validSessionNumber, invalidPhraseNumber)
                assertNull(nextPhrase)
            }
        }

        @Test
        fun `test valid args`() {
            validSessionNumbers.forEach { validSessionNumber ->
                val nextPhrase = Singleton.getPhrase(validSessionNumber, validPhraseNumber)
                assertNotNull(nextPhrase)
            }

            validPhraseNumbers.forEach { validPhraseNumber ->
                val nextPhrase = Singleton.getPhrase(validSessionNumber, validPhraseNumber)
                assertNotNull(nextPhrase)
            }
        }
    }
}
