package code_grow.afeety.app.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import code_grow.afeety.app.repository.LabsRepository

class MyLabReservationsViewModelFactory(
    private val app: Application,
    private val repo: LabsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyLabReservationsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MyLabReservationsViewModel(app, repo) as T
        }
        throw IllegalArgumentException("Unable to construct viewModel")
    }
}
