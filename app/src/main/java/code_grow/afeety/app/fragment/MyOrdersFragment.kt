package code_grow.afeety.app.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import code_grow.afeety.app.R
import code_grow.afeety.app.activity.MainActivity
import code_grow.afeety.app.adapter.MyOrdersAdapter
import code_grow.afeety.app.databinding.FragmentMyOrdersBinding
import code_grow.afeety.app.utils.Localize
import com.google.android.material.tabs.TabLayoutMediator

class MyOrdersFragment : Fragment() {
    private lateinit var binding: FragmentMyOrdersBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        binding = FragmentMyOrdersBinding.inflate(inflater, container, false)

        // hide bottom navigation while creating this fragment
        val activity = requireActivity() as MainActivity
        activity.hideBottomNav(true)

        val adapter = MyOrdersAdapter(childFragmentManager, lifecycle)
        binding.myOrdersViewPager.adapter = adapter
        Log.d("MyOrdersFragment","Index: ${MyOrdersFragmentArgs.fromBundle(requireArguments()).index}")
        binding.myOrdersViewPager.currentItem =
            MyOrdersFragmentArgs.fromBundle(requireArguments()).index
        val tabsNames = resources.getStringArray(R.array.my_orders_tabs_titles)
        TabLayoutMediator(
            binding.myOrdersTabs,
            binding.myOrdersViewPager
        ) { tab, position ->
            tab.text = tabsNames[position]
        }.attach()

        binding.navigateBack.setOnClickListener {
            findNavController().popBackStack()
        }


        return binding.root
    }
}