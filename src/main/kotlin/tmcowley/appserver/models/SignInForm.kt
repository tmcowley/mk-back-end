package tmcowley.appserver.models

import kotlinx.serialization.Serializable

@Serializable
data class SignInForm(val userCode: String)