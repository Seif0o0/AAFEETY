package code_grow.afeety.app.adapter

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import code_grow.afeety.app.fragment.*

class MyOrdersAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount() = 3
    override fun createFragment(position: Int) = when (position) {
        0 -> MyPharmacyOrdersFragment.newInstance()
        1 -> MyProductOrdersFragment.newInstance()
        else -> MyFamousOrdersFragment.newInstance()
    }


}