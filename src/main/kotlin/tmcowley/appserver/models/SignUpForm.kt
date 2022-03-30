package tmcowley.appserver.models

import kotlinx.serialization.Serializable

@Serializable
data class SignUpForm(val age: Int, val speed: Int)