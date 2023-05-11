package code_grow.afeety.app.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import code_grow.afeety.app.model.Coupon
import code_grow.afeety.app.repository.CouponsRepository

class BookCouponViewModelFactory(
    private val application: Application,
    private val coupon: Coupon,
    private val repo: CouponsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookCouponViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BookCouponViewModel(application, coupon, repo) as T
        }
        throw IllegalArgumentException("Unable to construct viewModel")
    }
}
