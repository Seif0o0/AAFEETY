package code_grow.afeety.app.repository

import code_grow.afeety.app.retrofit.AppApi

class PharmaciesRepository {
    suspend fun getPharmacies(map: Map<String, String>) =
        AppApi.pharmacyServices.fetchPharmacies(map)

    suspend fun getMedicines(map: Map<String, String>) = AppApi.pharmacyServices.fetchMedicines(map)
    suspend fun getMedicineCategories() = AppApi.pharmacyServices.fetchMedicineCategories()
    suspend fun getSliderData() = AppApi.pharmacyServices.fetchPharmacySlider()
    suspend fun getOrders() = AppApi.pharmacyServices.fetchMedicineOrders()
}