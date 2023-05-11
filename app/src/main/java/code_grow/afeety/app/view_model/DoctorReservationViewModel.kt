package code_grow.afeety.app.view_model

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code_grow.afeety.app.R
import code_grow.afeety.app.kot_pref.UserInfo
import code_grow.afeety.app.model.Doctor
import code_grow.afeety.app.model.DoctorSchedule
import code_grow.afeety.app.repository.HospitalsRepository
import code_grow.afeety.app.retrofit.Resource
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "DoctorReservationViewModel"

class DoctorReservationViewModel(
    private val doctorDetails: Doctor,
    private val selectedSchedule: DoctorSchedule,
    private val application: Application,
    private val repo: HospitalsRepository
) : ViewModel() {
    /* username input */
    val userNameLiveData = MutableLiveData(UserInfo.username)

    /* username error message */
    val userNameErrorLiveData = MutableLiveData("")

    /* phone number input */
    val phoneNumberLiveData = MutableLiveData(UserInfo.phoneNumber)

    /* phone error message */
    val phoneNumberErrorLiveData = MutableLiveData("")

    /* access medical file input */
    val accessMedicalFileLiveData = MutableLiveData(R.id.access_medical_files_yes)

    val isHomeVisitLiveData =
        MutableLiveData(if (doctorDetails.isHomeVisiting == 1) View.VISIBLE else View.GONE)

    /* home visit input */
    val homeVisitLiveData = MutableLiveData(R.id.home_visit_yes)

    private val _startBooking = MutableLiveData(false)
    val startBooking: LiveData<Boolean> get() = _startBooking

    fun setStartBooking(value: Boolean) {
        _startBooking.value = value
    }

    fun bookBtnClicked() {
        var pass = true

        if (!validatePhone(phoneNumberLiveData.value!!))
            pass = false
        if (!validateUsername(userNameLiveData.value!!))
            pass = false

        if (pass)
            book()
    }

    private val _bookingResponse = MutableLiveData<Resource>(Resource.Idle)
    val bookingResponse: LiveData<Resource> get() = _bookingResponse

    @SuppressLint("LongLogTag")
    fun book() {
        val map = mutableMapOf<String, String>()
        map["hospital_id"] = doctorDetails.hospitalId.toString()
        map["user_id"] = UserInfo.userId.toString()
        map["doctor_id"] = doctorDetails.doctorId.toString()
        if (doctorDetails.isHomeVisiting == 1)
            map["home_visit"] = if (homeVisitLiveData.value == R.id.home_visit_yes) "1" else "0"
        map["medical_record"] =
            if (accessMedicalFileLiveData.value == R.id.access_medical_files_yes) "1" else "0"
        map["price"] = doctorDetails.bookingPrice.toString()
        map["time_doctor_id"] = selectedSchedule.id.toString()
        if (uploadedReport.isNotEmpty())
            map["attached_file"] = uploadedReport


        map["book_date"] = getBookDate()



        viewModelScope.launch {
            try {
                _bookingResponse.value = Resource.Loading
                val apiResponse = repo.bookDoctor(map)
                if (apiResponse.status) {
                    _bookingResponse.value = Resource.Success("")
                } else {
                    _bookingResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Book-Req. Failed: $e")
                _bookingResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    private fun getBookDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US);
        val calendar = Calendar.getInstance();

        var selectedDayNumber = selectedSchedule.dayNumber
//        if (selectedDayNumber == 0)
//            selectedDayNumber = 7

        while (calendar.get(Calendar.DAY_OF_WEEK) != selectedDayNumber) {
            calendar.add(Calendar.DATE, 1)
        }

        if (calendar.get(Calendar.DATE) == Calendar.getInstance().get(Calendar.DATE)) {
            if (Calendar.getInstance()
                    .get(Calendar.HOUR_OF_DAY) > selectedSchedule.endTime.split(":")[0].toInt()
            ) {
                calendar.add(Calendar.DATE, 7)
            }
        }
        calendar.add(Calendar.DATE,-1)
        Log.d("TAGTAG", "NextDate: ${sdf.format(calendar.time)}")
        return sdf.format(calendar.time);
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

    private val _startUploadingImage = MutableLiveData(false)
    val startUploadingImage: LiveData<Boolean> get() = _startUploadingImage


    fun setStartUploadingImage(value: Boolean, image: File?) {
        if (image != null)
            this.file = image
        _startUploadingImage.value = value
    }

    private val _uploadReportResponse = MutableLiveData<Resource>(Resource.Idle)
    val uploadReportResponse: LiveData<Resource> get() = _uploadReportResponse
    var uploadedReport = ""
    var cancelUploadingReport =
        false /* if user tried to remove his report while it getting uploaded */
    private lateinit var file: File

    /*
        Start uploading report using MultiPart request
        (uploadedReport = "") in case if user uploaded report successfully before and decided to change it before register. so if the second one failed we have to reset the last one
     */
    @SuppressLint("LongLogTag")
    fun uploadReport() {
        viewModelScope.launch {
            try {

                val fileRequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData(
                    "parameters[0]",
                    file.name,
                    fileRequestBody
                )
                _uploadReportResponse.value = Resource.Loading
                val apiResponse = repo.uploadReport(body)
                if (!cancelUploadingReport) {
                    if (!apiResponse.data.isNullOrEmpty()) {
                        uploadedReport =
                            apiResponse.data[0] /* response is list & the uploaded data is one reportImage so we get the first object of the list */
                        _uploadReportResponse.value = Resource.Success(apiResponse.data)
                    } else {
                        uploadedReport = ""
                        if (apiResponse.message != null) {
                            _uploadReportResponse.value = Resource.Failed(apiResponse.message)
                        } else {
                            _uploadReportResponse.value =
                                Resource.Failed(application.getString(R.string.something_went_wrong_try_again_later))
                        }
                    }
                } else
                    _uploadReportResponse.value = Resource.Idle
                cancelUploadingReport = false
            } catch (fileException: FileNotFoundException) {
                if (!cancelUploadingReport) {
                    uploadedReport = ""
                    Log.d(TAG, "UploadReport-FileNotFound: ${fileException.message}")
                    _uploadReportResponse.value = Resource.Failed(fileException.message.toString())
                } else
                    _uploadReportResponse.value = Resource.Idle
                cancelUploadingReport = false

            } catch (e: Exception) {
                if (!cancelUploadingReport) {
                    uploadedReport = ""
                    Log.d(TAG, "UploadReport-reqFailed: $e")
                    _uploadReportResponse.value = Resource.Failed(catchNetworkException(e))
                } else
                    _uploadReportResponse.value = Resource.Idle
                cancelUploadingReport = false
            }
        }
    }

    fun removeReport() {
        uploadedReport = ""
        if (_uploadReportResponse.value is Resource.Loading) {
            cancelUploadingReport = true
        }
    }


}