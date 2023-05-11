package code_grow.afeety.app.view_model

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import code_grow.afeety.app.R
import code_grow.afeety.app.repository.AuthRepository

class ForgetPasswordViewModel(
    private val application: Application,
    private val repo: AuthRepository
) : ViewModel() {
    /* user phone number input */
    val phoneNumberLiveData = MutableLiveData("")

    /* phone error message */
    val phoneNumberErrorLiveData = MutableLiveData("")

    val sendCodeBooleanLiveData = MutableLiveData(false)

    /* progress visibility & buttons clickable or not */
    val progressLiveData = MutableLiveData(false)


    /* validate sendCode form */
    fun sendCodeBtnClicked() {
        var pass = true
        if (!validatePhone(phoneNumberLiveData.value!!))
            pass = false

        /* start sendCode req */
        if (pass)
            sendCodeBooleanLiveData.value = true
    }

    private fun validatePhone(phone: String) = if (phone.isEmpty()) {
        phoneNumberErrorLiveData.value =
            application.getString(R.string.empty_field_error_message)
        false
    } else {
        if (phone.length != 9) {
            phoneNumberErrorLiveData.value =
                application.getString(R.string.phone_number_length_error_message)
            false
        } else
            true
    }


}