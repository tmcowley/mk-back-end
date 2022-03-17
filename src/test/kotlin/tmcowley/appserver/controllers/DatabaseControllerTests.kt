package tmcowley.appserver.controllers

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Assertions.assertNotNull
import org.springframework.boot.test.context.SpringBootTest

import org.jetbrains.exposed.sql.transactions.transaction

import java.lang.Exception

import tmcowley.appserver.Singleton

@SpringBootTest
class DatabaseControllerTests {

    val db = DatabaseController()

    @Test
    fun `user-code taken` () {
        val nonMappingStrings = listOf( "", " ", "test", "test-test", "test-test-test" )
        nonMappingStrings.forEach { code -> assert(!(db.userCodeTaken(code))) }
    }

    @Test
	fun `user creation`() {
        repeat(10) {
            val userCode = db.createNewUser(21, 60)
            // println(userCode)

            // ensure userCode is not null -> indicates unsuccessful addition
            assertNotNull(userCode)
            userCode ?: return

            // ensure addition worked correctly
            assert(db.userCodeTaken(userCode))
        }
    }

    @Test
    fun `adding users, sessions, sessions_to_users`() {
        transaction{
            val user = User.new {
                uid = "admin-admin-admin"
                age = 21
                speed = 60
            }

            val session = Session.new {
                number = 1
                speed = 60f
                accuracy = 70f
            }

            val session_to_user = Session_To_User.new {
                user_id = user.id
                session_id = session.id
            }

            commit()

            assert(User.all().contains(user))
            assert(Session.all().contains(session))
            assert(Session_To_User.all().contains(session_to_user))
        }
    }

    // TODO
    @Test
    fun `get top complteed session`() {}

    // TODO
    @Test
    fun `get next session number`() {}

    // TODO
    @Test
    fun `get user-id from user-code`() {}

    // TODO
    @Test
    fun `create new session under user by user-code`() {}
}
