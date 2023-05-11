package code_grow.afeety.app.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Question(
    @Json(name = "id") val questionId: Int,
    @Json(name = "user_name") val username: String = "seifo",
    val question: String,
    @Json(name = "title") val answerer: String,
    @Json(name = "body") val answer: String,
    @Json(name = "category_name") val categoryName: String,
    @Json(name = "image") val image: String = "logo",
    @Json(name = "created") val answerTime: String
) : Parcelable