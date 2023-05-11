package code_grow.afeety.app.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class ProductOrder(
    @Json(name = "id") val orderId: Int,
    @Json(name = "user_id") val userId: Int,
    @Json(name = "products_count") val productCount: String,
    val status: Int,/* 0-> current, 1-> past */
    val type: Int,
    @Json(name = "total_price") val price: Double,
    @Json(name = "delivery_price") val delivery: Double,
    @Json(name = "total_price_delivery") val totalPriceWithDelivery: Double,
    val note: String,
    @Json(name = "address_id") val addressId: Int,
    @Json(name = "created") val createdDate: String,
    val products: MutableList<SmallProduct>
)