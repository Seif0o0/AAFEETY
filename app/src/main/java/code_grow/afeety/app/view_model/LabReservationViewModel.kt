package code_grow.afeety.app.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code_grow.afeety.app.R
import code_grow.afeety.app.kot_pref.UserInfo
import code_grow.afeety.app.model.LabExamination
import code_grow.afeety.app.repository.LabsRepository
import code_grow.afeety.app.retrofit.Resource
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception

private const val TAG = "LabReservationViewModel"

class LabReservationViewModel(
    private val labId: Int,
    private val examinations: MutableList<LabExamination>,
    private val application: Application,
    private val repo: LabsRepository
) : ViewModel() {

    /* request home visit (user input) */
    val isHomeVisitLiveData = MutableLiveData("0")/* 0-> false, 1-> true*/

    private val _startBooking = MutableLiveData(false)
    val startBooking: LiveData<Boolean> get() = _startBooking

    fun setStartBooking(value: Boolean) {
        _startBooking.value = value
    }

    private val _bookingResponse = MutableLiveData<Resource>(Resource.Idle)
    val bookingResponse: LiveData<Resource> get() = _bookingResponse

    fun book() {
        /*
            lab_id:1
            user_id:1
            home_visit:1
            examination_id[0]:1
            price[0]:10
            examination_id[1]:2
            price[1]:10
         */
        val map = mutableMapOf<String, String>()
        map["lab_id"] = labId.toString()
        map["user_id"] = UserInfo.userId.toString()
        map["home_visit"] = isHomeVisitLiveData.value!!
        for (index in 0 until examinations.size) {
            map["examination_id[$index]"] = examinations[index].examinationId.toString()
            map["price[$index]"] = examinations[index].price.toString()
        }

        viewModelScope.launch {
            try {
                _bookingResponse.value = Resource.Loading
                val apiResponse = repo.bookExamination(map)
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

    private fun catchNetworkException(e: Exception) = when (e) {
        is IOException -> application.getString(R.string.no_internet_connection)
        else -> application.getString(R.string.something_went_wrong_try_again_later)
    }

    fun checkIsHomeVisit() {
        if (isHomeVisitLiveData.value!! == "0")
            isHomeVisitLiveData.value = "1"
        else
            isHomeVisitLiveData.value = "0"
    }

}