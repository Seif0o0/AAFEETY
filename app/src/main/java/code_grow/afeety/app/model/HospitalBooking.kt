package code_grow.afeety.app.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize


@Parcelize
@JsonClass(generateAdapter = true)
data class HospitalBooking(
    @Json(name = "id") val bookingId: Int,
    val status: Int,/* 0-> current, 1-> past */
    @Json(name = "medical_record") val accessMedicalFile: Int, /* 0-> false, 1-> true */
    val price: Double,
    @Json(name = "doctor_data") val doctor: Doctor,
    @Json(name = "review") val userRating: Float,
    @Json(name = "book_date") val bookingDate: String,
    @Json(name = "book_time") val bookingTime: String,
    @Json(name = "hospital_data") val hospital: Hospital,
    @Json(name = "doctor_name") val doctorName: String,
    @Json(name = "speciality_name") val doctorSpeciality: String
) : Parcelable