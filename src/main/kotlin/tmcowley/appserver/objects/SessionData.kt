package tmcowley.appserver.objects

import kotlinx.serialization.Serializable

@Serializable
final data class SessionData(val number: Int, val speed: Float, val accuracy: Float)
