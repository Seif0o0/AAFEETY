package code_grow.afeety.app.view_model

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code_grow.afeety.app.R
import code_grow.afeety.app.kot_pref.UserInfo
import code_grow.afeety.app.model.Speciality
import code_grow.afeety.app.repository.QuestionsRepository
import code_grow.afeety.app.retrofit.Resource
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception

private const val TAG = "SendQuestionViewModel"

class SendQuestionViewModel(
    private val application: Application,
    private val repo: QuestionsRepository
) : ViewModel() {

    /* user question input */
    val questionLiveData = MutableLiveData("")

    /* question error message */
    val questionErrorLiveData = MutableLiveData("")

    /* user specialities input */
    val specialitiesLiveData = MutableLiveData("")

    /* specialities error message */
    val specialitiesErrorLiveData = MutableLiveData("")


    private val _startRequestSpecialities = MutableLiveData(false)
    val startRequestSpecialities: LiveData<Boolean> get() = _startRequestSpecialities

    private val _specialitiesResponse = MutableLiveData<Resource>(Resource.Idle)
    val specialitiesResponse: LiveData<Resource> get() = _specialitiesResponse

    fun startRequestSpecialities(value: Boolean) {
        _startRequestSpecialities.value = value
    }

    val progressState = MutableLiveData<Resource>(Resource.Idle)

    private val _startSendQuestion = MutableLiveData(false)
    val startSendQuestion: LiveData<Boolean> get() = _startSendQuestion

    fun startSendQuestion(value: Boolean) {
        _startSendQuestion.value = value
    }

    private val _sendQuestionResponse = MutableLiveData<Resource>(Resource.Idle)
    val sendQuestionResponse: LiveData<Resource> get() = _sendQuestionResponse

    private val _areasResponse = MutableLiveData<Resource>()
    val areasResponse: LiveData<Resource> get() = _areasResponse

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
                    _specialitiesResponse.value =
                        Resource.Success(apiResponse.data as MutableList<Speciality>)
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

    var specialityId = ""
    fun sendBtnClicked() {
        var pass = true
        if (!validateQuestion(questionLiveData.value!!))
            pass = false
        if (!validateSpeciality(specialitiesLiveData.value!!))
            pass = false

        if (pass) {
            val specialities =
                (_specialitiesResponse.value as Resource.Success<MutableList<Speciality>>).data
            specialityId = specialities.find {
                it.name == specialitiesLiveData.value!!
            }?.specialityId.toString()

            startSendQuestion(true)
        }
    }

    fun sendQuestion() {
        val map = mutableMapOf<String, String>()
        map["user_id"] = UserInfo.userId.toString()
        map["question"] = questionLiveData.value.toString()
        map["category_id"] = specialityId

        viewModelScope.launch {
            try {
                progressState.value = Resource.Loading
                _sendQuestionResponse.value = Resource.Loading
                val apiResponse = repo.sendQuestion(map)
                if (apiResponse.status) {
                    _sendQuestionResponse.value = Resource.Success(apiResponse.message)
                } else {
                    _sendQuestionResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "SendQuestion-Req. Failed: $e")
                _sendQuestionResponse.value = Resource.Failed(catchNetworkException(e))
            } finally {
                progressState.value = Resource.Idle
            }
        }

    }

    private fun validateQuestion(question: String) = if (question.isEmpty()) {
        questionErrorLiveData.value =
            application.getString(R.string.empty_field_error_message)
        false
    } else {
        true
    }

    private fun validateSpeciality(specialities: String) = if (specialities.isEmpty()) {
        specialitiesErrorLiveData.value =
            application.getString(R.string.empty_spinner_field_error_message)
        false
    } else {
        true
    }

    private fun catchNetworkException(e: Exception) = when (e) {
        is IOException -> application.getString(R.string.no_internet_connection)
        else -> application.getString(R.string.something_went_wrong_try_again_later)
    }
}