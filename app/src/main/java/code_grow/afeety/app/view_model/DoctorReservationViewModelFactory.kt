package code_grow.afeety.app.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import code_grow.afeety.app.model.Doctor
import code_grow.afeety.app.model.DoctorSchedule
import code_grow.afeety.app.repository.HospitalsRepository

class DoctorReservationViewModelFactory(
    private val doctorDetails: Doctor,
    private val selectedSchedule: DoctorSchedule,
    private val app: Application,
    private val repo: HospitalsRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DoctorReservationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DoctorReservationViewModel(doctorDetails, selectedSchedule, app, repo) as T
        }
        throw IllegalArgumentException("Unable to construct viewModel")
    }
}
