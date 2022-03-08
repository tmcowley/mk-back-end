package tmcowley.appserver.controllers

// https://junit.org/junit4/javadoc/4.8/org/junit/Assert.html
import org.junit.Assert.assertEquals;
import org.junit.Ignore;

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

import tmcowley.appserver.Singleton

@SpringBootTest
class APITest {

    val apiInstance: API = API()

    val phrase: String = "The house at the end of the street is red."
    val phraseLHS: String = "Tge gwrse at tge ebd wf tge street es red."
    val phraseRHS: String = "Thi houli ay yhi ink oj yhi lyuiiy il uik."

	@Test
	fun `for debugging`(){
        // for general debugging (for the time being)
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