package code_grow.afeety.app.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code_grow.afeety.app.R
import code_grow.afeety.app.model.LoginUser
import code_grow.afeety.app.repository.AuthRepository
import code_grow.afeety.app.retrofit.Resource
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception

class LoginViewModel(private val application: Application, private val repo: AuthRepository) :
    ViewModel() {
    var firebaseToken = ""

    /* user phone number input */
    val phoneNumberLiveData = MutableLiveData("")

    /* phone error message */
    val phoneNumberErrorLiveData = MutableLiveData("")

    /* user password input */
    val passwordLiveData = MutableLiveData("")

    /* password error message */
    val passwordErrorLiveData = MutableLiveData("")

    val loginBooleanLiveData = MutableLiveData(false)


    /* validate login form */
    fun loginBtnCLicked() {
        var pass = true
        if (phoneNumberLiveData.value.isNullOrEmpty()) {
            phoneNumberErrorLiveData.value =
                application.getString(R.string.empty_field_error_message)
            pass = false
        } else {
            phoneNumberErrorLiveData.value = ""
        }

        if (passwordLiveData.value.isNullOrEmpty()) {
            passwordErrorLiveData.value = application.getString(R.string.empty_field_error_message)
            pass = false
        } else {
            passwordErrorLiveData.value = ""
        }

        /* start login req */
        if (pass)
            loginBooleanLiveData.value = true

    }

    private val _loginResponse = MutableLiveData<Resource>(Resource.Idle)
    val loginResponse: LiveData<Resource> get() = _loginResponse

    /* call login req. */
    fun login() {
        viewModelScope.launch {
            try {
                _loginResponse.value = Resource.Loading
                val apiResponse = repo.login(phoneNumberLiveData.value!!, passwordLiveData.value!!,firebaseToken)
                if (apiResponse.status) {
                    _loginResponse.value = Resource.Success(apiResponse.data as LoginUser)
                } else {
                    _loginResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d("Login", "viewModel-Req. Failed: $e")
                _loginResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }


    private fun catchNetworkException(e: Exception) = when (e) {
        is IOException -> application.getString(R.string.no_internet_connection)
        else -> application.getString(R.string.something_went_wrong_try_again_later)
    }
}