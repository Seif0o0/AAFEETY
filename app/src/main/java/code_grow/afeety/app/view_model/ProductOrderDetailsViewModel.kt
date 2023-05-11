package code_grow.afeety.app.view_model

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code_grow.afeety.app.R
import code_grow.afeety.app.kot_pref.CartInfo
import code_grow.afeety.app.kot_pref.UserInfo
import code_grow.afeety.app.local_model.LocalProduct
import code_grow.afeety.app.repository.CartRepository
import code_grow.afeety.app.repository.LocalProductRepository
import code_grow.afeety.app.retrofit.Resource
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception

private const val TAG = "ProductOrderDetailsViewModel"

class ProductOrderDetailsViewModel(
    private val products: MutableList<LocalProduct>,
    private val addressId: Int,
    private val deliveryFees:String,
    private val application: Application,
    private val repo: CartRepository,
    private val localRepository: LocalProductRepository
) : ViewModel() {
    private val _startBuying = MutableLiveData(false)
    val startBuying: LiveData<Boolean> get() = _startBuying

    fun startBuying(value: Boolean) {
        _startBuying.value = value
    }

    private val _buyingResponse = MutableLiveData<Resource>(Resource.Idle)
    val buyingResponse: LiveData<Resource> get() = _buyingResponse


    //https://www.imextrading-co.com/aafety/productsapi/
    // make_request?
    // user_id=22&
    // address_id=28&
    // product_id%5B0%5D=1  price%5B0%5D=21.0 invoice_type%5B0%5D=1
    // product_id%5B1%5D=3  price%5B1%5D=10.0 invoice_type%5B1%5D=1

    @SuppressLint("LongLogTag")
    fun buy() {
        val map = mutableMapOf<String, String>()
        map["user_id"] = UserInfo.userId.toString()
        map["address_id"] = addressId.toString()
        map["delivery_price"] = deliveryFees.toString()
        for (index in 0 until products.size) {
            map["product_id[$index]"] = products[index].productId.toString()
            map["price[$index]"] = products[index].price.toString()
            if (products[index].famousId == 0) {
                map["type[$index]"] = "1"
            } else {
                map["influncer_id[$index]"] = products[index].famousId.toString()
                map["type[$index]"] = "2"
            }
        }

        viewModelScope.launch {
            try {
                _buyingResponse.value = Resource.Loading
                val apiResponse = repo.buyProduct(map)
                if (apiResponse.status) {
                    _buyingResponse.value = Resource.Success("")

                    CartInfo.cartStatus = 0
                    CartInfo.pharmacyId = 0
                    CartInfo.pharmacyName = ""
                    viewModelScope.launch {
                        localRepository.removeAllProducts()
                    }

                } else {
                    _buyingResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Buy-Req. Failed: $e")
                _buyingResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    private fun catchNetworkException(e: Exception) = when (e) {
        is IOException -> application.getString(R.string.no_internet_connection)
        else -> application.getString(R.string.something_went_wrong_try_again_later)
    }
}