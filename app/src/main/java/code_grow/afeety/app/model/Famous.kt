package code_grow.afeety.app.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Famous(
    val id: Int,
    val name: String,
    @Json(name = "image") val profPic: String,
    @Json(name = "slider") val sliderImage: String,
    val description: String,
    val status: Int,
    val gender: Int,
):Parcelable