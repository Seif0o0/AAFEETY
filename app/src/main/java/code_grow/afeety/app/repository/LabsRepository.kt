package code_grow.afeety.app.repository

import code_grow.afeety.app.retrofit.AppApi

class LabsRepository {
    suspend fun getLabs(map: Map<String, String>) = AppApi.labServices.fetchLabs(map)
    suspend fun getReviews(labId: Int) = AppApi.labServices.fetchLabReviews(labId)
    suspend fun getSliderData() = AppApi.labServices.fetchLabSlider()
    suspend fun bookExamination(map: Map<String, String>) = AppApi.labServices.bookExamination(map)
    suspend fun getReservations() = AppApi.labServices.getReservations()
}