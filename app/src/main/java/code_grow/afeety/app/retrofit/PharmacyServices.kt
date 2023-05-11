package code_grow.afeety.app.retrofit

import code_grow.afeety.app.kot_pref.UserInfo
import code_grow.afeety.app.model.*
import code_grow.afeety.app.utils.Constants
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface PharmacyServices {

    @GET(Constants.PHARMACY.plus(Constants.GET_PHARMACIES))
    suspend fun fetchPharmacies(@QueryMap map: Map<String, String>): ApiResponse<MutableList<Pharmacy>>

    @GET(Constants.PHARMACY.plus(Constants.PHARMACY_MEDICINES))
    suspend fun fetchMedicines(@QueryMap map: Map<String, String>): ApiResponse<MutableList<Medicine>>

    @GET(Constants.PHARMACY.plus(Constants.MEDICINE_CATEGORIES))
    suspend fun fetchMedicineCategories(): ApiResponse<MutableList<MedicineCategory>>

    @GET(Constants.PHARMACY.plus(Constants.PHARMACY_SLIDER))
    suspend fun fetchPharmacySlider(): SliderResponse

    @GET(Constants.PHARMACY.plus(Constants.MEDICINE_ORDERS))
    suspend fun fetchMedicineOrders(@Query("user_id") userId: Int = UserInfo.userId): ApiResponse<MutableList<MedicineOrder>>

    @GET(Constants.PHARMACY.plus(Constants.BUY_MEDICINE))
    suspend fun buyMedicine(@QueryMap map: Map<String, String>): MessageApiResponse



}