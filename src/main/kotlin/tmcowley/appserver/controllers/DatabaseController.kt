package tmcowley.appserver.controllers

// using:
// https://github.com/JetBrains/Exposed

import tmcowley.appserver.Singleton
import tmcowley.appserver.objects.Key
import tmcowley.appserver.objects.KeyPair
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

// object Metrics: IntIdTable() {
//     val speed = float("speed")
//     val accuracy = float("accuracy")
// }

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

        transaction(db = dbLocal ) {
            addLogger(StdOutSqlLogger)

            SchemaUtils.create(Users, Sessions, Sessions_To_Users)

            addDummyData()

            commit()
        }
    }

    fun addDummyData() {
        val u1 = 
        User.new {
            uid = "test-test-test"
            age = 21
            speed = 60
        }

        val u1FirstSession = Session.new {
            number = 1
            speed = 60f
            accuracy = 70f
        }

        Session_To_User.new {
            user_id = u1.id
            session_id = u1FirstSession.id
        }

        println("Users: ${User.all().joinToString{ user -> user.uid}}")
        println("Sessions: ${Session.all().joinToString{ session -> session.speed.toString()}}")

        val userCode = "test-test-test"
        val failingUserCode = "fail-fail-fail"

        println("Matches($userCode): ${userCodeTaken(userCode)}")
        println("Matches(${failingUserCode}): ${userCodeTaken(failingUserCode)}")

        println()
        println("test adding users")
        repeat(5) {
            println(createNewUser(21, 60))
        }
    }

    fun createNewUser(userAge: Int, typingSpeed: Int): String? {

        var userCode: String?

        do {
            userCode = Singleton.getRandomUserCode()
        } while (userCodeTaken(userCode as String))

        transaction {
            User.new { 
                uid = userCode
                age = userAge
                speed = typingSpeed
            }

            commit()
        }

        // verify user by code exists
        val userAdded = userCodeTaken(userCode)

        if (!userAdded){ 
            println("Error: User-creation failed")
            return null
        }

        // return user code
        return userCode
    }

    fun getNextSession(userCode: String): Int {
        // get the last completed session number (0 if none completed)
        val lastSessionNumb = getLastSessionNumber(userCode) ?: 0

        return (lastSessionNumb + 1)
    }

    private fun getLastSessionNumber(userCode: String): Int? {
        return getTopCompletedSession(userCode)?.number
    }

    private fun getTopCompletedSession(userCode: String): Session? {

        var topSession: Session? = null

        transaction {
            // get userId of user
            val id = getUserId(userCode)

            // get all sessions by user
            // join users with sessions via sessions_to_users
            val sessionsQuery = Users.rightJoin(Sessions_To_Users).rightJoin(Sessions).select {
                Users.id eq id
            }

            val sessions = Session.wrapRows(sessionsQuery).toMutableList()
            sessions.sortBy{ session: Session -> session.number }

            topSession = sessions.firstOrNull()

            commit()
        }

        return topSession
    }

    private fun getUserId(userCode: String): Int? {
        var id: Int? = null
        transaction {
            // get userId of user
            val user: User? = User.find{ Users.uid eq userCode }.firstOrNull()
            id = user?.id?.value

            commit()
        }

        return id
    }

    fun userCodeTaken(userCode: String): Boolean {
        return (!userCodeFree(userCode))
    }

    private fun userCodeFree(userCode: String): Boolean {
        var isFree = true;
        transaction {
            // matches = User.find{ Users.uid eq userCode }.joinToString() { user -> user.uid } == ""

            isFree = User.find{ Users.uid eq userCode }.empty()

            commit()
        }
        return isFree
    }
}
