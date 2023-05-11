package code_grow.afeety.app.repository

import code_grow.afeety.app.retrofit.AppApi
import okhttp3.MultipartBody

class HospitalsRepository {
    suspend fun getHospitals(map: Map<String, String>) = AppApi.hospitalServices.fetchHospitals(map)
    suspend fun getDoctors(map: Map<String, Int>) = AppApi.hospitalServices.fetchDoctors(map)
    suspend fun getSpecialities(map: Map<String, Int>) =
        AppApi.hospitalServices.fetchSpecialities(map)

    suspend fun getReviews(hospitalId: Int) =
        AppApi.hospitalServices.fetchHospitalReviews(hospitalId)

    suspend fun getSliderData() = AppApi.hospitalServices.fetchHospitalSlider()
    suspend fun uploadReport(body: MultipartBody.Part) = AppApi.globalServices.uploadImage(body)
    suspend fun bookDoctor(map: Map<String, String>) = AppApi.hospitalServices.bookDoctor(map)
    suspend fun getReservations() = AppApi.hospitalServices.getReservations()
}