package code_grow.afeety.app.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize


@Parcelize
@JsonClass(generateAdapter = true)
data class LabBooking(
    @Json(name = "id") val bookingId: Int,
    val status: Int,/* 0-> current, 1-> past */
    @Json(name = "home_visit") val isHomeVisit: Int,/* 0-> false, 1-> true */
    @Json(name = "total_price") val price: Double,
    @Json(name = "created") val bookingDate: String,
    val examinations: MutableList<LabExamination>,
    @Json(name = "examinations_count") val examinationsCount: String,
    @Json(name = "lab_data") val labDetails: Lab
) : Parcelable