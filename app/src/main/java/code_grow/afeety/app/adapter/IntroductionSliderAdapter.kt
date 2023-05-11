package code_grow.afeety.app.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import code_grow.afeety.app.fragment.IntroductionFragment

class IntroductionSliderAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment =
        IntroductionFragment.newInstance(position)
}
