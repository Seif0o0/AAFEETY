package code_grow.afeety.app.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import code_grow.afeety.app.repository.LocalProductRepository

class ProductCartViewModelFactory(
    private val app: Application,
    private val localRepository: LocalProductRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductCartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductCartViewModel(app, localRepository) as T
        }
        throw IllegalArgumentException("Unable to construct viewModel")
    }
}