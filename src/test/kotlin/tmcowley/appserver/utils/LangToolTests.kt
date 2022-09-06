package tmcowley.appserver.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import tmcowley.appserver.Singleton

@SpringBootTest
internal class LangToolTests {

    @Test
    fun countErrors() {
        val syntacticallyValidSentence = "This is a valid sentence."

        @Suppress("SpellCheckingInspection")
        val syntacticallyInvalidSentence = "This invald is a sentence."

        val validSentenceScore = Singleton.langTool.countErrors(syntacticallyValidSentence)
        val invalidSentenceScore = Singleton.langTool.countErrors(syntacticallyInvalidSentence)

        assertThat(validSentenceScore).isEqualTo(0)
        assertThat(invalidSentenceScore).isNotEqualTo(0)
    }
}