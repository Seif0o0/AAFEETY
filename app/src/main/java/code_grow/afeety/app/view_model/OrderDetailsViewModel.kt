package code_grow.afeety.app.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code_grow.afeety.app.R
import code_grow.afeety.app.kot_pref.CartInfo
import code_grow.afeety.app.kot_pref.UserInfo
import code_grow.afeety.app.local_model.LocalMedicine
import code_grow.afeety.app.repository.CartRepository
import code_grow.afeety.app.repository.MedicineRepository
import code_grow.afeety.app.retrofit.Resource
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception

private const val TAG = "OrderDetailsViewModel"

class OrderDetailsViewModel(
    private val medicines: MutableList<LocalMedicine>,
    private val addressId: Int,
    private val application: Application,
    private val repo: CartRepository,
    private val localRepository: MedicineRepository
) : ViewModel() {
    private val _startBuying = MutableLiveData(false)
    val startBuying: LiveData<Boolean> get() = _startBuying

    fun startBuying(value: Boolean) {
        _startBuying.value = value
    }

    private val _buyingResponse = MutableLiveData<Resource>(Resource.Idle)
    val buyingResponse: LiveData<Resource> get() = _buyingResponse

    fun buy() {
        val map = mutableMapOf<String, String>()
        map["pharmacy_id"] = CartInfo.pharmacyId.toString()
        map["user_id"] = UserInfo.userId.toString()
        map["address_id"] = addressId.toString()
        for (index in 0 until medicines.size) {
            map["drug_id[$index]"] = medicines[index].medicineId.toString()
            map["drug_pharmacy_id[$index]"] = medicines[index].id.toString()
            map["price[$index]"] = medicines[index].price.toString()
        }

        viewModelScope.launch {
            try {
                _buyingResponse.value = Resource.Loading
                val apiResponse = repo.buyMedicine(map)
                if (apiResponse.status) {
                    _buyingResponse.value = Resource.Success("")

                    CartInfo.cartStatus = 0
                    CartInfo.pharmacyId = 0
                    CartInfo.pharmacyName = ""
                    viewModelScope.launch {
                        localRepository.removeAllMedicines()
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