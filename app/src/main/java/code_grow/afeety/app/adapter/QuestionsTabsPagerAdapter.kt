package code_grow.afeety.app.adapter

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import code_grow.afeety.app.fragment.FilteredQuestionsFragment
import code_grow.afeety.app.fragment.QuestionsFragment

class QuestionsTabsPagerAdapter(
    private val fragmentManager: FragmentManager,
    lifeCycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifeCycle) {
    override fun getItemCount() = 2

    override fun createFragment(position: Int) = when (position) {
        0 -> QuestionsFragment.newInstance()
        else -> FilteredQuestionsFragment.newInstance()
    }

    fun filter(query: String, currentItem: Int) {
        when (currentItem) {
            0 -> {
                val fragment = fragmentManager.fragments[currentItem] as QuestionsFragment
                fragment.filter(query)
            }
            else -> {
                val fragment = fragmentManager.fragments[currentItem] as FilteredQuestionsFragment
                fragment.filter(query)
            }
        }
    }

}