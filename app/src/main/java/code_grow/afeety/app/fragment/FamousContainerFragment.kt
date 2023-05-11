package code_grow.afeety.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import code_grow.afeety.app.R
import code_grow.afeety.app.activity.MainActivity
import code_grow.afeety.app.adapter.FamousTabsPagerAdapter
import code_grow.afeety.app.databinding.FragmentFamousContainerBinding
import code_grow.afeety.app.utils.Localize
import com.google.android.material.tabs.TabLayoutMediator

class FamousContainerFragment : Fragment() {
    private lateinit var binding: FragmentFamousContainerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        binding = FragmentFamousContainerBinding.inflate(inflater, container, false)

        // hide bottom navigation while creating this fragment
        val activity = requireActivity() as MainActivity
        activity.hideBottomNav(true)

        binding.navigateBack.setOnClickListener {
            findNavController().popBackStack()
        }

        val tabsAdapter = FamousTabsPagerAdapter(
            childFragmentManager,
            lifecycle
        )

        binding.famousViewPager.adapter = tabsAdapter

        val tabsNames = resources.getStringArray(R.array.famous_tabs_titles)

        TabLayoutMediator(
            binding.famousTab,
            binding.famousViewPager
        ) { tab, position ->
            tab.text = tabsNames[position]
        }.attach()



        return binding.root

    }


}