package code_grow.afeety.app.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class MedicineCategory(
    @Json(name = "id") val categoryId: Int,
    val name: String
)