package code_grow.afeety.app.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import code_grow.afeety.app.repository.LocalProductRepository
import code_grow.afeety.app.repository.ProductRepository

class FamousInfoViewModelFactory(
    private val application: Application,
    private val influencerId: Int,
    private val repo: ProductRepository,
    private val localRepo: LocalProductRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FamousInfoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FamousInfoViewModel(application, influencerId, repo, localRepo) as T
        }
        throw IllegalArgumentException("Unable to construct viewModel")
    }
}