package code_grow.afeety.app.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import code_grow.afeety.app.repository.CartRepository

class PreOrderDetailsViewModelFactory(
    private val prescriptionUri: String,
    private val addressId: Int,
    private val pharmacyId:Int,
    private val application: Application,
    private val repo: CartRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PreOrderDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PreOrderDetailsViewModel(
                prescriptionUri,
                addressId,
                pharmacyId,
                application,
                repo
            ) as T
        }
        throw IllegalArgumentException("Unable to construct viewModel")
    }
}