package code_grow.afeety.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import code_grow.afeety.app.R
import code_grow.afeety.app.activity.MainActivity
import code_grow.afeety.app.adapter.MyReservationsAdapter
import code_grow.afeety.app.databinding.FragmentMyReservationsBinding
import code_grow.afeety.app.utils.Localize
import com.google.android.material.tabs.TabLayoutMediator

class MyReservationsFragment : Fragment() {
    private lateinit var binding: FragmentMyReservationsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        binding = FragmentMyReservationsBinding.inflate(inflater, container, false)

        // hide bottom navigation while creating this fragment
        val activity = requireActivity() as MainActivity
        activity.hideBottomNav(true)

        val adapter = MyReservationsAdapter(childFragmentManager, lifecycle)
        binding.myReservationsViewPager.adapter = adapter

        val isLab = MyReservationsFragmentArgs.fromBundle(requireArguments()).isLab
        binding.myReservationsViewPager.currentItem = if (isLab) 0 else 1
        val tabsNames = resources.getStringArray(R.array.my_reservations_tabs_titles)
        TabLayoutMediator(
            binding.myReservationsTabs,
            binding.myReservationsViewPager
        ) { tab, position ->
            tab.text = tabsNames[position]
        }.attach()

        binding.navigateBack.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }
}