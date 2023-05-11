package code_grow.afeety.app.view_model

import android.app.Application
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code_grow.afeety.app.R
import code_grow.afeety.app.kot_pref.UserInfo
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
import kotlin.Exception

private const val TAG = "EditProfileViewModel"

class EditProfileViewModel(
    private val user: LoginUser,
    private val application: Application,
    private val repo: AuthRepository
) : ViewModel() {
    /* user username input */
    val userNameLiveData = MutableLiveData(user.username)

    /* username error message */
    val userNameErrorLiveData = MutableLiveData("")

    /* user phone number input */
    val phoneNumberLiveData = MutableLiveData(user.phoneNumber)

    /* phone error message */
    val phoneNumberErrorLiveData = MutableLiveData("")

    /* user email input */
    val emailLiveData = MutableLiveData(user.email)

    /* email error message */
    val emailErrorLiveData = MutableLiveData("")

    /* user age input */
    val ageLiveData = MutableLiveData(user.age.toString())

    /* age error message */
    val ageErrorLiveData = MutableLiveData("")

    /* user gender input */
    val genderLiveData = MutableLiveData("")

    /* gender error message */
    val genderErrorLiveData = MutableLiveData("")

    /* user city input */
    val cityLiveData = MutableLiveData("")

    /* city error message */
    val cityErrorLiveData = MutableLiveData("")

    val editProfileBooleanLiveData = MutableLiveData(false)

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
                    progressState.value = View.GONE
                    _citiesResponse.value =
                        Resource.Success(apiResponse.data)
                } else {
                    progressState.value = View.GONE
                    _citiesResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Cities-Req. Failed: $e")
                progressState.value = View.GONE
                _citiesResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    /* validate registration form */
    fun editProfileBtnClicked() {
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
        if (!validateGender(genderLiveData.value!!))
            pass = false
        if (!validateCity(cityLiveData.value!!))
            pass = false


        /* start edit req */
        if (pass)
            editProfileBooleanLiveData.value = true

    }

    private val _editProfileResponse = MutableLiveData<Resource>(Resource.Idle)
    val editProfileResponse: LiveData<Resource> get() = _editProfileResponse

    /* call register req. */
    fun editProfile() {
        viewModelScope.launch {
            val map = mutableMapOf<String, String>()

            map["id"] = user.userId.toString()
            if (uploadedImage.isNotEmpty())
                map["image"] = uploadedImage
            map["mobile"] = phoneNumberLiveData.value!!
            map["name"] = userNameLiveData.value!!
            map["username"] = emailLiveData.value!!/* email */
            map["longitude"] = UserInfo.latitude
            map["latitude"] = UserInfo.longitude
            val cities = (_citiesResponse.value!! as Resource.Success<MutableList<City>>).data
            map["city_id"] = cities.find {
                it.name == cityLiveData.value!!
            }?.cityId.toString()
            map["age"] = ageLiveData.value!!
            map["gender"] =
                if (genderLiveData.value!! == application.resources.getStringArray(R.array.gender)[0]) "1" else "2"
            try {
                progressState.value = View.VISIBLE
                _editProfileResponse.value = Resource.Loading
                val apiResponse = repo.editProfile(map)
                if (apiResponse.status) {
                    progressState.value = View.GONE
                    _editProfileResponse.value = Resource.Success(apiResponse.data as LoginUser)
                } else {
                    progressState.value = View.GONE
                    _editProfileResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                progressState.value = View.GONE
                Log.d(TAG, "EditProfileViewModel-Req. Failed: $e")
                _editProfileResponse.value = Resource.Failed(catchNetworkException(e))
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
        } else
            true
    }

    private fun validatePhone(phone: String) = if (phone.isEmpty()) {
        phoneNumberErrorLiveData.value =
            application.getString(R.string.empty_field_error_message)
        false
    } else true

    private fun validateEmail(email: String) = if (email.isEmpty()) {
        emailErrorLiveData.value =
            application.getString(R.string.empty_field_error_message)
        false
    } else {
        if (Patterns.EMAIL_ADDRESS.matcher(email).matches())
            true
        else {
            emailErrorLiveData.value =
                application.getString(R.string.email_validation_error_message)
            false
        }
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

}