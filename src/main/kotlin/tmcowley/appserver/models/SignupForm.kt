package tmcowley.appserver.models

import kotlinx.serialization.Serializable

@Serializable
data class SignupForm(val age: Int, val speed: Int)