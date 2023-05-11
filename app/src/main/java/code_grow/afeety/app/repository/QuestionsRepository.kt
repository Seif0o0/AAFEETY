package code_grow.afeety.app.repository

import code_grow.afeety.app.retrofit.AppApi

class QuestionsRepository {
    suspend fun getQuestions(map: Map<String,String>) = AppApi.questionsServices.fetchQuestions(map)
    suspend fun getSpecialities(map: Map<String,Int>) = AppApi.hospitalServices.fetchSpecialities(map)
    suspend fun sendQuestion(map: Map<String, String>) = AppApi.questionsServices.sendQuestion(map)
}