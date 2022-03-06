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

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

// for DAO (Data Access Object) model
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Users: IntIdTable() {
    val uid = varchar("uid", 120).uniqueIndex()
    val age = integer("age")
    // val sessions = reference("sessions", Sessions)
}

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)
    var uid by Users.uid
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
    val sessionCount = integer("session_count")
    // val metrics = reference("metrics", Metrics)
    val speed = float("speed")
    val accuracy = float("accuracy")
}

class Session(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Session>(Sessions)
    var sessionCount by Sessions.sessionCount
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

        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            addLogger(StdOutSqlLogger)

            SchemaUtils.create(Users, Sessions, Sessions_To_Users)

            val u1 = User.new {
                uid = "test-test-test"
                age = 21
            }
    
            val u1FirstSession = Session.new {
                sessionCount = 1
                speed = 60f
                accuracy = 70f
            }

            Session_To_User.new {
                user_id = u1.id
                session_id = u1FirstSession.id
            }
        }
    }

    
}