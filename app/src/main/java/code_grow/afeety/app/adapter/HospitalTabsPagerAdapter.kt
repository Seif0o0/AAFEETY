package code_grow.afeety.app.adapter

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import code_grow.afeety.app.fragment.*
import code_grow.afeety.app.model.Speciality

class HospitalTabsPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val latitude: Double,
    private val longitude: Double,
    private val details: String,
    private val hospitalId: Int,
    private val specialities: MutableList<Speciality>
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount() = 3
    override fun createFragment(position: Int) = when (position) {
        0 -> HospitalMoreDetailsFragment.newInstance(
            latitude,
            longitude,
            details
        )
        1 -> DoctorsFragment.newInstance(hospitalId, specialities.toTypedArray())
        else -> HospitalReviewsFragment.newInstance(hospitalId)
    }


}