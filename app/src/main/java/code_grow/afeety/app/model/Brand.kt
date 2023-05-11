package code_grow.afeety.app.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Brand(
    @Json(name = "id") val brandId: Int,
    val name: String,
    val description: String,
    val image: String,
    @Json(name = "slider") val sliderImage: String,
    val type: Int,
    val status: Int,
) :Parcelable