package code_grow.afeety.app.view_model

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code_grow.afeety.app.R
import code_grow.afeety.app.model.MedicineOrder
import code_grow.afeety.app.repository.PharmaciesRepository
import code_grow.afeety.app.retrofit.Resource
import kotlinx.coroutines.launch
import java.io.IOException

private const val TAG = "MyPharmacyOrdersViewModel"

class MyPharmacyOrdersViewModel(
    private val application: Application,
    private val repo: PharmaciesRepository
) : ViewModel() {
    private val _startRequestOrders = MutableLiveData(false)
    val startRequestOrders: LiveData<Boolean> get() = _startRequestOrders

    fun startRequestOrders(value: Boolean) {
        _startRequestOrders.value = value
    }

    private val _ordersResponse = MutableLiveData<Resource>(Resource.Idle)
    val ordersResponse: LiveData<Resource> get() = _ordersResponse

    init {
        startRequestOrders(true)
    }

    @SuppressLint("LongLogTag")
    fun getOrders() {
        viewModelScope.launch {
            try {
                _ordersResponse.value = Resource.Loading
                val apiResponse = repo.getOrders()
                if (apiResponse.status) {
                    if ((apiResponse.data as MutableList<MedicineOrder>).isEmpty())
                        _ordersResponse.value = Resource.Empty
                    else
                        _ordersResponse.value =
                            Resource.Success(apiResponse.data)
                } else {
                    _ordersResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Orders-Req. Failed: $e")
                _ordersResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    private fun catchNetworkException(e: java.lang.Exception) = when (e) {
        is IOException -> application.getString(R.string.no_internet_connection)
        else -> application.getString(R.string.something_went_wrong_try_again_later)
    }


}