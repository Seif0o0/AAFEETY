package code_grow.afeety.app.retrofit

import code_grow.afeety.app.kot_pref.UserInfo
import code_grow.afeety.app.model.*
import code_grow.afeety.app.utils.Constants
import okhttp3.MultipartBody
import retrofit2.http.*

interface GlobalServices {

    @GET(Constants.GLOBAL.plus(Constants.CITIES))
    suspend fun fetchCities(): CitiesResponse<MutableList<City>>

    @GET(Constants.MAIN.plus(Constants.HOME_DATA))
    suspend fun fetchHomeData(): HomeResponse

    @GET(Constants.MAIN.plus(Constants.HOME_SLIDER))
    suspend fun fetchHomeSlider(): SliderResponse

    @Multipart
    @POST(Constants.GLOBAL.plus(Constants.UPLOAD_IMAGE))
    suspend fun uploadImage(@Part body: MultipartBody.Part): UploadFilesResponse<MutableList<String>>

    @GET(Constants.GLOBAL.plus(Constants.CONTACT_US))
    suspend fun sendFeedback(@QueryMap map: Map<String, String>): MessageApiResponse

    @GET(Constants.GLOBAL.plus(Constants.ADDRESSES))
    suspend fun fetchAddresses(@Query("user_id") userInt: Int = UserInfo.userId): AddressResponse<MutableList<Address>>

    @GET(Constants.GLOBAL.plus(Constants.ADD_ADDRESS))
    suspend fun addAddresses(@QueryMap map: Map<String, String>): AddAddressResponse

    @GET(Constants.GLOBAL.plus(Constants.AREAS))
    suspend fun fetchAreas(): AreasResponse

    @GET(Constants.GLOBAL.plus(Constants.UPDATE_TOKEN))
    suspend fun updateToken(@QueryMap queryMap: Map<String, String>): UpdateTokenResponse<LoginUser>


}