package code_grow.afeety.app.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class UpdateTokenResponse<T>(
    val user: T?,
    val status: Boolean,
    val message: String?,
    @Json(name = "count") val dataSize: Int?
)