package code_grow.afeety.app.view_model

import android.annotation.SuppressLint
import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code_grow.afeety.app.R
import code_grow.afeety.app.kot_pref.UserInfo
import code_grow.afeety.app.repository.CartRepository
import code_grow.afeety.app.retrofit.Resource
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.lang.Exception

private const val TAG = "PreOrderDetailsViewModel"

class PreOrderDetailsViewModel(
    private val prescriptionUri: String,
    private val addressId: Int,
    private val pharmacyId:Int,
    private val application: Application,
    private val repo: CartRepository
) : ViewModel() {
    private val _startBuying = MutableLiveData(false)
    val startBuying: LiveData<Boolean> get() = _startBuying

    fun startBuying(value: Boolean) {
        _startBuying.value = value
    }

    private val _buyingResponse = MutableLiveData<Resource>(Resource.Idle)
    val buyingResponse: LiveData<Resource> get() = _buyingResponse

    private val _startUploadingImage = MutableLiveData(false)
    val startUploadingImage: LiveData<Boolean> get() = _startUploadingImage

    fun startUploadingImage(value: Boolean) {
        _startUploadingImage.value = value
    }

    private val _uploadReportResponse = MutableLiveData<Resource>(Resource.Idle)
    val uploadReportResponse: LiveData<Resource> get() = _uploadReportResponse
    var uploadedPrescription = ""

    @SuppressLint("LongLogTag")
    fun uploadPrescription() {
        //        var url = Uri.parse(urlString)
//        var prescription = File(url.path)

        viewModelScope.launch {
            try {
                val file = File(Uri.parse(prescriptionUri)!!.path!!)
                val fileRequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData(
                    "parameters[0]",
                    file.name,
                    fileRequestBody
                )
                _uploadReportResponse.value = Resource.Loading
                val apiResponse = repo.uploadPrescription(body)
                if (!apiResponse.data.isNullOrEmpty()) {
                    uploadedPrescription =
                        apiResponse.data[0] /* response is list & the uploaded data is one reportImage so we get the first object of the list */
                    _uploadReportResponse.value = Resource.Success(apiResponse.data)
                    startBuying(true)
                } else {
                    uploadedPrescription = ""
                    if (apiResponse.message != null) {
                        _uploadReportResponse.value = Resource.Failed(apiResponse.message)
                    } else {
                        _uploadReportResponse.value =
                            Resource.Failed(application.getString(R.string.something_went_wrong_try_again_later))
                    }
                }

            } catch (fileException: FileNotFoundException) {
                uploadedPrescription = ""
                Log.d(TAG, "UploadPrescription-FileNotFound: ${fileException.message}")
                _uploadReportResponse.value = Resource.Failed(fileException.message.toString())


            } catch (e: Exception) {
                uploadedPrescription = ""
                Log.d(TAG, "UploadReport-reqFailed: $e")
                _uploadReportResponse.value = Resource.Failed(catchNetworkException(e))

            }
        }
    }


    @SuppressLint("LongLogTag")
    fun buy() {
        val map = mutableMapOf<String, String>()
        map["pharmacy_id"] = pharmacyId.toString()
        map["user_id"] = UserInfo.userId.toString()
        map["address_id"] = addressId.toString()
        map["prescription"] = uploadedPrescription


        viewModelScope.launch {
            try {
                _buyingResponse.value = Resource.Loading
                val apiResponse = repo.buyMedicine(map)
                if (apiResponse.status) {
                    _buyingResponse.value = Resource.Success("")
                } else {
                    _buyingResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Buy-Req. Failed: $e")
                _buyingResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    private fun catchNetworkException(e: Exception) = when (e) {
        is IOException -> application.getString(R.string.no_internet_connection)
        else -> application.getString(R.string.something_went_wrong_try_again_later)
    }


}