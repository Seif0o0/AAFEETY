package code_grow.afeety.app.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code_grow.afeety.app.R
import code_grow.afeety.app.model.Speciality
import code_grow.afeety.app.repository.HospitalsRepository
import code_grow.afeety.app.retrofit.Resource
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception

class HospitalDetailsViewModel(
    private val hospitalId: Int,
    private val application: Application,
    private val repo: HospitalsRepository
) : ViewModel() {

    private val _startRequestSpecialities = MutableLiveData(false)
    val startRequestSpecialities: LiveData<Boolean> get() = _startRequestSpecialities

    private val _specialitiesResponse = MutableLiveData<Resource>(Resource.Idle)
    val specialitiesResponse: LiveData<Resource> get() = _specialitiesResponse

    fun setStartRequestSpecialities(value: Boolean) {
        _startRequestSpecialities.value = value
    }

    init {
        setStartRequestSpecialities(true) // start calling specialities request.
        getSpecialities()
    }


    fun getSpecialities() {
        viewModelScope.launch {
            try {
                _specialitiesResponse.value = Resource.Loading
                val apiResponse =
                    repo.getSpecialities(mutableMapOf("hospital_id" to hospitalId))
                if (apiResponse.status) {
                    val data = apiResponse.data as MutableList<Speciality>
                    data.add(
                        0,
                        Speciality(
                            0,
                            application.getString(R.string.all),
                            R.drawable.item_test.toString()
                        )
                    )
                    _specialitiesResponse.value = Resource.Success(data)
                } else {
                    _specialitiesResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d("HospitalsViewModel", "Specialities-Req. Failed: $e")
                _specialitiesResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }


    private fun catchNetworkException(e: Exception) = when (e) {
        is IOException -> application.getString(R.string.no_internet_connection)
        else -> application.getString(R.string.something_went_wrong_try_again_later)
    }
}