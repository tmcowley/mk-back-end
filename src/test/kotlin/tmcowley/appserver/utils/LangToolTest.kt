package tmcowley.appserver.utils

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.assertj.core.api.Assertions.assertThat
import tmcowley.appserver.Singleton

@SpringBootTest
internal class LangToolTest {

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