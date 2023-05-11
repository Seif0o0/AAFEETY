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

private const val TAG = "BookCouponViewModel"

class BookCouponViewModel(
    private val application: Application,
    private val coupon: Coupon,
    private val repo: CouponsRepository
) : ViewModel() {

    private val _startBookCoupon = MutableLiveData(false)
    val startBookCoupon: LiveData<Boolean> get() = _startBookCoupon

    private val _bookingResponse = MutableLiveData<Resource>(Resource.Idle)
    val bookingResponse: LiveData<Resource> get() = _bookingResponse

    fun startBookCoupon(value: Boolean) {
        _startBookCoupon.value = value
    }

    fun bookCoupon() {
        val map = mutableMapOf<String, String>()
        map["user_id"] = UserInfo.userId.toString()
        map["provider_id"] = coupon.providerId.toString()
        map["coupon_id"] = coupon.couponId.toString()


        viewModelScope.launch {
            try {
                _bookingResponse.value = Resource.Loading
                val apiResponse = repo.bookCoupon(map)
                if (apiResponse.status) {
                    _bookingResponse.value = Resource.Success(apiResponse.message)
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


}