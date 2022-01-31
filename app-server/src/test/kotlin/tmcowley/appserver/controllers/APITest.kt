package tmcowley.appserver

import tmcowley.appserver.controllers.API

// https://junit.org/junit4/javadoc/4.8/org/junit/Assert.html
import org.junit.Assert.assertEquals;
import org.junit.Ignore;

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class APITest {

    val apiInstance: API = API();

	@Test
    @Ignore
	fun api_test(){

        // var requestBodies: Array<String>  = arrayOf( "requestBody", "requestBody2", "requestBody3" );

        // for (validReqBody: String in requestBodies) {
        //     assertEquals(kotlin.Unit, test(validReqBody))
        // }
    }

    @Test
    fun testSubmit(){

        apiInstance.submit("this is a test");
    }
}