package code_grow.afeety.app.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Pharmacy(
    @Json(name = "id") val pharmacyId: Int,
    val name: String,
    @Json(name = "body") val details: String,
    @Json(name = "longitude") val longitude: Double,
    @Json(name = "latitude") val latitude: Double,
    val address: String,
    @Json(name="image")val pharmacyImage: String,
    @Json(name = "banner") val bannerImage: String,
    @Json(name = "city_id") val cityId: Int,
    @Json(name = "delivery_fees") val deliveryFees: Double,
    @Json(name = "rate") val avgRate: Float,
    @Json(name = "mobile") val phoneNumber: String,
    @Json(name = "drugs") val medicines: MutableList<Medicine>?
):Parcelable