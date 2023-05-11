package code_grow.afeety.app.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

/*
    {"data":[{"id":"3",
    "lab_id":"1",
    "patient_id":"1",
    "status":"1",
    "comment":"\u062a\u062c\u0631\u0628\u0629
\u0645\u0645\u064a\u0632\u0629",
"book_date":"0000-00-00",
"home_visit":"1",
"attached_file":"",
"price":"0.00",
"review":"5",
"review_comment":"\u062a\u062c\u0631\u0628\u0629
\u0645\u0645\u064a\u0632\u0629",
"created":"2022-01-23",
"modified":"2022-01-23",
"client_name":"amrsesee",
"client_id":"1",
"client_image":"http:\/\/www.code-grow.com\/slamtk\/prod_img\/image.jpg",
"date":"0000-00-00",
"rate":"5"}]
 */
@JsonClass(generateAdapter = true)
@Parcelize
data class Review(
    @Json(name = "id") val reviewId: Int,
    @Json(name = "review_comment") val review: String,
    @Json(name = "review") val avgRate: Float,
    @Json(name = "client_name") val reviewerName: String,
    @Json(name = "client_image") val reviewerPhoto: String,
    @Json(name = "created") val reviewDate: String
) : Parcelable