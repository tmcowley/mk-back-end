package tmcowley.appserver.controllers

// using:
// https://github.com/JetBrains/Exposed

import tmcowley.appserver.Singleton
import tmcowley.appserver.objects.Key
import tmcowley.appserver.objects.KeyPair
import tmcowley.appserver.objects.SessionData

import tmcowley.appserver.utils.FreqTool
import tmcowley.appserver.utils.LangTool


import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.beans.factory.annotation.Value;

import org.jetbrains.exposed.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

// for DAO (Data Access Object) model
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Users: IntIdTable() {
    val uid = varchar("uid", 120).uniqueIndex()
    val speed = integer("speed")
    val age = integer("age")
    // val sessions = reference("sessions", Sessions)
}

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)
    var uid by Users.uid
    var speed by Users.speed
    var age by Users.age
}

object Sessions_To_Users: IntIdTable() {
    val user_id = reference("user_id", Users)
    val session_id = reference("session_id", Sessions)

    // override val primaryKey = PrimaryKey(user, session)
}

class Session_To_User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Session_To_User>(Sessions_To_Users)

    var user_id by Sessions_To_Users.user_id
    var session_id by Sessions_To_Users.session_id
}

object Sessions: IntIdTable() {
    val number = integer("number")
    // val metrics = reference("metrics", Metrics)
    val speed = float("speed")
    val accuracy = float("accuracy")
}

class Session(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Session>(Sessions)
    var number by Sessions.number
    var speed by Sessions.speed
    var accuracy by Sessions.accuracy
}

class DatabaseController {

    // @Value("#{spring.datasource.url}")
    // lateinit var dbUrl: String
    // @Value("#{spring.datasource.username}")
    // lateinit var dbUser: String
    // @Value("#{spring.datasource.password}")
    // lateinit var dbPass: String

    init {
        println("\nInit Database\n")
        // Database.connect(dbUrl, driver = "org.postgresql.Driver", user = dbUser, password = dbPass)
        // Database.connect(url = "jdbc:postgresql:test", driver = "org.postgresql.Driver", user = "admin", password = "")
        // Database.connect({ DriverManager.getConnection("jdbc:h2:mem:test;MODE=MySQL") })

        // "jdbc:h2:~/test"
        val dbLocal = Database.connect(url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")

        transaction(db = dbLocal) {
            // addLogger(StdOutSqlLogger)

            SchemaUtils.create(Users, Sessions, Sessions_To_Users)

            commit()
        }
    }

    /** create a new user by age and typing speed */
    fun createNewUser(userAge: Int, typingSpeed: Int): String? {

        // get a free user-code
        var userCode: String
        do {
            userCode = Singleton.getRandomUserCode()
        } while (userCodeTaken(userCode))

        // create the new user
        transaction {
            User.new { uid = userCode; age = userAge; speed = typingSpeed }
            commit()
        }

        // verify user by code exists
        val userAdded = userCodeTaken(userCode)
        if (!userAdded){ 
            println("Error: User-creation failed")
            return null
        }

        // create empty session (as session 0)
        fun createSessionZero(): Boolean? {
            return createNewSession(userCode, SessionData(number = 0, speed = 0f, accuracy = 0f))
        }
        val createdNewSession = createSessionZero() ?: return null
        if (!createdNewSession) return null

        // return user code
        return userCode
    }

    /** get the next session number for a user (by user-code) */
    fun getNextSessionNumber(userCode: String): Int? {

        // ensure the user code is taken
        if (!userCodeTaken(userCode)) return null

        // get userId of user
        val userId = getUserId(userCode) ?: return null

        return (getLastSessionNumber(userId) + 1)
    }

    /** 
     * get the last session number completed by a user (by user-code)
     * assumes the user by user-code exists 
     */
    private fun getLastSessionNumber(userId: Int): Int {
        val topCompletedSession = getTopCompletedSession(userId)
        return topCompletedSession.number
    }

    /** 
     * get the last session completed by a user (by user-id)
     * assumes the user by user-code exists 
     * 
     * @return null indicates failure
     */
    @Throws(Exception::class)
    private fun getTopCompletedSession(userId: Int): Session {

        // get the highest numbered session
        var topSession: Session? = transaction {
            // get all sessions by user; join users with sessions via sessions_to_users
            val sessionsQuery = Users.rightJoin(Sessions_To_Users).rightJoin(Sessions).select {

                // TODO - this may need to be compared to an EntityId<Int> not just an int
                Users.id eq userId
            }
            val sessions = Session.wrapRows(sessionsQuery).toMutableList()

            sessions.maxByOrNull { session: Session -> session.number }
        }

        return topSession ?: throw Exception("getTopCompletedSession() failed -> user addition failed to add session zero")
    }

    /** get the user-id of a user by user-code */
    private fun getUserId(userCode: String): Int? {
        return getUserEntityId(userCode)?.value
    }

    /** get the entity-id of a user by user-code */
    private fun getUserEntityId(userCode: String): EntityID<Int>? {
        var entityId: EntityID<Int>? = transaction {
            User.find{ Users.uid eq userCode }.firstOrNull()?.id
        }
        return entityId
    }

    /** check if a user-code is taken */
    fun userCodeTaken(userCode: String): Boolean {
        return (!userCodeFree(userCode))
    }

    /** check if a user-code is free */
    private fun userCodeFree(userCode: String): Boolean {
        var isFree: Boolean = transaction { User.find{ Users.uid eq userCode }.empty() }
        return isFree 
    }

    /** add a completed session to the database under user by user-code */
    fun storeCompletedSession(userCode: String, data: SessionData): Boolean? {
        return createNewSession(userCode, data)
    }

    /** add a new session to the database under user by user-code */
    private fun createNewSession(userCode: String, sessionData: SessionData): Boolean? {

        // get userId of user
        val userEntityId = getUserEntityId(userCode) ?: return null

        transaction {
            val session = Session.new {
                number = sessionData.number
                speed = sessionData.speed
                accuracy = sessionData.accuracy
            }

            Session_To_User.new { 
                user_id = userEntityId
                session_id = session.id 
            }

            commit()
        }

        // TODO: validate session and session_to_user were added

        return true
    }
}
