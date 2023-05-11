package code_grow.afeety.app.repository

import code_grow.afeety.app.retrofit.AppApi
import okhttp3.MultipartBody

class CartRepository {
    suspend fun getAddresses() = AppApi.globalServices.fetchAddresses()

    suspend fun addAddress(map: Map<String, String>) = AppApi.globalServices.addAddresses(map)

    suspend fun buyMedicine(map: Map<String, String>) = AppApi.pharmacyServices.buyMedicine(map)

    suspend fun buyProduct(map: Map<String, String>) = AppApi.productServices.buyProduct(map)

    suspend fun uploadPrescription(body: MultipartBody.Part) =
        AppApi.globalServices.uploadImage(body)

    suspend fun getAreas() = AppApi.globalServices.fetchAreas()
}