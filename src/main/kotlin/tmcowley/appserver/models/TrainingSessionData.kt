package tmcowley.appserver.models

import kotlinx.serialization.Serializable

/** for storing training session data (mirrors Sessions in db w/o training session number) */
@Serializable
data class TrainingSessionData(val speed: Float, val accuracy: Float)
