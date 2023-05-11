package code_grow.afeety.app.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class AddressResponse<T>(
    @Json(name = "list") val data: T?,
    val status: Boolean,
    val message: String?
)