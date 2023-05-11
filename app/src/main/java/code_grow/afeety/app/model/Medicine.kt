package code_grow.afeety.app.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize


@Parcelize
@JsonClass(generateAdapter = true)
data class Medicine(
    @Json(name = "id") val id: Int,
    @Json(name = "drug_id") val medicineId: Int?,
//    @Json(name = "pharmacy_id") val pharmacyId: Int,
    val sale: Double,
    val price: Double,
    @Json(name = "image") val imageUrl: String?,
    val name: String,
    @Json(name = "category_name") val categoryName: String,
    val description: String,
) : Parcelable