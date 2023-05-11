package code_grow.afeety.app.retrofit

import code_grow.afeety.app.model.ApiResponse
import code_grow.afeety.app.model.MessageApiResponse
import code_grow.afeety.app.model.Question
import code_grow.afeety.app.utils.Constants
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface QuestionsServices {
    @GET(Constants.QUESTIONS.plus(Constants.GET_QUESTIONS))
    suspend fun fetchQuestions(@QueryMap map: Map<String, String>): ApiResponse<MutableList<Question>>

    @GET(Constants.QUESTIONS.plus(Constants.SEND_QUESTION))
    suspend fun sendQuestion(@QueryMap map: Map<String, String>): MessageApiResponse
}