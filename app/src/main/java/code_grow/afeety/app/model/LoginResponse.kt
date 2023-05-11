package code_grow.afeety.app.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class LoginResponse<T>(
    val data: T?,
    val status: Boolean,
    val message: String?
)