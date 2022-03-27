package tmcowley.appserver

import org.junit.jupiter.api.Test

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
}
