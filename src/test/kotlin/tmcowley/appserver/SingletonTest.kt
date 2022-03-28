package tmcowley.appserver

import tmcowley.appserver.models.Key
import tmcowley.appserver.models.KeyPair

import org.springframework.boot.test.context.SpringBootTest

// junit5
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.DisplayName

// for fluent assertions
import org.assertj.core.api.Assertions.assertThat

// for assertions with smart-casts (nullability inferred)
import kotlin.test.assertNull
import kotlin.test.assertNotNull

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
        fun `invalid args`() {
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
        fun `valid args`() {
            validSessionNumbers.forEach { validSessionNumber ->
                val nextPhrase = Singleton.getPhrase(validSessionNumber, validPhraseNumber)
                assertNotNull(nextPhrase)
            }

            validPhraseNumbers.forEach { validPhraseNumber ->
                val nextPhrase = Singleton.getPhrase(validSessionNumber, validPhraseNumber)
                assertNotNull(nextPhrase)
            }
        }

        @Test
        fun `determinism for first 20 sessions`() {
            // for each of the first 20 sessions, ensure phrases are deterministic
            repeat(20) { sessionNumber ->
                repeat(8) { phraseNumber ->
                    val phraseN = Singleton.getPhrase(sessionNumber + 1, phraseNumber + 1)
                    assertThat(phraseN).isEqualTo(Singleton.getPhrase(sessionNumber + 1, phraseNumber + 1))
                }
            }
        }

        /**
         * to ensure the random mapping has worked as intended;
         * we should expect some phrase overlap as we test more sessions;
         * this test does not account for that tolerance
         */
        @Test
        fun `uniqueness in the first 5 sessions`() {
            // for each of the first five sessions, ensure phrases are unique
            repeat(5) { sessionNumber ->
                val phrases = List<String?>(8) { phraseNumber  ->
                    // println("sessionNumber: $sessionNumber, phraseNumber: $phraseNumber")
                    Singleton.getPhrase(sessionNumber + 1, phraseNumber + 1)
                }
                phrases.forEach { phrase -> println(phrase) }
                val nonNullPhrases = phrases.filterNotNull()

                // ensure no phrase was null
                assertThat(nonNullPhrases.size).isEqualTo(8)

                // ensure all phrases are unique
                assertThat(nonNullPhrases.distinct()).isEqualTo(nonNullPhrases)
            }
        }

        @Test
        fun `session uniqueness`() {
            val sessionPhrases = List<List<String>>(5) { sessionNumber ->
                // generate session phrase list
                List<String?>(8) { phraseNumber  ->
                    Singleton.getPhrase(sessionNumber + 1, phraseNumber + 1)
                }.filterNotNull()
            }

            // ensure sessions are unique
            assertThat(sessionPhrases.distinct()).isEqualTo(sessionPhrases)
        }
    }
}
