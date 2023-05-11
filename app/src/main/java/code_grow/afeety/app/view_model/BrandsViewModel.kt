package code_grow.afeety.app.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code_grow.afeety.app.R
import code_grow.afeety.app.model.Brand
import code_grow.afeety.app.repository.ProductRepository
import code_grow.afeety.app.retrofit.Resource
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception

private const val TAG = "BrandsViewModel"

class BrandsViewModel(
    private val application: Application,
    private val repo: ProductRepository
) : ViewModel() {

    private val _startRequestBrands = MutableLiveData(false)
    val startRequestBrands: LiveData<Boolean> get() = _startRequestBrands

    private val _brandsResponse = MutableLiveData<Resource>(Resource.Idle)
    val brandsResponse: LiveData<Resource> get() = _brandsResponse

    fun startRequestBrands(value: Boolean) {
        _startRequestBrands.value = value
    }

    val query = mutableMapOf<String, String>()

    init {
        startRequestBrands(true)
    }

    fun getBrands() {
        viewModelScope.launch {
            try {
                _brandsResponse.value = Resource.Loading
                val apiResponse = repo.fetchBrands(query)
                if (apiResponse.status) {
                    if ((apiResponse.data as MutableList<Brand>).isEmpty())
                        _brandsResponse.value = Resource.Empty
                    else
                        _brandsResponse.value =
                            Resource.Success(apiResponse.data)
                } else {
                    _brandsResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Brands-Req. Failed: $e")
                _brandsResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    private fun catchNetworkException(e: Exception) = when (e) {
        is IOException -> application.getString(R.string.no_internet_connection)
        else -> application.getString(R.string.something_went_wrong_try_again_later)
    }

    fun startSearch(search: String) {
        query["search"] = search
        startRequestBrands(true)
    }

    fun changeResponseToIdle() {
        _brandsResponse.value = Resource.Idle
    }
}