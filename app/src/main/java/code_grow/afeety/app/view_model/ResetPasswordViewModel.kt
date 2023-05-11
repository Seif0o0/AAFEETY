package code_grow.afeety.app.view_model

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import code_grow.afeety.app.R
import code_grow.afeety.app.repository.AuthRepository

class ResetPasswordViewModel(
    private val application: Application,
    private val repo: AuthRepository
) : ViewModel() {
    /* user password input */
    val passwordLiveData = MutableLiveData("")

    /* password error message */
    val passwordErrorLiveData = MutableLiveData("")

    /* user confirm password input */
    val confirmPasswordLiveData = MutableLiveData("")

    /* confirm password error message */
    val confirmPasswordErrorLiveData = MutableLiveData("")

    val changePasswordBooleanLiveData = MutableLiveData(false)


    fun changePasswordBtnClicked() {
        var pass = true
        if (!validatePassword(passwordLiveData.value!!))
            pass = false
        if (!validateConfirmPassword(confirmPasswordLiveData.value!!))
            pass = false
        /* start change password req */
        if (pass)
            changePasswordBooleanLiveData.value = true
    }

    private fun validatePassword(password: String) = if (password.isEmpty()) {
        passwordErrorLiveData.value = application.getString(R.string.empty_field_error_message)
        false
    } else {
        true
    }

    private fun validateConfirmPassword(password: String) = if (password.isEmpty()) {
        confirmPasswordErrorLiveData.value =
            application.getString(R.string.empty_field_error_message)
        false
    } else {
        if (password != passwordLiveData.value!!) {
            confirmPasswordErrorLiveData.value =
                application.getString(R.string.confirmation_password_error_message)
            false
        } else
            true
    }

}