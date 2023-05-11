package code_grow.afeety.app.view_model

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import code_grow.afeety.app.repository.AuthRepository

class OtpViewModel(
    private val application: Application,
    private val repo: AuthRepository
) : ViewModel() {
    /* user OTP input */
    private val otpLiveData = MutableLiveData("")

    /* OTP error message */
    /* 0-> nothing wrong, 1-> empty error, 2-> length error */
    val otpErrorLiveData = MutableLiveData(0)

    val submitBooleanLiveData = MutableLiveData(false)

    /* progress visibility & buttons clickable or not */
    val progressLiveData = MutableLiveData(false)


    fun setOtpLiveData(value: String) {
        otpLiveData.value = value
    }

    fun setOtpErrorLiveData(value: Int) {
        otpErrorLiveData.value = value
    }

    /* validate sendCode form */
    fun submitBtnClicked() {
        var pass = true
        if (otpLiveData.value.isNullOrEmpty()) {
            otpErrorLiveData.value = 1
            pass = false
        } else {
            if (otpLiveData.value!!.length != 4) {
                otpErrorLiveData.value = 2
                pass = false
            }
        }

        /* start sendCode req */
        if (pass)
            submitBooleanLiveData.value = true
    }
}