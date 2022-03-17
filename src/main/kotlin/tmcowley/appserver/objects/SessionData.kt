package tmcowley.appserver.objects

import kotlinx.serialization.Serializable

/** for storing session data (mirrors Sessions in db) */
@Serializable
final data class SessionData(val number: Int, val speed: Float, val accuracy: Float)
