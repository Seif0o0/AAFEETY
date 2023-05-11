package code_grow.afeety.app.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class AreasResponse(
    @Json(name = "areas") val areas: MutableList<Area>,
    val status: Boolean,
    val message: String?
)