package tmcowley.appserver.controllers


// https://junit.org/junit4/javadoc/4.8/org/junit/Assert.html
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Ignore

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

import tmcowley.appserver.Singleton

@SpringBootTest
class DatabaseControllerTest {

    val db = DatabaseController()

    @Test
	fun `user creation`(){
        repeat(10) {
            val userCode = db.createNewUser(21, 60)
            println(userCode)
            assertNotNull(userCode)
        }
    }
}