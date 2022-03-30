package tmcowley.appserver.utils

import tmcowley.appserver.models.TrainingSessionData
import tmcowley.appserver.models.SignUpForm

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
fun validateSessionData(session: TrainingSessionData): Boolean {
    // validate speed
    if (session.speed < 0f) return false

    // validate accuracy
    if (session.accuracy < 0f) return false
    if (session.accuracy > 100f) return false

    return true
}

/** validate the format of a user-code (5gram-5gram-5gram) */
fun validateUserCode(userCode: String): Boolean {
    // TODO
    return true
}
