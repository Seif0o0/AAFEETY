package code_grow.afeety.app.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class SliderResponse(
    @Json(name = "sliders") val sliderItems: MutableList<SliderItem>?,
    val message: String?,
    val status: Boolean,
    val count: Int
)