package tmcowley.appserver.utils

import tmcowley.appserver.models.SignUpForm
import tmcowley.appserver.models.TrainingSessionData

/** validate a sign-up form */
fun validateSignUpForm(form: SignUpForm): Boolean {
    // validate age
    if (form.age < 13) return false
    if (form.age > 200) return false

    // validate original typing speed
    if (form.speed < 0) return false
    if (form.speed > 400) return false

    // form is valid
    return true
}

/** validate a training session */
fun validateTrainingSessionData(session: TrainingSessionData): Boolean {
    // validate speed
    if (session.speed < 0f) return false

    // validate accuracy
    if (session.accuracy < 0f) return false
    if (session.accuracy > 100f) return false

    return true
}

/** validate the format of a user-code (5gram-5gram-5gram) */
fun validateUserCode(userCode: String): Boolean {
    // length check
    if (userCode.length != 17) return false

    // case check - must be lowercase
    if (userCode.lowercase() != userCode) return false

    // split on dashes
    val words = userCode.split("-")

    // check there are three words
    if (words.size != 3) return false

    // check each word is a 5-gram
    if (!words.all { word -> word.length == 5}) return false

    return true
}
