package code_grow.afeety.app.view_model

import android.app.Application
import android.location.Location
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code_grow.afeety.app.R
import code_grow.afeety.app.model.City
import code_grow.afeety.app.model.LoginUser
import code_grow.afeety.app.repository.AuthRepository
import code_grow.afeety.app.retrofit.Resource
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.util.regex.Pattern
import kotlin.Exception

private const val TAG = "RegisterViewModel"

class RegisterViewModel(
    private val application: Application,
    private val repo: AuthRepository
) : ViewModel() {
    /* user username input */
    val userNameLiveData = MutableLiveData("")

    /* username error message */
    val userNameErrorLiveData = MutableLiveData("")

    /* user phone number input */
    val phoneNumberLiveData = MutableLiveData("")

    /* phone error message */
    val phoneNumberErrorLiveData = MutableLiveData("")

    /* user email input */
    val emailLiveData = MutableLiveData("")

    /* email error message */
    val emailErrorLiveData = MutableLiveData("")

    /* user age input */
    val ageLiveData = MutableLiveData("")

    /* age error message */
    val ageErrorLiveData = MutableLiveData("")

    /* user password input */
    val passwordLiveData = MutableLiveData("")

    /* password error message */
    val passwordErrorLiveData = MutableLiveData("")

    /* user confirm password input */
    val confirmPasswordLiveData = MutableLiveData("")

    /* confirm password error message */
    val confirmPasswordErrorLiveData = MutableLiveData("")

    /* user gender input */
    val genderLiveData = MutableLiveData("")

    /* gender error message */
    val genderErrorLiveData = MutableLiveData("")

    /* user city input */
    val cityLiveData = MutableLiveData("")

    /* city error message */
    val cityErrorLiveData = MutableLiveData("")

    /* user terms & conditions input */
    val termsConditionsLiveData = MutableLiveData("0")

    /* terms & conditions error message */
    val termsConditionsErrorLiveData = MutableLiveData(false)

    private val _userCurrentLocation = MutableLiveData<Location>()
    val userCurrentLocation: LiveData<Location> get() = _userCurrentLocation

    fun setUserCurrentLocation(location: Location) {
        _userCurrentLocation.value = location
    }

    val registerBooleanLiveData = MutableLiveData(false)

    val progressState = MutableLiveData(View.GONE)

    private val _citiesResponse = MutableLiveData<Resource>()
    val citiesResponse: LiveData<Resource> get() = _citiesResponse

    init {
        getCities()
    }

    fun getCities() {
        viewModelScope.launch {
            try {
                progressState.value = View.VISIBLE
                _citiesResponse.value = Resource.Loading
                val apiResponse = repo.getCities()
                if (apiResponse.status) {
//                    if ((apiResponse.data as MutableList<City>).isEmpty())
//                        _citiesResponse.value = Resource.Empty
//                    else
                    progressState.value = View.GONE
                    _citiesResponse.value =
                        Resource.Success(apiResponse.data)
                } else {
                    progressState.value = View.GONE
                    _citiesResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "viewModel-Req. Failed: $e")
                progressState.value = View.GONE
                _citiesResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    /* validate registration form */
    fun registerBtnClicked() {
        var pass = true
        if (_uploadImageResponse.value is Resource.Loading) {
            imageMessageLiveData.value =
                application.getString(R.string.wait_uploading_photo_message)
            pass = false
        }
        if (!validateUsername(userNameLiveData.value!!))
            pass = false
        if (!validatePhone(phoneNumberLiveData.value!!))
            pass = false
        if (!validateEmail(emailLiveData.value!!))
            pass = false
        if (!validateAge(ageLiveData.value!!))
            pass = false
        if (!validatePassword(passwordLiveData.value!!))
            pass = false
        if (!validateConfirmPassword(confirmPasswordLiveData.value!!))
            pass = false
        if (!validateGender(genderLiveData.value!!))
            pass = false
        if (!validateCity(cityLiveData.value!!))
            pass = false
        if (!validateTermsConditions(termsConditionsLiveData.value!!))
            pass = false


        /* start register req */
        if (pass)
            registerBooleanLiveData.value = true

    }

    private val _registerResponse = MutableLiveData<Resource>(Resource.Idle)
    val registerResponse: LiveData<Resource> get() = _registerResponse

    /* call register req. */
    fun register() {
        viewModelScope.launch {
            /* image=image.jpg */
            val map = mutableMapOf<String, String>()

            if (uploadedImage.isNotEmpty())
                map["image"] = uploadedImage
            map["password"] = passwordLiveData.value!!
            map["mobile"] = phoneNumberLiveData.value!!
            map["name"] = userNameLiveData.value!!
            map["username"] = emailLiveData.value!!/* email */
            if (userCurrentLocation.value != null) {
                map["longitude"] = (userCurrentLocation.value as Location).longitude.toString()
                map["latitude"] = (userCurrentLocation.value as Location).latitude.toString()
            }


            val cities = (_citiesResponse.value!! as Resource.Success<MutableList<City>>).data
            map["city_id"] = cities.find {
                it.name == cityLiveData.value!!
            }?.cityId.toString()
            map["age"] = ageLiveData.value!!
            map["gender"] =
                if (genderLiveData.value!! == application.resources.getStringArray(R.array.gender)[0]) "1" else "2"
            try {
                progressState.value = View.VISIBLE
                _registerResponse.value = Resource.Loading
                val apiResponse = repo.register(map)
                if (apiResponse.status) {
                    progressState.value = View.GONE
                    _registerResponse.value = Resource.Success(apiResponse.data as LoginUser)
                } else {
                    progressState.value = View.GONE
                    _registerResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                progressState.value = View.GONE
                Log.d("Register", "viewModel-Req. Failed: $e")
                _registerResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    private fun catchNetworkException(e: Exception) = when (e) {
        is IOException -> application.getString(R.string.no_internet_connection)
        else -> application.getString(R.string.something_went_wrong_try_again_later)
    }

    private val _startUploadingImage = MutableLiveData(false)
    val startUploadingImage: LiveData<Boolean> get() = _startUploadingImage

    fun setStartUploadingImage(value: Boolean, image: File?) {
        if (image != null)
            this.file = image
        _startUploadingImage.value = value
    }

    private val _uploadImageResponse = MutableLiveData<Resource>(Resource.Idle)
    val uploadImageResponse: LiveData<Resource> get() = _uploadImageResponse
    val imageMessageLiveData = MutableLiveData("")
    private var uploadedImage = ""
    private lateinit var file: File

    /*
        Start uploading image using MultiPart request
        (uploadedImage = "") in case if user uploaded image successfully before and decided to change it before register. so if the second one failed we have to reset the last one
     */
    fun uploadImage() {
        viewModelScope.launch {
            try {

                val fileRequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData(
                    "parameters[0]",
                    file.name,
                    fileRequestBody
                )

                _uploadImageResponse.value = Resource.Loading
                val apiResponse = repo.uploadImage(body)
                if (!apiResponse.data.isNullOrEmpty()) {
                    uploadedImage =
                        apiResponse.data[0] /* response is list & the uploaded data is one image so we get the first object of the list */
                    _uploadImageResponse.value = Resource.Success(apiResponse.data)
                } else {
                    uploadedImage = ""
                    if (apiResponse.message != null) {
                        _uploadImageResponse.value = Resource.Failed(apiResponse.message)
                    } else {
                        _uploadImageResponse.value =
                            Resource.Failed(application.getString(R.string.something_went_wrong_try_again_later))
                    }
                }
            } catch (fileException: FileNotFoundException) {
                uploadedImage = ""
                Log.d(TAG, "UploadPhoto-FileNotFound: ${fileException.message}")
                _uploadImageResponse.value = Resource.Failed(fileException.message.toString())
            } catch (e: Exception) {
                uploadedImage = ""
                Log.d(TAG, "UploadPhoto-reqFailed: $e")
                _uploadImageResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    private fun validateUsername(username: String) = if (username.isEmpty()) {
        userNameErrorLiveData.value = application.getString(R.string.empty_field_error_message)
        false
    } else {
        if (username.length < 3) {
            userNameErrorLiveData.value =
                application.getString(R.string.username_length_error_message)
            false
        } else if (username.length > 30) {
            userNameErrorLiveData.value =
                application.getString(R.string.username_length_error_message_1)
            false
        } else
            true
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


    private fun validateEmail(email: String) = if (email.isEmpty()) {
        emailErrorLiveData.value =
            application.getString(R.string.empty_field_error_message)
        false
    } else {
        if (validateEmailFormat(email))
            true
        else {
            emailErrorLiveData.value =
                application.getString(R.string.email_validation_error_message)
            false
        }
    }

    private fun validateEmailFormat(email: String): Boolean {
        val regExpression =
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        val pattern = Pattern.compile(regExpression, Pattern.CASE_INSENSITIVE)

        return pattern.matcher(email).matches()
    }

    private fun validateAge(age: String) = if (age.isEmpty()) {
        ageErrorLiveData.value =
            application.getString(R.string.empty_field_error_message)
        false
    } else {
        if (age.toInt() < 16) {
            ageErrorLiveData.value = application.getString(R.string.age_range_error_massage)
            false
        } else if (age.toInt() > 90) {
            ageErrorLiveData.value = application.getString(R.string.age_range_error_massage_1)
            false
        } else
            true
    }

    private fun validatePassword(password: String) = if (password.isEmpty()) {
        passwordErrorLiveData.value = application.getString(R.string.empty_field_error_message)
        false
    } else {
        if (password.length < 6) {
            passwordErrorLiveData.value =
                application.getString(R.string.password_length_error_message)
            false
        } else
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

    private fun validateGender(gender: String) = if (gender.isEmpty()) {
        genderErrorLiveData.value =
            application.getString(R.string.empty_spinner_field_error_message)
        false
    } else {
        true
    }

    private fun validateCity(city: String) = if (city.isEmpty()) {
        cityErrorLiveData.value =
            application.getString(R.string.empty_spinner_field_error_message)
        false
    } else {
        true
    }

    private fun validateTermsConditions(termsStatus: String) =
        if (termsStatus == "0") {
            termsConditionsErrorLiveData.value = true
            false
        } else
            true

    fun checkTermsConditions() {
        termsConditionsErrorLiveData.value = false
        if (termsConditionsLiveData.value!! == "0") {
            termsConditionsLiveData.value = "1"
        } else {
            termsConditionsLiveData.value = "0"
        }
    }


}