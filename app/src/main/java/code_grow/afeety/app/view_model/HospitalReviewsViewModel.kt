package code_grow.afeety.app.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code_grow.afeety.app.R
import code_grow.afeety.app.model.Review
import code_grow.afeety.app.repository.HospitalsRepository
import code_grow.afeety.app.retrofit.Resource
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception

class HospitalReviewsViewModel(
    private val hospitalId: Int,
    private val application: Application,
    private val repo: HospitalsRepository
) : ViewModel() {

    private val _reviewsResponse = MutableLiveData<Resource>(Resource.Idle)
    val reviewsResponse: LiveData<Resource> get() = _reviewsResponse

    init {
        getReviews()
    }

    fun getReviews() {
        viewModelScope.launch {
            try {
                _reviewsResponse.value = Resource.Loading
                val apiResponse = repo.getReviews(hospitalId)
                if (apiResponse.status) {
                    if ((apiResponse.data as MutableList<Review>).isEmpty())
                        _reviewsResponse.value = Resource.Empty
                    else
                        _reviewsResponse.value =
                            Resource.Success(apiResponse.data)
                } else {
                    _reviewsResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d("HReviewsViewModel", "Reviews-Req. Failed: $e")
                _reviewsResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }


    private fun catchNetworkException(e: Exception) = when (e) {
        is IOException -> application.getString(R.string.no_internet_connection)
        else -> application.getString(R.string.something_went_wrong_try_again_later)
    }
}