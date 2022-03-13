package tmcowley.appserver.controllers

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.boot.test.context.SpringBootTest

import tmcowley.appserver.Singleton

@SpringBootTest
class APIsGetTest {

    val apiInstance: APIsGet = APIsGet()

    val phrase: String = "The house at the end of the street is red."
    val phraseLHS: String = "Tge gwrse at tge ebd wf tge street es red."
    val phraseRHS: String = "Thi houli ay yhi ink oj yhi lyuiiy il uik."

    @Disabled
	@Test
    /** for general debugging */
	fun `for debugging`(){
        println(Singleton.maxLengthInDictionary)
    }

    // -----

    @Test
	fun `loads`(){}

    @Test
    fun `submission`(){
        apiInstance.submit("this is a test");
    }

    @Test
    fun `conversions`(){
        assertEquals(phraseLHS, apiInstance.convertToLHS(phrase))
        assertEquals(phraseRHS, apiInstance.convertToRHS(phrase))
    }

    @Test
    fun `get random phrase`(){
        assert(!apiInstance.getRandomPhrase().isEmpty())
    }
}