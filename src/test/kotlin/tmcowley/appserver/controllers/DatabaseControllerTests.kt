package tmcowley.appserver.controllers

import org.junit.jupiter.api.Test

import org.assertj.core.api.Assertions.assertThat

// for assertions with smart-casts (nullability inferred)
import kotlin.test.assertNotNull
import kotlin.test.assertNull

import org.springframework.boot.test.context.SpringBootTest

import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Nested

import tmcowley.appserver.models.TrainingSessionData

import tmcowley.appserver.utils.createUserGettingCode
import tmcowley.appserver.utils.createUser

@SpringBootTest
internal class DatabaseControllerTests {

    private val db = DatabaseController()

    @Test
    fun `user-code taken`() {
        // given
        val nonMappingStrings = listOf("", " ", "test", "test-test", "test-test-test")
        nonMappingStrings.forEach { code ->
            // then
            assertThat(db.userCodeTaken(code)).isFalse
        }
    }

    @Test
    fun `user creation`() {
        repeat(10) {
            // when
            val userCode = db.createNewUserGettingCode(21, 60)

            // then

            // ensure successful creation
            assertNotNull(userCode)

            // ensure successful db addition
            assertThat(db.userCodeTaken(userCode))
        }
    }

    @Test
    fun `user creation getting id`() {
        // when
        val newUserId = db.createNewUserGettingId(23, 23)

        // then
        assertNotNull(newUserId)
    }

    @Test
    fun `adding users, sessions, sessions_to_users`() {
        transaction {
            // when

            val user = User.new {
                userCode = "admin-admin-admin"
                age = 21
                speed = 60
            }

            val trainingSession = TrainingSession.new {
                number = 1
                speed = 60f
                accuracy = 70f
            }

            val sessionToUser = SessionToUser.new {
                userId = user.id
                sessionId = trainingSession.id
            }

            commit()

            // then
            assertThat(User.all()).contains(user)
            assertThat(TrainingSession.all()).contains(trainingSession)
            assertThat(SessionToUser.all()).contains(sessionToUser)
        }
    }

    @Test
    fun `get top completed session of new user`() {
        // when
        val userCode = createUserGettingCode(this.db)

        // then
        // for new user, next session number should be one
        assertThat(db.getNextSessionNumber(userCode)).isEqualTo(1)
    }

    @Test
    fun `create new session under user by user-code`() {
        // given
        // fresh user created
        val userCode = createUserGettingCode(this.db)

        // get next available session number
        val nextSessionNumber = db.getNextSessionNumber(userCode)
        assertNotNull(nextSessionNumber)

        // when
        // add a session under the user
        val sessionAdded = db.storeCompletedSession(userCode, TrainingSessionData(speed = 23f, accuracy = 23f))
        assertThat(sessionAdded)

        // then
        // check next session number is incremented
        val newNextSessionNumber = db.getNextSessionNumber(userCode)
        assertNotNull(newNextSessionNumber)
        assertThat(newNextSessionNumber).isEqualTo(nextSessionNumber + 1)
    }

    @Test
    fun `get top completed session of (non-initial) user`() {
        // given
        // fresh user created
        val userCode = createUserGettingCode(this.db)

        // when
        // add two sessions under the user
        var sessionAdded = db.storeCompletedSession(userCode, TrainingSessionData(speed = 23f, accuracy = 23f))
        assertThat(sessionAdded)
        sessionAdded = db.storeCompletedSession(userCode, TrainingSessionData(speed = 23f, accuracy = 23f))
        assertThat(sessionAdded)

        // then
        // next session number is incremented
        val nextSessionNumber = db.getNextSessionNumber(userCode)
        assertNotNull(nextSessionNumber)
        assertThat(nextSessionNumber).isEqualTo(3)
    }

    @Test
    fun `get next session number`() {
        // given
        // fresh user created
        val userCode = createUserGettingCode(this.db)

        // when
        val nextSessionNumber = db.getNextSessionNumber(userCode)

        // then
        // next session should be one
        assertThat(nextSessionNumber).isEqualTo(1)
    }

    @Test
    fun `get user-id from user-code`() {
        // given
        // fresh user created
        val user = createUser(this.db)
        val userCode = user.userCode
        val correctUserId = user.id.value

        // when
        // lookup user-id from user-code
        val mappedUserId = db.getUserId(userCode)

        // then
        // ensure mapped id is correct
        assertThat(mappedUserId).isEqualTo(correctUserId)
    }

    @Test
    fun `count users`() {
        // given (at least one user)
        createUser(this.db)

        // when
        val count = db.countUsers()

        // then
        assertThat(count).isGreaterThan(0)
    }

    @Nested
    inner class GetAllTrainingSessions {

        // given
        private val invalidUserCode = "invalid-user-code"

        @Test
        fun `invalid arguments`() {
            // when
            // we query sessions of an invalid user
            val sessionsOfInvalidUser = db.getAllSessions(invalidUserCode)

            // then
            // the query should return null
            assertNull(sessionsOfInvalidUser)
        }

        @Test
        fun `for a new user`() {
            // given
            // fresh user created
            val userCode = createUserGettingCode(db)

            // when
            // we query the user's sessions
            val sessions = db.getAllSessions(userCode)

            // then

            assertNotNull(sessions)

            // ensure the user has a single session
            assertThat(sessions.size).isEqualTo(1)

            // and that this single session is the completed zero-session
            val firstSession = sessions.firstOrNull()
            assertNotNull(firstSession)
            assertThat(firstSession.number).isEqualTo(0)
        }

        @Test
        fun `for a user with recorded sessions`() {
            // given
            // user created with initial session and first session completed
            val userCode = createUserGettingCode(db)
            val successfulAddition = db.storeCompletedSession(userCode, TrainingSessionData(60f, 100f))
            assertThat(successfulAddition)

            // when
            // we query the user's sessions
            val sessions = db.getAllSessions(userCode)

            // then

            assertNotNull(sessions)

            // ensure the user has two sessions
            assertThat(sessions.size).isEqualTo(2)

            // and that the last session is the second session
            val secondSession = sessions.lastOrNull()
            assertNotNull(secondSession)
            assertThat(secondSession.number).isEqualTo(1)
        }
    }

    @Nested
    inner class Entities {
        @Test
        fun `Session overwritten functions`() {
            createUser(db)
            val sessionOne = transaction { TrainingSession.all().firstOrNull() }
            assertNotNull(sessionOne)

            createUser(db)
            val sessionTwo = transaction { TrainingSession.all().firstOrNull() }
            assertNotNull(sessionTwo)

            // test toString()
            assertThat(sessionOne.toString()).isNotEmpty

            // test hashCode()
            assertThat(sessionOne.hashCode()).isEqualTo(sessionTwo.hashCode())
        }
    }
}
