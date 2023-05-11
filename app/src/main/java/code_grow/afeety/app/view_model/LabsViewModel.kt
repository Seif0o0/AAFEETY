package code_grow.afeety.app.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code_grow.afeety.app.R
import code_grow.afeety.app.kot_pref.UserInfo
import code_grow.afeety.app.model.Lab
import code_grow.afeety.app.repository.LabsRepository
import code_grow.afeety.app.retrofit.Resource
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception

private const val TAG = "LabsViewModel"

class LabsViewModel(
    private val application: Application,
    private val repo: LabsRepository
) : ViewModel() {

    private val _startRequestLabs = MutableLiveData(false)
    val startRequestLabs: LiveData<Boolean> get() = _startRequestLabs

    fun setStartRequestLabs(value: Boolean) {
        _startRequestLabs.value = value
    }

    val filterByLiveData = MutableLiveData(0)
    val labsQueryMap = mutableMapOf<String, String>()

    private val _labsResponse = MutableLiveData<Resource>(Resource.Idle)
    val labsResponse: LiveData<Resource> get() = _labsResponse

    private val _bestLabsResponse = MutableLiveData<Resource>(Resource.Idle)
    val bestLabsResponse: LiveData<Resource> get() = _bestLabsResponse

    private val _startRequestSlider = MutableLiveData(false)
    val startRequestSlider: LiveData<Boolean> get() = _startRequestSlider

    private val _sliderResponse = MutableLiveData<Resource>(Resource.Idle)
    val sliderResponse: LiveData<Resource> get() = _sliderResponse

    fun setStartRequestSlider(value: Boolean) {
        _startRequestSlider.value = value
    }

    init {
        setStartRequestSlider(true)
        val cityId = UserInfo.cityId
        if (cityId != 0) {
            labsQueryMap["latitude"] = UserInfo.latitude
            labsQueryMap["longitude"] = UserInfo.longitude
            labsQueryMap["city_id"] = cityId.toString()
        }

        setStartRequestLabs(true) // start calling labs request.
        getBestLabs()
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

    fun getLabs() {
        viewModelScope.launch {
            try {
                _labsResponse.value = Resource.Loading
                val apiResponse = repo.getLabs(labsQueryMap)
                if (apiResponse.status) {
                    if ((apiResponse.data as MutableList<Lab>).isEmpty())
                        _labsResponse.value = Resource.Empty
                    else
                        _labsResponse.value =
                            Resource.Success(apiResponse.data)
                } else {
                    _labsResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Labs-Req. Failed: $e")
                _labsResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    fun getBestLabs() {
        _bestLabsResponse.value = Resource.Empty
        viewModelScope.launch {
            try {
                val map = mutableMapOf<String, String>()
                map["featured"] = "1"
                val cityId = UserInfo.cityId
                if (cityId != 0)
                    map["city_id"] = cityId.toString()

                _bestLabsResponse.value = Resource.Loading
                val apiResponse = repo.getLabs(map)
                if (apiResponse.status) {
                    if ((apiResponse.data as MutableList<Lab>).isEmpty())
                        _bestLabsResponse.value = Resource.Empty
                    else
                        _bestLabsResponse.value =
                            Resource.Success(apiResponse.data)
                } else {
                    _bestLabsResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "BestLabs-Req. Failed: $e")
                _bestLabsResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    private fun catchNetworkException(e: Exception) = when (e) {
        is IOException -> application.getString(R.string.no_internet_connection)
        else -> application.getString(R.string.something_went_wrong_try_again_later)
    }

    fun startFiltering() {
        if (filterByLiveData.value!! == 0) {
            labsQueryMap.remove("rate")
            labsQueryMap["latitude"] = UserInfo.latitude
            labsQueryMap["longitude"] = UserInfo.longitude
        } else {
            labsQueryMap.remove("latitude")
            labsQueryMap.remove("longitude")
            labsQueryMap["rate"] = "1"
        }
        setStartRequestLabs(true)
    }
}