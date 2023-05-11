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
import code_grow.afeety.app.model.Product
import code_grow.afeety.app.model.ProductCategory
import code_grow.afeety.app.repository.LocalProductRepository
import code_grow.afeety.app.repository.ProductRepository
import code_grow.afeety.app.retrofit.Resource
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception

private const val TAG = "ProductsViewModel"

class ProductsViewModel(
    private val application: Application,
    private val repo: ProductRepository,
    private val localRepo: LocalProductRepository
) : ViewModel() {

    var errorMessage = ""

    private val _startAddToCart = MutableLiveData(false)
    val startAddToCart: LiveData<Boolean> get() = _startAddToCart

    private val _startRequestProducts = MutableLiveData(false)
    val startRequestProducts: LiveData<Boolean> get() = _startRequestProducts

    private val _productsResponse = MutableLiveData<Resource>(Resource.Idle)
    val productsResponse: LiveData<Resource> get() = _productsResponse

    private val productsQueryMap = mutableMapOf<String, String>()

    fun startRequestProducts(value: Boolean) {
        _startRequestProducts.value = value
    }

    private val _startRequestCategories = MutableLiveData(false)
    val startRequestCategories: LiveData<Boolean> get() = _startRequestCategories

    private val _categoriesResponse = MutableLiveData<Resource>(Resource.Idle)
    val categoriesResponse: LiveData<Resource> get() = _categoriesResponse

    fun startRequestCategories(value: Boolean) {
        _startRequestCategories.value = value
    }

    private val _startRequestSlider = MutableLiveData(false)
    val startRequestSlider: LiveData<Boolean> get() = _startRequestSlider

    private val _sliderResponse = MutableLiveData<Resource>(Resource.Idle)
    val sliderResponse: LiveData<Resource> get() = _sliderResponse

    fun setStartRequestSlider(value: Boolean) {
        _startRequestSlider.value = value
    }

    fun updateCategoriesToIdle() {
        _categoriesResponse.value = Resource.Idle
    }

    fun updateSlidersToIdle() {
        _sliderResponse.value = Resource.Idle
    }

    init {
        productsQueryMap["type"] = "1"
        startRequestCategories(true)
        setStartRequestSlider(true)
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

    fun getCategories() {
        viewModelScope.launch {
            try {
                _categoriesResponse.value = Resource.Loading
                val apiResponse = repo.fetchCategories()
                if (apiResponse.status) {
                    val data = apiResponse.data as MutableList<ProductCategory>
                    data.add(
                        0,
                        ProductCategory(
                            0,
                            application.getString(R.string.all),
                            R.drawable.ic_all_specialities.toString(),
                            0, 0,
                            true
                        )
                    )
                    _categoriesResponse.value = Resource.Success(data)
                } else {
                    _categoriesResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Categories-Req. Failed: $e")
                _categoriesResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    fun getProducts() {
        viewModelScope.launch {
            try {
                _productsResponse.value = Resource.Loading
                val apiResponse = repo.fetchAllProducts(productsQueryMap)
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

    fun updateProductsQueryMap(categoryId: Int, query: String) {
        _productsResponse.value = Resource.Success(mutableListOf<Product>())
        productsQueryMap.clear()
        productsQueryMap["type"] = "1"
        if (categoryId != 0)
            productsQueryMap["category_id"] = categoryId.toString()
        if (query.isNotEmpty())
            productsQueryMap["search"] = query

        startRequestProducts(true)
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

    @SuppressLint("LongLogTag")
    fun addToCart() {

        viewModelScope.launch {
            try {
                localRepo.insert(product!!)
                CartInfo.cartStatus = 2
                CartInfo.deliveryFees = "0" //TODO(1) check delivery fees
                _addToCartResponse.value = 1
            } catch (e: Exception) {
                Log.d(TAG, "AddToCart-Failed: ${e.message}")
                errorMessage = if (e is SQLiteConstraintException)
                    application.getString(R.string.item_already_added) else application.getString(R.string.add_to_cart_failed_message)
                _addToCartResponse.value = 2
            }
        }
    }

    fun updateProductsToIdle() {
        _productsResponse.value = Resource.Idle
    }
}