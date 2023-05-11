package code_grow.afeety.app.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HomeData(
    @Json(name = "hospitals") val bestHospitals: MutableList<Hospital>,
    @Json(name = "labs") val bestLabs: MutableList<Lab>,
    @Json(name = "influncers") val influencers: MutableList<Famous>
)