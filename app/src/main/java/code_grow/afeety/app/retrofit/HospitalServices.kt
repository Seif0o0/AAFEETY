package code_grow.afeety.app.retrofit

import code_grow.afeety.app.kot_pref.UserInfo
import code_grow.afeety.app.model.*
import code_grow.afeety.app.utils.Constants
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface HospitalServices {

    @GET(Constants.HOSPITAL.plus(Constants.GET_HOSPITALS))
    suspend fun fetchHospitals(@QueryMap map: Map<String, String>): ApiResponse<MutableList<Hospital>>

    @GET(Constants.HOSPITAL.plus(Constants.DOCTORS))
    suspend fun fetchDoctors(@QueryMap map: Map<String, Int>): ApiResponse<MutableList<Doctor>>

    @GET(Constants.HOSPITAL.plus(Constants.SPECIALITIES))
    suspend fun fetchSpecialities(@QueryMap map: Map<String, Int>): ApiResponse<MutableList<Speciality>>

    @GET(Constants.HOSPITAL.plus(Constants.HOSPITALS_REVIEWS))
    suspend fun fetchHospitalReviews(@Query("id") hospitalId: Int): ApiResponse<MutableList<Review>>

    @GET(Constants.HOSPITAL.plus(Constants.HOSPITAL_SLIDER))
    suspend fun fetchHospitalSlider(): SliderResponse

    @GET(Constants.HOSPITAL.plus(Constants.BOOK_DOCTOR))
    suspend fun bookDoctor(@QueryMap map: Map<String, String>): MessageApiResponse

    @GET(Constants.HOSPITAL.plus(Constants.HOSPITAL_RESERVATIONS))
    suspend fun getReservations(@Query("user_id") userId: Int = UserInfo.userId): ApiResponse<MutableList<HospitalBooking>>
}