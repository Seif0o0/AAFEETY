package code_grow.afeety.app.model

import com.squareup.moshi.JsonClass

/*
    {"sliders":
    [{"id":"2",
    "place":"hospital",
    "image":"http:\/\/www.code-grow.com\/slamtk\/slider\/hospital_slider.png","target":"0","status":"0","created":"0000-00-00"}],"status":true,"count":1}
 */
@JsonClass(generateAdapter = true)
data class SliderItem(
    val id: Int,
    val image: String,
    val target: Int
)