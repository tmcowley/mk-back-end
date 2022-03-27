package tmcowley.appserver

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Disabled

// for assertions with smart-casts (nullability inferred)
import kotlin.test.assertNotNull
import kotlin.test.assertNull

import org.springframework.boot.test.context.SpringBootTest

import org.assertj.core.api.Assertions.assertThat
import tmcowley.appserver.models.Key
import tmcowley.appserver.models.KeyPair

@Disabled
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
}
