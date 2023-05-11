package code_grow.afeety.app.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class DoctorSchedule(
    val id: Int,
    @Json(name = "day_week") val dayNumber: Int,
    @Json(name = "start_time") val startTime: String,
    @Json(name = "end_time") val endTime: String,
    var isClicked: Boolean = false
) : Parcelable