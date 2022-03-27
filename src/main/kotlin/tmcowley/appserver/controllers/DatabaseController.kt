package tmcowley.appserver.controllers

// using:
// https://github.com/JetBrains/Exposed

// for DAO (Data Access Object) model
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import tmcowley.appserver.Singleton
import tmcowley.appserver.models.SessionData

object Users : IntIdTable() {
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

object SessionsToUsers : IntIdTable() {
    val user_id = reference("user_id", Users)
    val session_id = reference("session_id", Sessions)

    // override val primaryKey = PrimaryKey(user, session)
}

class SessionToUser(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SessionToUser>(SessionsToUsers)

    var userId by SessionsToUsers.user_id
    var sessionId by SessionsToUsers.session_id
}

object Sessions : IntIdTable() {
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

            SchemaUtils.create(Users, Sessions, SessionsToUsers)

            commit()
        }
    }

    /** create a new user by age and typing speed, returning user-id */
    fun createNewUserGettingId(userAge: Int, typingSpeed: Int): Int? {
        val user = createNewUser(userAge, typingSpeed) ?: return null
        return user.id.value
    }

    /** create a new user by age and typing speed, returning user-code */
    fun createNewUserGettingCode(userAge: Int, typingSpeed: Int): String? {
        val user = createNewUser(userAge, typingSpeed) ?: return null
        return user.uid
    }

    /** create a new user by age and typing speed */
    fun createNewUser(userAge: Int, typingSpeed: Int): User? {

        // get a free user-code
        var userCode: String
        do {
            userCode = Singleton.getRandomUserCode()
        } while (userCodeTaken(userCode))

        // create the new user
        val user = transaction {
            User.new { uid = userCode; age = userAge; speed = typingSpeed }
        }

        // verify user by code exists
        val userAdded = userCodeTaken(userCode)
        if (!userAdded) {
            println("Error: User-creation failed")
            return null
        }

        // create empty session (as session 0)
        val createdNewSession = createSessionZero(userCode)
        if (!createdNewSession) return null

        // return full user object
        return user
    }

    /** get the next session number for a user (by user-code) */
    fun getNextSessionNumber(userCode: String): Int? {

        // ensure the user code is taken
        if (userCodeFree(userCode)) return null

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
     */
    @Throws(RuntimeException::class)
    private fun getTopCompletedSession(userId: Int): Session {

        // get the highest numbered session
        val topSession: Session? = getAllSessions(userId).maxByOrNull { session -> session.number }

        // null top session indicates error
        topSession
            ?: throw RuntimeException("getTopCompletedSession() failed -> user addition failed to add session zero")

        return topSession
    }

    /** get the user-id of a user by user-code */
    fun getUserId(userCode: String): Int? {
        return getUserEntityId(userCode)?.value
    }

    /** get the entity-id of a user by user-code */
    private fun getUserEntityId(userCode: String): EntityID<Int>? {
        val entityId: EntityID<Int>? = transaction {
            User.find { Users.uid eq userCode }.firstOrNull()?.id
        }
        return entityId
    }

    /** check if a user-code is taken */
    fun userCodeTaken(userCode: String): Boolean {
        return (!userCodeFree(userCode))
    }

    /** check if a user-code is free */
    private fun userCodeFree(userCode: String): Boolean {
        return transaction { User.find { Users.uid eq userCode }.empty() }
    }

    /**
     * add a completed session to the database under user by user-code
     * stores it as the next available session number
     */
    fun storeCompletedSession(userCode: String, data: SessionData): Boolean {

        // get next session number to be completed
        val sessionNumber = getNextSessionNumber(userCode) ?: return false

        return createNewSession(userCode, sessionNumber, data)
    }

    /** add a new session to the database under user by user-code */
    private fun createNewSession(userCode: String, sessionNumber: Int, sessionData: SessionData): Boolean {

        // get userId of user
        val userEntityId = getUserEntityId(userCode) ?: return false

        transaction {
            val session = Session.new {
                number = sessionNumber
                speed = sessionData.speed
                accuracy = sessionData.accuracy
            }

            SessionToUser.new {
                userId = userEntityId
                sessionId = session.id
            }

            commit()
        }

        // TODO: validate session and session_to_user were added

        return true
    }

    private fun createSessionZero(userCode: String): Boolean {
        return createNewSession(userCode, sessionNumber = 0, SessionData(speed = 0f, accuracy = 0f))
    }

    /** get all sessions of a user by user-code */
    fun getAllSessions(userCode: String): MutableList<Session>? {

        // ensure the user code is taken
        if (userCodeFree(userCode)) return null

        // get user entity-id
        val userId = getUserId(userCode) ?: return null

        // get all sessions by userId
        return getAllSessions(userId)
    }

    /**
     * get all sessions of a user by user-entity-id;
     * join users with sessions via sessions_to_users
     */
    fun getAllSessions(userId: Int): MutableList<Session> {

        // get all sessions of a user, by joins
        val sessions: MutableList<Session> = transaction {
            // get all sessions by user; join users with sessions via sessions_to_users
            val sessionsQuery = Users.rightJoin(SessionsToUsers).rightJoin(Sessions).select {
                Users.id eq userId
            }
            Session.wrapRows(sessionsQuery).toMutableList()
        }

        return sessions
    }
}
