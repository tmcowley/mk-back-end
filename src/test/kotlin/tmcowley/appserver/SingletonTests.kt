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
import tmcowley.appserver.utils.splitIntoWords

// for assertions with smart-casts (nullability inferred)
import kotlin.test.assertNull
import kotlin.test.assertNotNull

@SpringBootTest
internal class SingletonTests {

    init {
        Singleton
    }

    @Test
    fun `word list should not contain empty string`() {
        // given
        val wordList = Singleton.words

        // then
        val containsEmptyString = wordList.any { word ->
            word == ""
        }
        assertThat(containsEmptyString).isFalse
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
            val sessions = (1 .. 20)
            val phrases = (1 .. 8)

            // for each of the first 20 sessions
            sessions.forEach { sessionNumber ->
                phrases.forEach { phraseNumber ->
                    val phraseCalledFirst = Singleton.getPhrase(sessionNumber, phraseNumber)
                    val phraseCalledSecond = Singleton.getPhrase(sessionNumber, phraseNumber)

                    // ensure phrases are deterministic
                    assertThat(phraseCalledFirst).isEqualTo(phraseCalledSecond)
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
            val sessions = (1 .. 5)

            sessions.forEach { sessionNumber ->
                val phrases = List(Singleton.phrasesPerSession) { phraseIndex  ->
                    val phraseNumber = phraseIndex + 1
                    // println("sessionNumber: $sessionNumber, phraseNumber: $phraseNumber")
                    Singleton.getPhrase(sessionNumber, phraseNumber)
                }.filterNotNull()

                // ensure session returns phrases
                assertThat(phrases).isNotEmpty

                // ensure the session had eight phrases
                assertThat(phrases.size).isEqualTo(8)

                // ensure all phrases are unique
                assertThat(phrases.distinct()).isEqualTo(phrases)
            }
        }

        @Test
        fun `session uniqueness`() {
            val sessionPhrases = List(5) { sessionIndex ->
                val sessionNumber = sessionIndex + 1
                // generate session phrase list
                List(Singleton.phrasesPerSession) { phraseIndex  ->
                    val phraseNumber = phraseIndex + 1
                    Singleton.getPhrase(sessionNumber, phraseNumber)
                }.filterNotNull()
            }

            // ensure sessions are unique
            assertThat(sessionPhrases.distinct()).isEqualTo(sessionPhrases)
        }

        @Test
        fun `test word-counts for the first 10 sessions`() {
            // all phrases from sessions one to ten
            val sessions = (1 .. 10)
            val phrases = (1 .. 8)

            sessions.forEach { sessionNumber ->
                var wordCount = 0
                phrases.forEach { phraseNumber ->
                    val phrase = Singleton.getPhrase(sessionNumber, phraseNumber)
                    assertNotNull(phrase)

                    val phraseAsList = splitIntoWords(phrase)
                    // println("phrase $phraseNumber of length ${phraseAsList.size}, $phrase")

                    wordCount += phraseAsList.size
                }
                println("Session $sessionNumber \tcontains \t$wordCount words.")
            }
        }

        @Test
        fun `test normalised word-counts for the first 10 sessions`() {
            // we define a word as 5 characters (4 letters and a space)
            val standardisedWordLength = 5

            // all phrases from sessions one to ten
            val sessions = (1 .. 10)
            val phrases = (1 .. 8)

            sessions.forEach { sessionNumber ->
                var characterCount = 0
                phrases.forEach { phraseNumber ->
                    val phrase = Singleton.getPhrase(sessionNumber, phraseNumber)
                    assertNotNull(phrase)

                    characterCount += phrase.length
                }

                val normalisedWordCount: Double = characterCount.toDouble() / standardisedWordLength
                println("Session $sessionNumber \tcontains \t$normalisedWordCount words (normalised).")
            }
        }
    }
}
