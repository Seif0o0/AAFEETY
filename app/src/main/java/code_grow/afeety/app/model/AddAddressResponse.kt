package code_grow.afeety.app.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class AddAddressResponse(
    val address: Address?,
    val status: Boolean,
    val message: String?
)