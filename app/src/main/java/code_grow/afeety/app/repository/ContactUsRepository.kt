package code_grow.afeety.app.repository

import code_grow.afeety.app.retrofit.AppApi

class ContactUsRepository {
    suspend fun sendFeedback(map: Map<String, String>) = AppApi.globalServices.sendFeedback(map)
}