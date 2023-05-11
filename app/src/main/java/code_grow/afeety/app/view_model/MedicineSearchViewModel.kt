package code_grow.afeety.app.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code_grow.afeety.app.R
import code_grow.afeety.app.model.MedicineCategory
import code_grow.afeety.app.repository.PharmaciesRepository
import code_grow.afeety.app.retrofit.Resource
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception

private const val TAG = "MedicineSearchViewModel"

class MedicineSearchViewModel(
    private val application: Application,
    private val repo: PharmaciesRepository
) : ViewModel() {

    /* user query input */
    val queryLiveData = MutableLiveData("")

    /* query error message */
    val queryErrorLiveData = MutableLiveData("")

    /* user categories input */
    val categoriesLiveData = MutableLiveData("")

    /* categories error message */
    val categoriesErrorLiveData = MutableLiveData("")

    private val _startRequestMedicineCategories = MutableLiveData(false)
    val startRequestMedicineCategories: LiveData<Boolean> get() = _startRequestMedicineCategories

    fun startRequestMedicineCategories(value: Boolean) {
        _startRequestMedicineCategories.value = value
    }

    private val _medicineCategoriesResponse = MutableLiveData<Resource>(Resource.Idle)
    val medicineCategoriesResponse: LiveData<Resource> get() = _medicineCategoriesResponse

    init {
        startRequestMedicineCategories(true)
    }

    fun getMedicines() {
        viewModelScope.launch {
            try {

                _medicineCategoriesResponse.value = Resource.Loading
                val apiResponse = repo.getMedicineCategories()
                if (apiResponse.status) {
                    if ((apiResponse.data as MutableList<MedicineCategory>).isEmpty())
                        _medicineCategoriesResponse.value = Resource.Empty
                    else
                        _medicineCategoriesResponse.value =
                            Resource.Success(apiResponse.data)
                } else {
                    _medicineCategoriesResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "MedicineCategories-Req. Failed: $e")
                _medicineCategoriesResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    private fun catchNetworkException(e: Exception) = when (e) {
        is IOException -> application.getString(R.string.no_internet_connection)
        else -> application.getString(R.string.something_went_wrong_try_again_later)
    }

    val startSearch = MutableLiveData(false)
    var categoryId = ""
    fun searchBtnClicked() {
        var pass = true
        if (!validateQuery(queryLiveData.value!!))
            pass = false
        if (!validateCategory(categoriesLiveData.value!!))
            pass = false

        if (pass) {
            val categories =
                (_medicineCategoriesResponse.value!! as Resource.Success<MutableList<MedicineCategory>>).data
            categoryId = categories.find {
                it.name == categoriesLiveData.value!!
            }?.categoryId.toString()

            startSearch.value = true
        }
    }

    private fun validateQuery(query: String) = if (query.isEmpty()) {
        queryErrorLiveData.value =
            application.getString(R.string.empty_field_error_message)
        false
    } else {
        true
    }

    private fun validateCategory(categories: String) = if (categories.isEmpty()) {
        categoriesErrorLiveData.value =
            application.getString(R.string.empty_spinner_field_error_message)
        false
    } else {
        true
    }


}