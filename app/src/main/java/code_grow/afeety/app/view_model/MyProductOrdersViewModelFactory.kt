package code_grow.afeety.app.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import code_grow.afeety.app.repository.ProductRepository

class MyProductOrdersViewModelFactory(
    private val application: Application,
    private val isMedicalProducts: Int,
    private val repo: ProductRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyProductOrdersViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MyProductOrdersViewModel(application, isMedicalProducts, repo) as T
        }
        throw IllegalArgumentException("Unable to construct viewModel")
    }
}