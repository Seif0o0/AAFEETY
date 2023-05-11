package code_grow.afeety.app.view_model

import android.annotation.SuppressLint
import android.app.Application
import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code_grow.afeety.app.R
import code_grow.afeety.app.kot_pref.CartInfo
import code_grow.afeety.app.local_model.LocalProduct
import code_grow.afeety.app.model.Famous
import code_grow.afeety.app.model.Product
import code_grow.afeety.app.repository.LocalProductRepository
import code_grow.afeety.app.repository.ProductRepository
import code_grow.afeety.app.retrofit.Resource
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception

private const val TAG = "FamousViewModel"

class FamousViewModel(
    private val application: Application,
    private val repo: ProductRepository,
    private val localRepo : LocalProductRepository
) : ViewModel() {
    private val _startRequestFamous = MutableLiveData(false)
    val startRequestFamous: LiveData<Boolean> get() = _startRequestFamous

    private val _famousResponse = MutableLiveData<Resource>(Resource.Idle)
    val famousResponse: LiveData<Resource> get() = _famousResponse

    fun startRequestFamous(value: Boolean) {
        _startRequestFamous.value = value
    }

    val query = mutableMapOf<String, String>()


    var errorMessage = ""

    private val _startAddToCart = MutableLiveData(false)
    val startAddToCart: LiveData<Boolean> get() = _startAddToCart

    private val _startRequestProducts = MutableLiveData(false)
    val startRequestProducts: LiveData<Boolean> get() = _startRequestProducts

    private val _productsResponse = MutableLiveData<Resource>(Resource.Idle)
    val productsResponse: LiveData<Resource> get() = _productsResponse

    fun startRequestProducts(value: Boolean) {
        _startRequestProducts.value = value
    }

    fun updateProductsToIdle() {
        _productsResponse.value = Resource.Idle
    }


    init {
        query["gender"] = "1"
        startRequestFamous(true)
        startRequestProducts(true)
    }

    fun getFamousList() {
        viewModelScope.launch {
            try {
                _famousResponse.value = Resource.Loading
                val apiResponse = repo.fetchFamousList(query)
                if (apiResponse.status) {
                    if ((apiResponse.data as MutableList<Famous>).isEmpty())
                        _famousResponse.value = Resource.Empty
                    else
                        _famousResponse.value =
                            Resource.Success(apiResponse.data)
                } else {
                    _famousResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Famous-Req. Failed: $e")
                _famousResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    fun getProducts() {
        viewModelScope.launch {
            try {
                _productsResponse.value = Resource.Loading
                val apiResponse =
                    repo.fetchProducts(mutableMapOf("gender" to query["gender"]!!))
                if (apiResponse.status) {
                    if ((apiResponse.data as MutableList<Product>).isEmpty())
                        _productsResponse.value = Resource.Empty
                    else
                        _productsResponse.value =
                            Resource.Success(apiResponse.data)
                } else {
                    _productsResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Products-Req. Failed: $e")
                _productsResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    private fun catchNetworkException(e: Exception) = when (e) {
        is IOException -> application.getString(R.string.no_internet_connection)
        else -> application.getString(R.string.something_went_wrong_try_again_later)
    }

    fun startSearch(search: String) {
        query["search"] = search
        startRequestFamous(true)
    }

    fun changeResponseToIdle() {
        _famousResponse.value = Resource.Idle
    }

    /* add to cart process */
    private var product: LocalProduct? = null
    fun startAddToCart(
        value: Boolean,
        product: LocalProduct?
    ) {
        this.product = product
        _startAddToCart.value = value
    }

    // 0-> Idle, 1-> Success, 2-> Failed
    private val _addToCartResponse = MutableLiveData(0)
    val addToCartResponse: LiveData<Int> get() = _addToCartResponse

    fun convertAddToCartToIdle(){
        _addToCartResponse.value = 0
    }
    @SuppressLint("LongLogTag")
    fun addToCart() {

        viewModelScope.launch {
            try {
                localRepo.insert(product!!)
                CartInfo.cartStatus = 3
                CartInfo.deliveryFees = "0" //TODO(2) check delivery fees
                _addToCartResponse.value = 1
            } catch (e: Exception) {
                Log.d(TAG, "AddToCart-Failed: ${e.message}")
                errorMessage = if (e is SQLiteConstraintException)
                    application.getString(R.string.item_already_added) else application.getString(R.string.add_to_cart_failed_message)
                _addToCartResponse.value = 2
            }
        }
    }
}