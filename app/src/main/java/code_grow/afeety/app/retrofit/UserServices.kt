package code_grow.afeety.app.retrofit

import code_grow.afeety.app.model.LoginResponse
import code_grow.afeety.app.model.LoginUser
import code_grow.afeety.app.utils.Constants
import retrofit2.http.*

interface UserServices {

    @GET(Constants.GLOBAL.plus(Constants.REGISTER))
    suspend fun register(@QueryMap queryMap: Map<String, String>): LoginResponse<LoginUser>

    @GET(Constants.GLOBAL.plus(Constants.EDIT_PROFILE))
    suspend fun editProfile(@QueryMap queryMap: Map<String, String>): LoginResponse<LoginUser>

    @GET(Constants.GLOBAL.plus(Constants.LOGIN))
    suspend fun login(
        @Query("mobile") phoneNumber: String,
        @Query("password") password: String,
        @Query("token") firebaseToken: String
    ): LoginResponse<LoginUser>


}