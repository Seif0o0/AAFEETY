package code_grow.afeety.app.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Speciality(
    @Json(name = "id") val specialityId: Int,
    var name: String,
    val image: String,
    var clicked: Boolean = false
) : Parcelable