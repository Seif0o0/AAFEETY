package code_grow.afeety.app.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import code_grow.afeety.app.local_model.LocalProduct
import code_grow.afeety.app.repository.CartRepository
import code_grow.afeety.app.repository.LocalProductRepository

class ProductOrderDetailsViewModelFactory(
    private val products: MutableList<LocalProduct>,
    private val addressId: Int,
    private val deliveryFees:String,
    private val application: Application,
    private val repo: CartRepository,
    private val localRepository: LocalProductRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductOrderDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductOrderDetailsViewModel(
                products,
                addressId,
                deliveryFees,
                application,
                repo,
                localRepository
            ) as T
        }
        throw IllegalArgumentException("Unable to construct viewModel")
    }
}