package code_grow.afeety.app.view_model

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code_grow.afeety.app.R
import code_grow.afeety.app.model.Question
import code_grow.afeety.app.model.Speciality
import code_grow.afeety.app.repository.QuestionsRepository
import code_grow.afeety.app.retrofit.Resource
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception

private const val TAG = "FilteredQuestionsViewModel"

class FilteredQuestionsViewModel(
    private val application: Application,
    private val repo: QuestionsRepository
) : ViewModel() {

    private val _startRequestSpecialities = MutableLiveData(false)
    val startRequestSpecialities: LiveData<Boolean> get() = _startRequestSpecialities

    private val _specialitiesResponse = MutableLiveData<Resource>(Resource.Idle)
    val specialitiesResponse: LiveData<Resource> get() = _specialitiesResponse

    fun startRequestSpecialities(value: Boolean) {
        _startRequestSpecialities.value = value
    }

    private val _startRequestQuestions = MutableLiveData(false)
    val startRequestQuestions: LiveData<Boolean> get() = _startRequestQuestions

    private val _questionsResponse = MutableLiveData<Resource>(Resource.Idle)
    val questionsResponse: LiveData<Resource> get() = _questionsResponse


    fun startRequestQuestions(value: Boolean) {
        _startRequestQuestions.value = value
    }

    private val questionsQueryMap = mutableMapOf<String, String>()
    val progressState = MutableLiveData<Resource>(Resource.Idle)

    init {
        startRequestSpecialities(true)
    }

    @SuppressLint("LongLogTag")
    fun getSpecialities() {
        viewModelScope.launch {
            try {
                progressState.value = Resource.Loading
                _specialitiesResponse.value = Resource.Loading
                val apiResponse =
                    repo.getSpecialities(mutableMapOf())
                if (apiResponse.status) {
                    val data = apiResponse.data as MutableList<Speciality>
                    data[0].clicked = true
                    filterQuestions(data[0].specialityId)
                    _specialitiesResponse.value = Resource.Success(data)
                } else {
                    _specialitiesResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Specialities-Req. Failed: $e")
                _specialitiesResponse.value = Resource.Failed(catchNetworkException(e))
            } finally {
                progressState.value = Resource.Idle
            }
        }
    }


    @SuppressLint("LongLogTag")
    fun getQuestions() {
        viewModelScope.launch {
            try {
                progressState.value = Resource.Loading
                _questionsResponse.value = Resource.Loading
                val apiResponse = repo.getQuestions(questionsQueryMap)
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
            } finally {
                progressState.value = Resource.Idle
            }
        }
    }


    private fun catchNetworkException(e: Exception) = when (e) {
        is IOException -> application.getString(R.string.no_internet_connection)
        else -> application.getString(R.string.something_went_wrong_try_again_later)
    }

    var currentQuery = ""
    fun filterQuestions(specialityId: Int) {
        if (currentQuery.isEmpty())
            questionsQueryMap.clear()
        else questionsQueryMap["search"] = currentQuery

        questionsQueryMap["category_id"] = specialityId.toString()

        startRequestQuestions(true)
    }
}