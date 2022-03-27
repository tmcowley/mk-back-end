package tmcowley.appserver.models

import kotlinx.serialization.Serializable

/** for storing session data (mirrors Sessions in db w/o number) */
@Serializable
data class SessionData(val speed: Float, val accuracy: Float)
