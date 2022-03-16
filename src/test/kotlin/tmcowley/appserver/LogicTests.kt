package tmcowley.appserver

import tmcowley.appserver.convertToLeft
import tmcowley.appserver.convertToRight

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class LogicTests {

	@Test
	fun `test conversion to left form`() {
        val sentencesToLeftForm = mapOf(
            "qwert" to "qwert", 
            "poiuy" to "qwert", 
            "word" to "wwrd", 
            "" to "", 
            null to ""
        )

        sentencesToLeftForm.forEach{ (sentence, correctResult) -> 
            assertEquals(correctResult, convertToLeft(sentence))
        }
	}

}
