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
import code_grow.afeety.app.local_model.LocalMedicine
import code_grow.afeety.app.repository.MedicineRepository
import kotlinx.coroutines.launch

private const val TAG = "PharmacyDetailsViewModel"

class PharmacyDetailsViewModel(
    private val pharmacyId: Int,
    private val pharmacyName: String,
    private val deliveryFees: Double,
    private val application: Application,
    private val localRepository: MedicineRepository
) : ViewModel() {

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

    @SuppressLint("LongLogTag")
    fun addToCart() {

        viewModelScope.launch {
            try {
                localRepository.insert(medicine!!)
                CartInfo.pharmacyId = pharmacyId
                CartInfo.pharmacyName = pharmacyName
                CartInfo.cartStatus = 1
                Log.d(TAG, "delivery: $deliveryFees")
                CartInfo.deliveryFees = deliveryFees.toString()
                Log.d(TAG, "delivery: ${CartInfo.deliveryFees}")
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