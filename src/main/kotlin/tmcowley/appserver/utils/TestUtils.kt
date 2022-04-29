package tmcowley.appserver.utils

import tmcowley.appserver.controllers.DatabaseController
import tmcowley.appserver.controllers.User
import tmcowley.appserver.models.TrainingSessionData
import kotlin.math.pow
import kotlin.test.assertNotNull

/** allow exponentiation on integers */
infix fun Int.pow(exponent: Int): Int = toDouble().pow(exponent).toInt()

/** create a fresh user, returning the user's user-code */
fun createUserGettingCode(db: DatabaseController): String {
    val freshUserCode = db.createNewUserGettingCode(
        userAge = 23,
        typingSpeed = 23
    )
    assertNotNull(freshUserCode)

    return freshUserCode
}

/** create a fresh user, returning the User object */
fun createUser(db: DatabaseController): User {
    val freshUser = db.createNewUser(
        userAge = 23,
        typingSpeed = 23
    )
    assertNotNull(freshUser)

    return freshUser
}

/** record an arbitrary training session against the given user */
fun reportTrainingSession(db: DatabaseController, user: User) {
    // create the (valid) training session data
    val trainingSession = TrainingSessionData(60f, 100f)

    // store the completed session
    db.storeCompletedSession(user.userCode, trainingSession)
}
