package code_grow.afeety.app.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code_grow.afeety.app.R
import code_grow.afeety.app.model.Doctor
import code_grow.afeety.app.repository.HospitalsRepository
import code_grow.afeety.app.retrofit.Resource
import code_grow.afeety.app.kot_pref.UserInfo
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception

class DoctorsViewModel(
    private val hospitalId: Int,
    private val application: Application,
    private val repo: HospitalsRepository
) : ViewModel() {

    private val _startRequestDoctors = MutableLiveData(false)
    val startRequestDoctors: LiveData<Boolean> get() = _startRequestDoctors

    private val _doctorsResponse = MutableLiveData<Resource>(Resource.Idle)
    val doctorsResponse: LiveData<Resource> get() = _doctorsResponse


    fun setStartRequestDoctors(value: Boolean) {
        _startRequestDoctors.value = value
    }

    val doctorsQueryMap = mutableMapOf<String, Int>()

    init {
        doctorsQueryMap["city_id"] = UserInfo.cityId
        doctorsQueryMap["hospital_id"] = hospitalId
        setStartRequestDoctors(true) // start calling doctors req. (speciality all)
        getDoctors()
    }


    fun getDoctors() {
        viewModelScope.launch {
            try {
                _doctorsResponse.value = Resource.Loading
                val apiResponse = repo.getDoctors(doctorsQueryMap)
                if (apiResponse.status) {
                    if ((apiResponse.data as MutableList<Doctor>).isEmpty())
                        _doctorsResponse.value = Resource.Empty
                    else
                        _doctorsResponse.value =
                            Resource.Success(apiResponse.data)
                } else {
                    _doctorsResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d("DoctorsFragment", "viewModel-Req. Failed: $e")
                _doctorsResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }


    private fun catchNetworkException(e: Exception) = when (e) {
        is IOException -> application.getString(R.string.no_internet_connection)
        else -> application.getString(R.string.something_went_wrong_try_again_later)
    }

    fun filterBySpeciality(specialityId: Int) {
        doctorsQueryMap.clear()
        doctorsQueryMap["city_id"] = UserInfo.cityId
        doctorsQueryMap["hospital_id"] = hospitalId
        if (specialityId != 0)
            doctorsQueryMap["speciality_id"] = specialityId
        setStartRequestDoctors(true)
    }
}