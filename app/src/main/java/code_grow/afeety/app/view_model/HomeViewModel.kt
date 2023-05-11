package code_grow.afeety.app.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code_grow.afeety.app.R
import code_grow.afeety.app.kot_pref.UserInfo
import code_grow.afeety.app.model.LoginUser
import code_grow.afeety.app.repository.AuthRepository
import code_grow.afeety.app.repository.HomeRepository
import code_grow.afeety.app.retrofit.Resource
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception

private const val TAG = "HomeViewModel"

class HomeViewModel(
    val application: Application,
    val repo: HomeRepository,
    val authRepository: AuthRepository
) : ViewModel() {
    private val _startRequestHomeData = MutableLiveData(false)
    val startRequestHomeData: LiveData<Boolean> get() = _startRequestHomeData

    private val _homeResponse = MutableLiveData<Resource>(Resource.Idle)
    val homeResponse: LiveData<Resource> get() = _homeResponse

    private val _startRequestHomeSlider = MutableLiveData(false)
    val startRequestHomeSlider: LiveData<Boolean> get() = _startRequestHomeSlider

    private val _sliderResponse = MutableLiveData<Resource>(Resource.Idle)
    val sliderResponse: LiveData<Resource> get() = _sliderResponse

    fun setStartRequestHomeSlider(value: Boolean) {
        _startRequestHomeSlider.value = value
    }

    fun setStartRequestHomeData(value: Boolean) {
        _startRequestHomeData.value = value
    }

    private val _startUpdateToken = MutableLiveData(false)
    val startUpdateToken: LiveData<Boolean> get() = _startUpdateToken

    fun startUpdateToken(value: Boolean) {
        _startUpdateToken.value = value
    }

    private val _updateTokenResponse = MutableLiveData<Resource>(Resource.Idle)
    val updateTokenResponse: LiveData<Resource> get() = _updateTokenResponse


    init {
        _startRequestHomeData.value = true
        _startRequestHomeSlider.value = true
    }

    fun getHomeData() {
        viewModelScope.launch {
            try {
                _homeResponse.value = Resource.Loading
                val apiResponse = repo.getHomeData()
                if (apiResponse.status) {
                    _homeResponse.value =
                        Resource.Success(apiResponse.data)
                } else {
                    _homeResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "HomeData-Req. Failed: $e")
                _homeResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    fun getHomeSlider() {
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

    fun updateToken() {
        viewModelScope.launch {
            try {
                _updateTokenResponse.value = Resource.Loading
                val apiResponse = authRepository.updateToken(
                    mutableMapOf(
                        "id" to UserInfo.userId.toString(),
                        "token" to UserInfo.firebaseToken
                    )
                )
                if (apiResponse.status) {
                    _updateTokenResponse.value = Resource.Success(apiResponse.user as LoginUser)
                } else {
                    _updateTokenResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "UpdateToken-Req. Failed: $e")
//                _updateTokenResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    private fun catchNetworkException(e: Exception) = when (e) {
        is IOException -> application.getString(R.string.no_internet_connection)
        else -> application.getString(R.string.something_went_wrong_try_again_later)
    }
}