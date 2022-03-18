package tmcowley.appserver.objects

import kotlinx.serialization.Serializable

/** for storing session data (mirrors Sessions in db w/o number) */
@Serializable
final data class SessionData(val speed: Float, val accuracy: Float)
