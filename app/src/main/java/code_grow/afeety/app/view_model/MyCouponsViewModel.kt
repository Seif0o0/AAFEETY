package code_grow.afeety.app.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code_grow.afeety.app.R
import code_grow.afeety.app.kot_pref.UserInfo
import code_grow.afeety.app.model.Coupon
import code_grow.afeety.app.repository.CouponsRepository
import code_grow.afeety.app.retrofit.Resource
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception

private const val TAG = "MyCouponsViewModel"

class MyCouponsViewModel(
    private val application: Application,
    private val couponType:Int,
    private val repo: CouponsRepository
) : ViewModel() {

    private val _startRequestCoupons = MutableLiveData(false)
    val startRequestCoupons: LiveData<Boolean> get() = _startRequestCoupons

    private val _couponsResponse = MutableLiveData<Resource>(Resource.Idle)
    val couponsResponse: LiveData<Resource> get() = _couponsResponse

    fun startRequestCoupons(value: Boolean) {
        _startRequestCoupons.value = value
    }

    val query = mutableMapOf<String, String>()

    init {
        query["type"] = couponType.toString()
        query["user_id"] = UserInfo.userId.toString()
        startRequestCoupons(true)
    }

    fun getCoupons() {
        viewModelScope.launch {
            try {
                _couponsResponse.value = Resource.Loading
                val apiResponse = repo.getMyCoupons(query)
                if (apiResponse.status) {
                    if ((apiResponse.data as MutableList<Coupon>).isEmpty())
                        _couponsResponse.value = Resource.Empty
                    else
                        _couponsResponse.value =
                            Resource.Success(apiResponse.data)
                } else {
                    _couponsResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "MyCoupons-Req. Failed: $e")
                _couponsResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    private fun catchNetworkException(e: Exception) = when (e) {
        is IOException -> application.getString(R.string.no_internet_connection)
        else -> application.getString(R.string.something_went_wrong_try_again_later)
    }


}