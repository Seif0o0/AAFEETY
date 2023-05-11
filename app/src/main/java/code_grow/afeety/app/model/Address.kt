package code_grow.afeety.app.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize


@Parcelize
@JsonClass(generateAdapter = true)
data class Address(
    @Json(name = "id") val addressId: Int,
    val longitude: Double,
    val latitude: Double,
    val address: String,
    val title: String,
    @Json(name = "price_area") val deliveryFees: Double,
    @Json(name = "description") val details: String,
    var isClicked: Boolean = false
) : Parcelable