package code_grow.afeety.app.retrofit

import code_grow.afeety.app.kot_pref.UserInfo
import code_grow.afeety.app.model.*
import code_grow.afeety.app.utils.Constants
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface LabServices {

    @GET(Constants.LAB.plus(Constants.GET_LABS))
    suspend fun fetchLabs(@QueryMap map: Map<String, String>): ApiResponse<MutableList<Lab>>

    @GET(Constants.LAB.plus(Constants.LAB_REVIEWS))
    suspend fun fetchLabReviews(@Query("lab_id") labId: Int): ApiResponse<MutableList<Review>>

    @GET(Constants.LAB.plus(Constants.LAB_SLIDER))
    suspend fun fetchLabSlider(): SliderResponse


    @GET(Constants.LAB.plus(Constants.BOOK_EXAMINATION))
    suspend fun bookExamination(@QueryMap map: Map<String, String>): MessageApiResponse

    @GET(Constants.LAB.plus(Constants.LAB_RESERVATIONS))
    suspend fun getReservations(@Query("user_id") userId: Int = UserInfo.userId): ApiResponse<MutableList<LabBooking>>
}