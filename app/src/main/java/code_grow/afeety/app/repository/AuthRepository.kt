package code_grow.afeety.app.repository

import code_grow.afeety.app.retrofit.AppApi
import okhttp3.MultipartBody

class AuthRepository {
    suspend fun register(map: Map<String, String>) = AppApi.userServices.register(map)
    suspend fun login(phoneNumber: String, password: String, firebaseToken: String) =
        AppApi.userServices.login(phoneNumber, password, firebaseToken)

    suspend fun updateToken(map: Map<String, String>) = AppApi.globalServices.updateToken(map)
    suspend fun editProfile(map: Map<String, String>) = AppApi.userServices.editProfile(map)
    suspend fun uploadImage(body: MultipartBody.Part) = AppApi.globalServices.uploadImage(body)
    suspend fun getCities() = AppApi.globalServices.fetchCities()
}