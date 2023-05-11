package code_grow.afeety.app.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code_grow.afeety.app.R
import code_grow.afeety.app.kot_pref.UserInfo
import code_grow.afeety.app.model.Hospital
import code_grow.afeety.app.model.Speciality
import code_grow.afeety.app.repository.HospitalsRepository
import code_grow.afeety.app.retrofit.Resource
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception

private const val TAG = "HospitalsViewModel"

class HospitalsViewModel(
    private val application: Application,
    private val repo: HospitalsRepository
) : ViewModel() {

    private val _startRequestHospitals = MutableLiveData(false)
    val startRequestHospitals: LiveData<Boolean> get() = _startRequestHospitals

    private val _hospitalsResponse = MutableLiveData<Resource>(Resource.Idle)
    val hospitalsResponse: LiveData<Resource> get() = _hospitalsResponse

    val filterByLiveData = MutableLiveData(0)
    val hospitalsQueryMap = mutableMapOf<String, String>()


    private val _specialHospitalsResponse = MutableLiveData<Resource>(Resource.Idle)
    val specialHospitalsResponse: LiveData<Resource> get() = _specialHospitalsResponse

    private val _specialitiesResponse = MutableLiveData<Resource>(Resource.Idle)
    val specialitiesResponse: LiveData<Resource> get() = _specialitiesResponse

    private val _startRequestSlider = MutableLiveData(false)
    val startRequestSlider: LiveData<Boolean> get() = _startRequestSlider

    private val _sliderResponse = MutableLiveData<Resource>(Resource.Idle)
    val sliderResponse: LiveData<Resource> get() = _sliderResponse

    fun setStartRequestSlider(value: Boolean) {
        _startRequestSlider.value = value
    }

    fun setStartRequestHospitals(value: Boolean) {
        _startRequestHospitals.value = value
    }

    init {
        setStartRequestSlider(true)
        if (UserInfo.userId != 0){
            hospitalsQueryMap["latitude"] = UserInfo.latitude
            hospitalsQueryMap["longitude"] = UserInfo.longitude
            hospitalsQueryMap["city_id"] = UserInfo.cityId.toString()
        }
        setStartRequestHospitals(true) // start calling hospitals request.
        getSpecialities()
        getHospitals()
        getSpecialHospitals()
    }

    fun getSlider() {
        viewModelScope.launch {
            try {
                _sliderResponse.value = Resource.Loading
                val apiResponse = repo.getSliderData()
                if (apiResponse.status) {
                    _sliderResponse.value =
                        Resource.Success(apiResponse.sliderItems)
                } else {
                    _sliderResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Slider-Req. Failed: $e")
                _sliderResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    fun getSpecialities() {
        viewModelScope.launch {
            try {
                _specialitiesResponse.value = Resource.Loading
                val apiResponse = repo.getSpecialities(mutableMapOf())
                if (apiResponse.status) {
                    val data = apiResponse.data as MutableList<Speciality>
                    data.add(
                        0,
                        Speciality(
                            0,
                            application.getString(R.string.all),
                            R.drawable.ic_all_specialities.toString(),
                            true
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

    fun getHospitals() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "ViewModel ${hospitalsQueryMap["search"]}")
                _hospitalsResponse.value = Resource.Loading
                val apiResponse = repo.getHospitals(hospitalsQueryMap)
                if (apiResponse.status) {
                    if ((apiResponse.data as MutableList<Hospital>).isEmpty())
                        _hospitalsResponse.value = Resource.Empty
                    else
                        _hospitalsResponse.value =
                            Resource.Success(apiResponse.data)
                } else {
                    _hospitalsResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Hospitals-Req. Failed: $e")
                _hospitalsResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }


    fun getSpecialHospitals() {
        viewModelScope.launch {
            try {
                _specialHospitalsResponse.value = Resource.Loading
                val map = mutableMapOf<String, String>()
                map["featured"] = "1"
                val cityId = UserInfo.cityId
                if (cityId != 0)
                    map["city_id"] = cityId.toString()
                val apiResponse = repo.getHospitals(map)
                if (apiResponse.status) {
                    if ((apiResponse.data as MutableList<Hospital>).isEmpty())
                        _specialHospitalsResponse.value = Resource.Empty
                    else
                        _specialHospitalsResponse.value =
                            Resource.Success(apiResponse.data)
                } else {
                    _specialHospitalsResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "SpecialHospitals-Req. Failed: $e")
                _specialHospitalsResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    private fun catchNetworkException(e: Exception) = when (e) {
        is IOException -> application.getString(R.string.no_internet_connection)
        else -> application.getString(R.string.something_went_wrong_try_again_later)
    }

    fun startFiltering() {
        if (filterByLiveData.value!! == 0) {
            hospitalsQueryMap.remove("rate")
            hospitalsQueryMap["latitude"] = UserInfo.latitude
            hospitalsQueryMap["longitude"] = UserInfo.longitude
        } else {
            hospitalsQueryMap.remove("latitude")
            hospitalsQueryMap.remove("longitude")
            hospitalsQueryMap["rate"] = "1"
        }
        setStartRequestHospitals(true)
    }
}