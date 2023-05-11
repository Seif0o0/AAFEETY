package code_grow.afeety.app.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code_grow.afeety.app.R
import code_grow.afeety.app.model.Question
import code_grow.afeety.app.repository.QuestionsRepository
import code_grow.afeety.app.retrofit.Resource
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception

private const val TAG = "QuestionsViewModel"

class QuestionsViewModel(
    private val application: Application,
    private val repo: QuestionsRepository
) : ViewModel() {

    private val _startRequestQuestions = MutableLiveData(false)
    val startRequestQuestions: LiveData<Boolean> get() = _startRequestQuestions

    private val _questionsResponse = MutableLiveData<Resource>(Resource.Idle)
    val questionsResponse: LiveData<Resource> get() = _questionsResponse


    fun startRequestQuestions(value: Boolean) {
        _startRequestQuestions.value = value
    }

    val queryMap = mutableMapOf<String, String>()

    init {
        getQuestions()
    }


    fun getQuestions() {
        viewModelScope.launch {
            try {
                _questionsResponse.value = Resource.Loading
                val apiResponse = repo.getQuestions(queryMap)
                if (apiResponse.status) {
                    if ((apiResponse.data as MutableList<Question>).isEmpty())
                        _questionsResponse.value = Resource.Empty
                    else
                        _questionsResponse.value =
                            Resource.Success(apiResponse.data)
                } else {
                    _questionsResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Questions-Req. Failed: $e")
                _questionsResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    fun updateQueryMap(query: String) {
        if (query.isEmpty())
            queryMap.clear()
        else queryMap["search"] = query
        startRequestQuestions(true)
    }

    private fun catchNetworkException(e: Exception) = when (e) {
        is IOException -> application.getString(R.string.no_internet_connection)
        else -> application.getString(R.string.something_went_wrong_try_again_later)
    }

}