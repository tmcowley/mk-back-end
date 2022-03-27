package tmcowley.appserver.models

import kotlinx.serialization.Serializable

@Serializable
data class MetricsData(val speed: Float, val accuracy: Float)