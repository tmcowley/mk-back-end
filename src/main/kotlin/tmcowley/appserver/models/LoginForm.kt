package tmcowley.appserver.models

import kotlinx.serialization.Serializable

@Serializable
data class LoginForm(val userCode: String)