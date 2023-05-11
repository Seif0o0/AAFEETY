package code_grow.afeety.app.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize


@JsonClass(generateAdapter = true)
@Parcelize
data class LabExamination(
    @Json(name = "id") val examinationId: Int,
    val comment: String?,
    val price: Double,
    val name: String,
    @Json(name = "pre_request") val preRequest: String?,
    var isClicked: Boolean = false
) : Parcelable