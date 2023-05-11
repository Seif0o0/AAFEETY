package code_grow.afeety.app.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code_grow.afeety.app.R
import code_grow.afeety.app.kot_pref.UserInfo
import code_grow.afeety.app.model.Pharmacy
import code_grow.afeety.app.repository.PharmaciesRepository
import code_grow.afeety.app.retrofit.Resource
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception

private const val TAG = "PharmaciesViewModel"

class PharmaciesViewModel(
    private val application: Application,
    private val repo: PharmaciesRepository
) : ViewModel() {
    private val _startRequestPharmacies = MutableLiveData(false)
    val startRequestPharmacies: LiveData<Boolean> get() = _startRequestPharmacies

    fun startRequestPharmacies(value: Boolean) {
        _startRequestPharmacies.value = value
    }

    private val _pharmaciesResponse = MutableLiveData<Resource>(Resource.Idle)
    val pharmaciesResponse: LiveData<Resource> get() = _pharmaciesResponse

    private val _bestPharmaciesResponse = MutableLiveData<Resource>(Resource.Idle)
    val bestPharmaciesResponse: LiveData<Resource> get() = _bestPharmaciesResponse

    val filterByLiveData = MutableLiveData(0)
    val pharmaciesQueryMap = mutableMapOf<String, String>()

    private val _startRequestSlider = MutableLiveData(false)
    val startRequestSlider: LiveData<Boolean> get() = _startRequestSlider

    private val _sliderResponse = MutableLiveData<Resource>(Resource.Idle)
    val sliderResponse: LiveData<Resource> get() = _sliderResponse

    fun startRequestSlider(value: Boolean) {
        _startRequestSlider.value = value
    }

    init {
        startRequestSlider(true)
        if (UserInfo.userId != 0){
            pharmaciesQueryMap["latitude"] = UserInfo.latitude
            pharmaciesQueryMap["longitude"] = UserInfo.longitude
            pharmaciesQueryMap["city_id"] = UserInfo.cityId.toString()
        }
        startRequestPharmacies(true)
        getPharmacies()
        getBestPharmacies()
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

    fun getPharmacies() {
        viewModelScope.launch {
            try {
                _pharmaciesResponse.value = Resource.Loading
                val apiResponse = repo.getPharmacies(pharmaciesQueryMap)
                if (apiResponse.status) {
                    if ((apiResponse.data as MutableList<Pharmacy>).isEmpty())
                        _pharmaciesResponse.value = Resource.Empty
                    else
                        _pharmaciesResponse.value =
                            Resource.Success(apiResponse.data)
                } else {
                    _pharmaciesResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Pharmacies-Req. Failed: $e")
                _pharmaciesResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    fun getBestPharmacies() {
        _bestPharmaciesResponse.value = Resource.Empty
        viewModelScope.launch {
            try {
                val map = mutableMapOf<String, String>()
                map["featured"] = "1"
                val cityId = UserInfo.cityId
                if (cityId != 0)
                    map["city_id"] = cityId.toString()

                _bestPharmaciesResponse.value = Resource.Loading
                val apiResponse = repo.getPharmacies(map)
                if (apiResponse.status) {
                    if ((apiResponse.data as MutableList<Pharmacy>).isEmpty())
                        _bestPharmaciesResponse.value = Resource.Empty
                    else
                        _bestPharmaciesResponse.value =
                            Resource.Success(apiResponse.data)
                } else {
                    _bestPharmaciesResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "BestPharmacies-Req. Failed: $e")
                _bestPharmaciesResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    private fun catchNetworkException(e: Exception) = when (e) {
        is IOException -> application.getString(R.string.no_internet_connection)
        else -> application.getString(R.string.something_went_wrong_try_again_later)
    }

    fun startFiltering() {
        if (filterByLiveData.value!! == 0) {
            pharmaciesQueryMap.remove("rate")
            pharmaciesQueryMap["latitude"] = UserInfo.latitude
            pharmaciesQueryMap["longitude"] = UserInfo.longitude
        } else {
            pharmaciesQueryMap.remove("latitude")
            pharmaciesQueryMap.remove("longitude")
            pharmaciesQueryMap["rate"] = "1"
        }
        startRequestPharmacies(true)
    }
}