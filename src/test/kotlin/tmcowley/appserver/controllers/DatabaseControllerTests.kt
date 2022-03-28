package tmcowley.appserver.controllers

import org.junit.jupiter.api.Test

import org.assertj.core.api.Assertions.assertThat

// for assertions with smart-casts (nullability inferred)
import kotlin.test.assertNotNull
import kotlin.test.assertNull

import org.springframework.boot.test.context.SpringBootTest

import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Nested

import tmcowley.appserver.models.SessionData

@SpringBootTest
class DatabaseControllerTests {

    val db = DatabaseController()

    fun createUserGettingCode(): String? {
        return db.createNewUserGettingCode(
            userAge = 23,
            typingSpeed = 23
        )
    }

    fun createUser(): User? {
        return db.createNewUser(
            userAge = 23,
            typingSpeed = 23
        )
    }

    @Test
    fun `user-code taken`() {
        val nonMappingStrings = listOf("", " ", "test", "test-test", "test-test-test")
        nonMappingStrings.forEach { code -> assertThat(db.userCodeTaken(code)).isFalse() }
    }

    @Test
    fun `user creation`() {
        repeat(10) {
            val userCode = db.createNewUserGettingCode(21, 60)
            // println(userCode)

            // ensure userCode is not null -> indicates unsuccessful addition
            assertNotNull(userCode)

            // ensure addition worked correctly
            assertThat(db.userCodeTaken(userCode)).isTrue()
        }
    }

    @Test
    fun `user creation getting id`() {
        val newUserId = db.createNewUserGettingId(23, 23)
        assertNotNull(newUserId)
    }

    @Test
    fun `adding users, sessions, sessions_to_users`() {
        transaction {
            val user = User.new {
                userCode = "admin-admin-admin"
                age = 21
                speed = 60
            }

            val session = Session.new {
                number = 1
                speed = 60f
                accuracy = 70f
            }

            val sessionToUser = SessionToUser.new {
                userId = user.id
                sessionId = session.id
            }

            commit()

            assertThat(User.all()).contains(user)
            assertThat(Session.all()).contains(session)
            assertThat(SessionToUser.all()).contains(sessionToUser)
        }
    }

    @Test
    fun `get top completed session of new user`() {
        val userCode: String? = createUserGettingCode()

        assertThat(userCode).isNotNull
        userCode ?: return

        // for new user, next session number should be one
        assertThat(db.getNextSessionNumber(userCode)).isEqualTo(1)
    }

    @Test
    fun `create new session under user by user-code`() {
        // create new user
        val userCode: String = createUserGettingCode() ?: return

        // get next available session number
        val nextSessionNumber = db.getNextSessionNumber(userCode) ?: return

        // add a session under the user
        val sessionAdded = db.storeCompletedSession(userCode, SessionData(speed = 23f, accuracy = 23f))
        assertThat(sessionAdded)

        // check next session number is incremented
        val newNextSessionNumber = db.getNextSessionNumber(userCode) ?: return
        assertThat(newNextSessionNumber).isEqualTo(nextSessionNumber + 1)
    }

    @Test
    fun `get top completed session of (non-initial) user`() {
        // create new user
        val userCode: String = createUserGettingCode() ?: return

        // add two sessions under the user
        var sessionAdded = db.storeCompletedSession(userCode, SessionData(speed = 23f, accuracy = 23f))
        assertThat(sessionAdded)
        sessionAdded = db.storeCompletedSession(userCode, SessionData(speed = 23f, accuracy = 23f))
        assertThat(sessionAdded)

        // check next session number is incremented
        val nextSessionNumber = db.getNextSessionNumber(userCode) ?: return
        assertThat(nextSessionNumber).isEqualTo(3)
    }

    @Test
    fun `get next session number`() {
        // for new user, next session should be one
        val userCode: String = createUserGettingCode() ?: return

        val nextSessionNumber = db.getNextSessionNumber(userCode)
        assertThat(nextSessionNumber).isEqualTo(1)
    }

    @Test
    fun `get user-id from user-code`() {
        // create a new user
        val user = createUser() ?: return
        val userCode = user.userCode
        val correctUserId = user.id.value

        // lookup user-id from user-code
        val mappedUserId = db.getUserId(userCode)

        // ensure mapped id is correct
        assertThat(mappedUserId).isEqualTo(correctUserId)
    }

    @Nested
    inner class GetAllSessions {

        private val invalidUserCode = "invalid-user-code"

        @Test
        fun `invalid arguments`() {
            // test an invalid user-code
            val sessionsOfInvalidUser = db.getAllSessions(invalidUserCode)
            assertNull(sessionsOfInvalidUser)
        }

        @Test
        fun `for a new user`() {
            val newUser = createUser()
            assertNotNull(newUser)

            val sessions = db.getAllSessions(newUser.userCode)
            assertNotNull(sessions)

            // ensure the user has a single session
            assertThat(sessions.size).isEqualTo(1)

            // and that this single session is the completed zero-session
            val firstSession = sessions.firstOrNull() ?: return
            assertThat(firstSession.number).isEqualTo(0)
        }

        @Disabled
        @Test
        fun `for a user with recorded sessions`() {
            // TODO
        }
    }
}
