package code_grow.afeety.app.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code_grow.afeety.app.repository.LocalProductRepository
import kotlinx.coroutines.launch

class ProductCartViewModel(
    private val app: Application,
    private val localRepository: LocalProductRepository
) : ViewModel() {

    val allProducts = localRepository.allProducts

    fun removeProduct(medicineId: Int) {
        viewModelScope.launch {
            localRepository.removeProductById(medicineId)
        }
    }



}