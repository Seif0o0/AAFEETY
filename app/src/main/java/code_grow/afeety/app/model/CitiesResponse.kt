package code_grow.afeety.app.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class CitiesResponse<T>(
    @Json(name = "cities") val data: T?,
    val status: Boolean,
    val message: String?,
    @Json(name = "count") val listSize: Int?
)