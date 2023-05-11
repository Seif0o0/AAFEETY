package code_grow.afeety.app.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import code_grow.afeety.app.repository.HospitalsRepository

class HospitalDetailsViewModelFactory(
    private val hospitalId: Int,
    private val app: Application,
    private val repo: HospitalsRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HospitalDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HospitalDetailsViewModel(hospitalId, app, repo) as T
        }
        throw IllegalArgumentException("Unable to construct viewModel")
    }
}
