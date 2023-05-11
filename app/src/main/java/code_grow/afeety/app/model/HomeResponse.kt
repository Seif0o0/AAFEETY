package code_grow.afeety.app.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HomeResponse(
    val data: HomeData?,
    val status: Boolean,
    val message: String?,
    val count: Int?
)