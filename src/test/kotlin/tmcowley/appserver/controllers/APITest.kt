package tmcowley.appserver

import tmcowley.appserver.controllers.API

// https://junit.org/junit4/javadoc/4.8/org/junit/Assert.html
import org.junit.Assert.assertEquals;
import org.junit.Ignore;

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

import tmcowley.appserver.Singleton

@SpringBootTest
class APITest {

    val apiInstance: API = API();


	@Test
	fun api_test(){
        // for general debugging (for the time being)
        println(Singleton.maxLengthInDictionary)
    }

    @Test
    fun testSubmit(){

        // apiInstance.submit("this is a test");
    }
}