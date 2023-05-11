package code_grow.afeety.app.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import code_grow.afeety.app.repository.LocalProductRepository
import code_grow.afeety.app.repository.ProductRepository

class FamousViewModelFactory(
    private val application: Application,
    private val repo: ProductRepository,
    private val localRepo : LocalProductRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FamousViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FamousViewModel(application, repo,localRepo) as T
        }
        throw IllegalArgumentException("Unable to construct viewModel")
    }
}
