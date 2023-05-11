package code_grow.afeety.app.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import code_grow.afeety.app.local_model.LocalMedicine
import code_grow.afeety.app.repository.CartRepository
import code_grow.afeety.app.repository.MedicineRepository

class OrderDetailsViewModelFactory(
    private val medicines: MutableList<LocalMedicine>,
    private val addressId: Int,
    private val application: Application,
    private val repo: CartRepository,
    private val localRepository: MedicineRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OrderDetailsViewModel(
                medicines,
                addressId,
                application,
                repo,
                localRepository
            ) as T
        }
        throw IllegalArgumentException("Unable to construct viewModel")
    }
}