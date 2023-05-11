package code_grow.afeety.app.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import code_grow.afeety.app.repository.LocalProductRepository
import code_grow.afeety.app.repository.ProductRepository

class ProductsViewModelFactory(
    private val application: Application,
    private val repo: ProductRepository,
    private val localRepo:LocalProductRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductsViewModel(application, repo,localRepo) as T
        }
        throw IllegalArgumentException("Unable to construct viewModel")
    }
}