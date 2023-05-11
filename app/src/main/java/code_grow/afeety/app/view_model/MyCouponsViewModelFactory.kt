package code_grow.afeety.app.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import code_grow.afeety.app.repository.CouponsRepository

class MyCouponsViewModelFactory(
    private val application: Application,
    private val couponType:Int,
    private val repo: CouponsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyCouponsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MyCouponsViewModel(application,couponType, repo) as T
        }
        throw IllegalArgumentException("Unable to construct viewModel")
    }
}