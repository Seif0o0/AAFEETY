package code_grow.afeety.app.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class City(
    @Json(name = "id") val cityId: Int,
    val name: String
)