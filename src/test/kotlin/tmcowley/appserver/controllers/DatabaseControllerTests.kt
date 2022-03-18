package tmcowley.appserver.controllers

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.springframework.boot.test.context.SpringBootTest

import org.jetbrains.exposed.sql.transactions.transaction

import java.lang.Exception

import tmcowley.appserver.Singleton
import tmcowley.appserver.objects.SessionData

@SpringBootTest
class DatabaseControllerTests {

    val db = DatabaseController()

    fun createUserGettingCode(): String? {
        return db.createNewUserGettingCode(userAge=23, typingSpeed=23)
    }

    fun createUser(): User? {
        return db.createNewUser(userAge=23, typingSpeed=23)
    }

    @Test
    fun `user-code taken` () {
        val nonMappingStrings = listOf( "", " ", "test", "test-test", "test-test-test" )
        nonMappingStrings.forEach { code -> assert(!(db.userCodeTaken(code))) }
    }

    @Test
	fun `user creation`() {
        repeat(10) {
            val userCode = db.createNewUserGettingCode(21, 60)
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

    @Test
    fun `get top completed session of new user`() {
        // for new user, next should be one
        val userCode: String? = createUserGettingCode()
        assertNotNull(userCode)
        val userInitCorrectly = (userCode != null && db.getNextSessionNumber(userCode) == 1)
        assert(userInitCorrectly)
    }

    @Test
    fun `create new session under user by user-code`() {
        // create new user
        val userCode: String = createUserGettingCode() ?: return 

        // get next available session number
        val nextSessionNumber = db.getNextSessionNumber(userCode) ?: return

        // add a session under the user
        val sessionAdded = db.storeCompletedSession(userCode, SessionData(speed=23f, accuracy=23f))
        assert(sessionAdded)

        // check next session number is incremented
        val newNextSessionNumber = db.getNextSessionNumber(userCode) ?: return
        assert(newNextSessionNumber == nextSessionNumber + 1)
    }

    @Test
    fun `get top completed session of (non-initial) user`() {
        // create new user
        val userCode: String = createUserGettingCode() ?: return 

        // add two sessions under the user
        var sessionAdded = db.storeCompletedSession(userCode, SessionData(speed=23f, accuracy=23f))
        assert(sessionAdded)
        sessionAdded = db.storeCompletedSession(userCode, SessionData(speed=23f, accuracy=23f))
        assert(sessionAdded)

        // check next session number is incremented
        val nextSessionNumber = db.getNextSessionNumber(userCode) ?: return
        assert(nextSessionNumber == 3)
    }

    @Test
    fun `get next session number`() {
        // for new user, next session should be one
        val userCode: String = createUserGettingCode() ?: return

        val nextSessionNumber = db.getNextSessionNumber(userCode)
        assert(nextSessionNumber == 1)
    }

    @Test
    fun `get user-id from user-code`() {
        // create a new user
        val user = createUser() ?: return
        val userCode = user.uid
        val correctUserId = user.id.value

        // lookup user-id from user-code
        val mappedUserId = db.getUserId(userCode)

        // ensure mapped id is correct
        assertEquals(correctUserId, mappedUserId)
    }
}
