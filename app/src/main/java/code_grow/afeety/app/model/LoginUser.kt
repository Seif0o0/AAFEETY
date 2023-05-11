package code_grow.afeety.app.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
class LoginUser(
    @Json(name = "id") val userId: Int,
    @Json(name = "name") val username: String,
    @Json(name = "password") val password: String,
    @Json(name = "username") val email: String,
    @Json(name = "mobile") val phoneNumber: String,
    @Json(name = "image") val profilePicture: String,
    @Json(name = "gender") val gender: Int,/*1-> male, 2-> female */
    @Json(name = "city_id") val cityId: Int,
    @Json(name = "longitude") val longitude: Double,
    @Json(name = "latitude") val latitude: Double,
    @Json(name = "address") val address: String,
    @Json(name = "token") val firebaseToken: String,
    @Json(name = "age") val age: Int,
    @Json(name = "code") val code: Int,
    @Json(name = "status") val status: Int,
    @Json(name = "activated") val activated: Int,
)