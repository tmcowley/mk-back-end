package tmcowley.appserver

import org.junit.jupiter.api.Test

// for assertions with smart-casts (nullability inferred)
import org.assertj.core.api.Assertions.assertThat

import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class LogicTests {

    @Test
    fun `test left form conversion`() {
        @Suppress("SpellCheckingInspection")
        val sentencesToLeftForm = mapOf(
            "qwert" to "qwert",
            "poiuy" to "qwert",
            "word" to "wwrd",
            "" to "",
            null to ""
        )

        sentencesToLeftForm.forEach { (sentence, correctLeftForm) ->
            assertThat(convertToLeft(sentence)).isEqualTo(correctLeftForm)
        }
    }

    @Test
    fun `test right form conversion`() {
        @Suppress("SpellCheckingInspection")
        val sentencesToRightForm = mapOf(
            "qwert" to "poiuy",
            "poiuy" to "poiuy",
            "word" to "oouk",
            "" to "",
            null to ""
        )

        sentencesToRightForm.forEach { (sentence, correctRightForm) ->
            assertThat(convertToRight(sentence)).isEqualTo(correctRightForm)
        }
    }
}
