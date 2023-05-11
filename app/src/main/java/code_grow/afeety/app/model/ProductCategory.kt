package code_grow.afeety.app.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class ProductCategory(
    @Json(name = "id") val categoryId: Int,
    val name: String,
    val image: String,
    val status: Int,
    val type: Int,
    var clicked: Boolean = false
)