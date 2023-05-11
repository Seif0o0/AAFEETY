package code_grow.afeety.app.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MedicineOrder(
    @Json(name = "id") val orderId: Int,
    @Json(name = "pharmacy") val pharmacyInfo: Pharmacy,
    val prescription: String,
    @Json(name = "drugs") val medicines: MutableList<Medicine>,
    val status: Int,/* 0-> current, 1-> past */
    @Json(name = "created") val createdDate: String,
    @Json(name = "total_price") val price: Double
)

