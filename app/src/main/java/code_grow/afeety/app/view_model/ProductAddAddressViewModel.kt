package code_grow.afeety.app.view_model

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code_grow.afeety.app.R
import code_grow.afeety.app.kot_pref.UserInfo
import code_grow.afeety.app.model.Area
import code_grow.afeety.app.repository.CartRepository
import code_grow.afeety.app.retrofit.Resource
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception

private const val TAG = "ProductAddAddressViewModel"

class ProductAddAddressViewModel(
    private val application: Application,
    private val repo: CartRepository
) : ViewModel() {

    val fullAddressLiveData = MutableLiveData("")

    /* name input */
    val nameLiveData = MutableLiveData("")

    /* name error message */
    val nameErrorLiveData = MutableLiveData("")

    /* details input */
    val detailsLiveData = MutableLiveData("")

    /* details error message */
    val detailsErrorLiveData = MutableLiveData("")

    /* user area input */
    val areaLiveData = MutableLiveData("")

    /* area error message */
    val areaErrorLiveData = MutableLiveData("")

    private val _startAddAddress = MutableLiveData(false)
    val startAddAddress: LiveData<Boolean> get() = _startAddAddress

    fun startAddAddress(value: Boolean) {
        _startAddAddress.value = value
    }

    private val _addAddressResponse = MutableLiveData<Resource>(Resource.Idle)
    val addAddressResponse: LiveData<Resource> get() = _addAddressResponse

    private val _areasResponse = MutableLiveData<Resource>()
    val areasResponse: LiveData<Resource> get() = _areasResponse


    init {
        getAreas()
    }

    @SuppressLint("LongLogTag")
    fun getAreas() {
        viewModelScope.launch {
            try {
                _areasResponse.value = Resource.Loading
                val apiResponse = repo.getAreas()
                if (apiResponse.status) {
//                    if ((apiResponse.data as MutableList<City>).isEmpty())
//                        _areasResponse.value = Resource.Empty
//                    else
                    _areasResponse.value =
                        Resource.Success(apiResponse.areas)
                } else {
                    _areasResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Areas-Req. Failed: $e")
                _areasResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    fun saveBtnClicked() {
        var pass = true
        if (!validateName(nameLiveData.value!!))
            pass = false
        if (!validateDetails(detailsLiveData.value!!))
            pass = false
        if (!validateArea(areaLiveData.value!!))
            pass = false

        if (pass)
            startAddAddress(true)
    }

    @SuppressLint("LongLogTag")
    fun addAddress(latLng: LatLng) {
        val map = mutableMapOf<String, String>()
        map["user_id"] = UserInfo.userId.toString()
        map["title"] = nameLiveData.value.toString()
        map["address"] = detailsLiveData.value.toString()
        map["longitude"] = latLng.longitude.toString()
        map["latitude"] = latLng.latitude.toString()
        map["description"] = fullAddressLiveData.value.toString()
        val areas = (_areasResponse.value!! as Resource.Success<MutableList<Area>>).data
        map["area_id"] = areas.find {
            it.name == areaLiveData.value!!
        }?.areaId.toString()

        viewModelScope.launch {
            try {
                _addAddressResponse.value = Resource.Loading
                val apiResponse = repo.addAddress(map)
                if (apiResponse.status) {
                    _addAddressResponse.value = Resource.Success(apiResponse.address!!)
                } else {
                    _addAddressResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "AddAddress-Req. Failed: $e")
                _addAddressResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    private fun catchNetworkException(e: Exception) = when (e) {
        is IOException -> application.getString(R.string.no_internet_connection)
        else -> application.getString(R.string.something_went_wrong_try_again_later)
    }

    private fun validateName(name: String) = if (name.isEmpty()) {
        nameErrorLiveData.value = application.getString(R.string.empty_field_error_message)
        false
    } else {
        if (name.length < 3) {
            nameErrorLiveData.value =
                application.getString(R.string.address_name_length_error_message)
            false
        } else
            true
    }

    private fun validateDetails(details: String) = if (details.isEmpty()) {
        detailsErrorLiveData.value = application.getString(R.string.empty_field_error_message)
        false
    } else {
        if (details.length < 3) {
            detailsErrorLiveData.value =
                application.getString(R.string.address_details_length_error_message)
            false
        } else
            true
    }

    private fun validateArea(area: String) = if (area.isEmpty()) {
        areaErrorLiveData.value =
            application.getString(R.string.empty_spinner_field_error_message)
        false
    } else {
        true
    }
}