package code_grow.afeety.app.view_model

import android.app.Application
import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code_grow.afeety.app.R
import code_grow.afeety.app.kot_pref.CartInfo
import code_grow.afeety.app.local_model.LocalMedicine
import code_grow.afeety.app.model.Medicine
import code_grow.afeety.app.repository.MedicineRepository
import code_grow.afeety.app.repository.PharmaciesRepository
import code_grow.afeety.app.retrofit.Resource
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception

private const val TAG = "MedicinesViewModel"

class MedicinesViewModel(
    private val pharmacyId: Int,
    private val pharmacyName: String,
    private val deliveryFees: Double,
    private val query: String,
    private val categoryId: String,
    private val application: Application,
    private val repo: PharmaciesRepository,
    private val localRepository: MedicineRepository
) : ViewModel() {
    private val _startRequestMedicines = MutableLiveData(false)
    val startRequestMedicines: LiveData<Boolean> get() = _startRequestMedicines

    fun startRequestMedicines(value: Boolean) {
        _startRequestMedicines.value = value
    }

    private val _medicinesResponse = MutableLiveData<Resource>(Resource.Idle)
    val medicinesResponse: LiveData<Resource> get() = _medicinesResponse

    init {
        startRequestMedicines(true)
    }

    fun getMedicines() {
        viewModelScope.launch {
            try {
                val map = mutableMapOf<String, String>()
                map["pharmacy_id"] = pharmacyId.toString()
                map["search"] = query
                map["drug_category"] = categoryId

                _medicinesResponse.value = Resource.Loading
                val apiResponse = repo.getMedicines(map)
                if (apiResponse.status) {
                    if ((apiResponse.data as MutableList<Medicine>).isEmpty())
                        _medicinesResponse.value = Resource.Empty
                    else
                        _medicinesResponse.value =
                            Resource.Success(apiResponse.data)
                } else {
                    _medicinesResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Pharmacies-Req. Failed: $e")
                _medicinesResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    private fun catchNetworkException(e: Exception) = when (e) {
        is IOException -> application.getString(R.string.no_internet_connection)
        else -> application.getString(R.string.something_went_wrong_try_again_later)
    }

    var errorMessage = ""

    private val _startAddToCart = MutableLiveData(false)
    val startAddToCart: LiveData<Boolean> get() = _startAddToCart

    private var medicine: LocalMedicine? = null
    fun startAddToCart(
        value: Boolean,
        medicine: LocalMedicine?
    ) {
        this.medicine = medicine
        _startAddToCart.value = value
    }

    // 0-> Idle, 1-> Success, 2-> Failed
    private val _addToCartResponse = MutableLiveData(0)
    val addToCartResponse: LiveData<Int> get() = _addToCartResponse


    fun addToCart() {
        viewModelScope.launch {
            try {
                localRepository.insert(medicine!!)
                CartInfo.pharmacyId = pharmacyId
                CartInfo.pharmacyName = pharmacyName
                CartInfo.cartStatus = 1
                CartInfo.deliveryFees = deliveryFees.toString()
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