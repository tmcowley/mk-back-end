package tmcowley.appserver

import tmcowley.appserver.convertToLeft
import tmcowley.appserver.convertToRight

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

import org.assertj.core.api.Assertions.assertThat

@SpringBootTest
class LogicTests {

	@Test
	fun `test left form conversion`() {
        val sentencesToLeftForm = mapOf(
            "qwert" to "qwert", 
            "poiuy" to "qwert", 
            "word" to "wwrd", 
            "" to "", 
            null to ""
        )

        sentencesToLeftForm.forEach{ (sentence, correctLeftForm) -> 
            assertThat(convertToLeft(sentence)).isEqualTo(correctLeftForm)
        }
	}
}
