package code_grow.afeety.app.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Area(
    @Json(name = "id") val areaId: Int,
    val name: String,
    @Json(name = "price") val deliveryFees: Double,
    val title: String,
)