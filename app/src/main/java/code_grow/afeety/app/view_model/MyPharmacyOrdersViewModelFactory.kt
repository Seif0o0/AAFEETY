package code_grow.afeety.app.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import code_grow.afeety.app.repository.PharmaciesRepository

class MyPharmacyOrdersViewModelFactory(
    private val application: Application,
    private val repo: PharmaciesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyPharmacyOrdersViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MyPharmacyOrdersViewModel(application, repo) as T
        }
        throw IllegalArgumentException("Unable to construct viewModel")
    }
}
