package code_grow.afeety.app.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Doctor(
    @Json(name = "id") val doctorId: Int,
    @Json(name = "hospital_id") val hospitalId: Int,
    val speciality: Speciality?,
    @Json(name = "doctor_title_name") val doctorTitle: String,
    @Json(name = "speciality_id") val specialityId: Int,
    @Json(name = "doctor_title_id") val doctorTitleId: Int,
    val name: String,
    @Json(name = "body") val info: String,
    @Json(name = "image") val doctorPicture: String,
    @Json(name = "rate") val avgRate: Float,
    @Json(name = "price") val bookingPrice: Double,
    @Json(name = "price_following") val followUpPrice: Double,
    @Json(name = "home_visit") val isHomeVisiting: Int,
    @Json(name = "booking") val bookingCount: Int?,
    @Json(name = "time_date") val doctorSchedule: List<DoctorSchedule>?
) : Parcelable