package code_grow.afeety.app.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class Lab(
    @Json(name = "id") val labId: Int,
    val name: String,
    @Json(name = "body") val details: String,
    val address: String,
    val longitude: Double,
    val latitude: Double,
    @Json(name = "city_id") val cityId: Int,
    @Json(name = "banner") val bannerImage: String?,
    @Json(name = "image") val labImage: String,
    @Json(name = "facebook") val facebookLink: String,
    @Json(name = "mobile") val phoneNumber: String,
    @Json(name = "instagram") val instagramLink: String,
    @Json(name = "twitter") val twitterLink: String,
    @Json(name = "rate") val avgRate: Float,
    @Json(name = "home_visit") val isHomeVisit: Int,/* 1-> true, 0-> false */
    @Json(name = "booking") val bookingCount: Int?,
    @Json(name = "examinations") val examinations: MutableList<LabExamination>?
) : Parcelable