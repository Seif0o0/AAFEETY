package code_grow.afeety.app.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize


@Parcelize
@JsonClass(generateAdapter = true)
data class SmallProduct(
    @Json(name = "id") val productId: Int,
    val price: Double,
    val sale: Double,
    val name: String,
    val type: Int,
    val description: String?,
    @Json(name = "category_name") val categoryName: String,
    @Json(name = "influncer_name") val famousName: String?
) : Parcelable