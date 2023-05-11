package code_grow.afeety.app.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Product(
    @Json(name = "id") val productId: Int,
    val name: String,
    @Json(name = "breif") val details: String,
    val image: String,
    @Json(name = "category_id") val categoryId: Int,
    @Json(name = "brand_id") val brandId: Int,
    val type: String,
    val price: Double,
    val sale: Double,
    val status: Int,
    @Json(name = "category_name") val categoryName: String,
    @Json(name = "category_image") val categoryImage: String,
    @Json(name = "brand_name") val brandName: String,
    @Json(name = "influncer_id") val influencerId: Int?,
    @Json(name = "influncer_name") val influencerName: String?
) : Parcelable