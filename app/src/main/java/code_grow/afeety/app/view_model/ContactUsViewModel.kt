package code_grow.afeety.app.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code_grow.afeety.app.R
import code_grow.afeety.app.kot_pref.UserInfo
import code_grow.afeety.app.repository.ContactUsRepository
import code_grow.afeety.app.retrofit.Resource
import kotlinx.coroutines.launch
import java.io.IOException

private const val TAG = "ContactUsViewModel"

class ContactUsViewModel(
    private val application: Application,
    private val repo: ContactUsRepository
) : ViewModel() {
    /* user username input */
    val userNameLiveData = MutableLiveData(UserInfo.username)

    /* username error message */
    val userNameErrorLiveData = MutableLiveData("")

    /* user phone number input */
    val phoneNumberLiveData = MutableLiveData(UserInfo.phoneNumber)

    /* phone error message */
    val phoneNumberErrorLiveData = MutableLiveData("")

    /* user message input */
    val messageLiveData = MutableLiveData("")

    /* message error message */
    val messageErrorLiveData = MutableLiveData("")

    private val _sendFeedbackBooleanLiveData = MutableLiveData(false)
    val sendFeedbackBooleanLiveData: LiveData<Boolean> get() = _sendFeedbackBooleanLiveData

    fun startSendingFeedback(value: Boolean) {
        _sendFeedbackBooleanLiveData.value = value
    }

    fun sendBtnClicked() {
        var pass = true
        if (!validateUsername(userNameLiveData.value!!))
            pass = false
        if (!validatePhone(phoneNumberLiveData.value!!))
            pass = false
        if (!validateMessage(messageLiveData.value!!))
            pass = false

        if (pass)
            _sendFeedbackBooleanLiveData.value = true
    }

    private val _sendFeedbackResponse = MutableLiveData<Resource>(Resource.Idle)
    val sendFeedbackResponse: LiveData<Resource> get() = _sendFeedbackResponse


    fun sendFeedback() {
        viewModelScope.launch {
            val map = mutableMapOf<String, String>()
            map["user_id"] = UserInfo.userId.toString()
            map["name"] = userNameLiveData.value!!
            map["mobile"] = phoneNumberLiveData.value!!
            map["comment"] = messageLiveData.value!!

            try {
                _sendFeedbackResponse.value = Resource.Loading
                val apiResponse = repo.sendFeedback(map)
                if (apiResponse.status) {
                    _sendFeedbackResponse.value = Resource.Success(apiResponse.message)
                } else {
                    _sendFeedbackResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "contactUs-Req. Failed: $e")
                _sendFeedbackResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    private fun catchNetworkException(e: Exception) = when (e) {
        is IOException -> application.getString(R.string.no_internet_connection)
        else -> application.getString(R.string.something_went_wrong_try_again_later)
    }

    private fun validateUsername(username: String) = if (username.isEmpty()) {
        userNameErrorLiveData.value = application.getString(R.string.empty_field_error_message)
        false
    } else {
        if (username.length < 3) {
            userNameErrorLiveData.value =
                application.getString(R.string.username_length_error_message)
            false
        } else
            true
    }

    private fun validatePhone(phone: String) = if (phone.isEmpty()) {
        phoneNumberErrorLiveData.value =
            application.getString(R.string.empty_field_error_message)
        false
    } else true

    private fun validateMessage(message: String) = if (message.isEmpty()) {
        messageErrorLiveData.value =
            application.getString(R.string.empty_field_error_message)
        false
    } else true


}