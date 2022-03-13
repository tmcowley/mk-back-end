package tmcowley.appserver.controllers

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Assertions.assertNotNull
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