package code_grow.afeety.app.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import code_grow.afeety.app.repository.MedicineRepository
import code_grow.afeety.app.repository.PharmaciesRepository

class MedicinesViewModelFactory(
    private val pharmacyId: Int,
    private val pharmacyName: String,
    private val deliveryFees: Double,
    private val query: String,
    private val categoryId: String,
    private val app: Application,
    private val repo: PharmaciesRepository,
    private val localRepository: MedicineRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MedicinesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MedicinesViewModel(
                pharmacyId,
                pharmacyName,
                deliveryFees,
                query,
                categoryId,
                app,
                repo,
                localRepository
            ) as T
        }
        throw IllegalArgumentException("Unable to construct viewModel")
    }
}
