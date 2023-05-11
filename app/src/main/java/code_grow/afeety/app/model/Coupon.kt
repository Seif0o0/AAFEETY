package code_grow.afeety.app.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Coupon(
    @Json(name = "id") val couponId: Int,
    @Json(name = "title") val name: String,
    val image: String,
    val type: Int,/* 1 for medical , 2 for beauty */
    @Json(name = "provider_id") val providerId: Int,
    @Json(name = "provider_name") val providerName: String,
    val price: Double
):Parcelable