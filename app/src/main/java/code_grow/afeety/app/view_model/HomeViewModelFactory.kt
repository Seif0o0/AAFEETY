package code_grow.afeety.app.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import code_grow.afeety.app.repository.AuthRepository
import code_grow.afeety.app.repository.HomeRepository

class HomeViewModelFactory(
    private val app: Application,
    private val repo: HomeRepository,
    private val authRepository:AuthRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(app, repo,authRepository) as T
        }
        throw IllegalArgumentException("Unable to construct viewModel")
    }
}
