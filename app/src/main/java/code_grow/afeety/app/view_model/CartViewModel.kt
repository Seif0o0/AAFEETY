package code_grow.afeety.app.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code_grow.afeety.app.repository.MedicineRepository
import kotlinx.coroutines.launch

class CartViewModel(
    private val app: Application,
    private val localRepository: MedicineRepository
) : ViewModel() {

    val allMedicines = localRepository.allMedicines

    fun removeMedicine(medicineId: Int) {
        viewModelScope.launch {
            localRepository.removeMedicineById(medicineId)
        }
    }



}