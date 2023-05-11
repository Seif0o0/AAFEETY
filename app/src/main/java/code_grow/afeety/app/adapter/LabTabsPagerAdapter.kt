package code_grow.afeety.app.adapter

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import code_grow.afeety.app.fragment.LabReviewsFragment
import code_grow.afeety.app.fragment.LabExaminationsFragment
import code_grow.afeety.app.model.LabExamination

class LabTabsPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    val labId: Int,
    private val examinationsList: MutableList<LabExamination>
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount() = 2
    override fun createFragment(position: Int) = when (position) {
        0 -> LabExaminationsFragment.newInstance(examinationsList)
        else -> LabReviewsFragment.newInstance(labId)
    }


}