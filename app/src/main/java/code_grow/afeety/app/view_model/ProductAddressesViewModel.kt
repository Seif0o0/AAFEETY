package code_grow.afeety.app.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code_grow.afeety.app.R
import code_grow.afeety.app.model.Address
import code_grow.afeety.app.repository.CartRepository
import code_grow.afeety.app.retrofit.Resource
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception

private const val TAG = "ProductAddressesViewModel"
class ProductAddressesViewModel (
    private val application: Application,
    private val repo: CartRepository
) : ViewModel() {
    private val _startRequestAddresses = MutableLiveData(false)
    val startRequestAddresses: LiveData<Boolean> get() = _startRequestAddresses

    private val _addressesResponse = MutableLiveData<Resource>(Resource.Idle)
    val addressesResponse: LiveData<Resource> get() = _addressesResponse


    fun startRequestAddresses(value: Boolean) {
        _startRequestAddresses.value = value
    }

    init {
        startRequestAddresses(true)
    }

    fun getAddresses() {
        viewModelScope.launch {
            try {
                _addressesResponse.value = Resource.Loading
                val apiResponse = repo.getAddresses()
                if (apiResponse.status) {
                    if ((apiResponse.data as MutableList<Address>).isEmpty())
                        _addressesResponse.value = Resource.Empty
                    else
                        _addressesResponse.value =
                            Resource.Success(apiResponse.data)
                } else {
                    _addressesResponse.value = Resource.Failed(apiResponse.message ?: "Error")
                }
            } catch (e: Exception) {
                Log.d(TAG, "Addresses-Req. Failed: $e")
                _addressesResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    private fun catchNetworkException(e: Exception) = when (e) {
        is IOException -> application.getString(R.string.no_internet_connection)
        else -> application.getString(R.string.something_went_wrong_try_again_later)
    }
}