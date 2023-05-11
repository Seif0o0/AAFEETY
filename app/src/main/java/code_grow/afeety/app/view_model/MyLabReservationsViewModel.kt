package code_grow.afeety.app.view_model

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code_grow.afeety.app.R
import code_grow.afeety.app.model.LabBooking
import code_grow.afeety.app.repository.LabsRepository
import code_grow.afeety.app.retrofit.Resource
import kotlinx.coroutines.launch
import java.io.IOException

private const val TAG = "LabReservationsViewModel"

class MyLabReservationsViewModel(
    val application: Application,
    val repo: LabsRepository
) : ViewModel() {

    private val _bookingResponse = MutableLiveData<Resource>(Resource.Idle)
    val bookingResponse: LiveData<Resource> get() = _bookingResponse

    init {
        getReservations()
    }

    @SuppressLint("LongLogTag")
    fun getReservations() {
        viewModelScope.launch {
            try {
                _bookingResponse.value = Resource.Loading
                val apiResponse = repo.getReservations()
                if (apiResponse.status) {
                    if ((apiResponse.data as MutableList<LabBooking>).isEmpty())
                        _bookingResponse.value = Resource.Empty
                    else
                        _bookingResponse.value =
                            Resource.Success(apiResponse.data)
                } else {
                    _bookingResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "LabReservations-Req. Failed: $e")
                _bookingResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    private fun catchNetworkException(e: java.lang.Exception) = when (e) {
        is IOException -> application.getString(R.string.no_internet_connection)
        else -> application.getString(R.string.something_went_wrong_try_again_later)
    }
}