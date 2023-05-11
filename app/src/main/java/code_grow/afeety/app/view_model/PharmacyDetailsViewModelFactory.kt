package code_grow.afeety.app.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import code_grow.afeety.app.repository.MedicineRepository

class PharmacyDetailsViewModelFactory(
    private val pharmacyId: Int,
    private val pharmacyName: String,
    private val deliveryFees : Double,
    private val application: Application,
    private val localRepository: MedicineRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PharmacyDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PharmacyDetailsViewModel(
                pharmacyId,
                pharmacyName,
                deliveryFees,
                application,
                localRepository
            ) as T
        }
        throw IllegalArgumentException("Unable to construct viewModel")
    }
}