package code_grow.afeety.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import code_grow.afeety.app.R
import code_grow.afeety.app.activity.MainActivity
import code_grow.afeety.app.adapter.LabTabsPagerAdapter
import code_grow.afeety.app.databinding.FragmentLabDetailsBinding
import code_grow.afeety.app.model.Lab
import code_grow.afeety.app.model.LabExamination
import code_grow.afeety.app.utils.CustomDialog
import code_grow.afeety.app.utils.Localize
import com.google.android.material.tabs.TabLayoutMediator

private const val TAG = "LabDetailsFragment"

class LabDetailsFragment : Fragment(), LabExaminationsFragment.BookExaminationInterface {
    private lateinit var binding: FragmentLabDetailsBinding
    private lateinit var switchBtn: ViewPager2.OnPageChangeCallback
    private lateinit var labDetails :Lab
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        binding = FragmentLabDetailsBinding.inflate(inflater, container, false)
        labDetails = LabDetailsFragmentArgs.fromBundle(requireArguments()).labDetails
        binding.lab = labDetails


        // hide bottom navigation while creating this fragment
        val activity = requireActivity() as MainActivity
        activity.hideBottomNav(true)

        val adapter = LabTabsPagerAdapter(
            childFragmentManager,
            lifecycle,
            labDetails.labId,
            labDetails.examinations!!
        )
        binding.testsRatesViewPager.adapter = adapter

        switchBtn = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    binding.reservationButtonContainer.visibility = View.VISIBLE
                } else
                    binding.reservationButtonContainer.visibility = View.GONE
            }
        }
        binding.testsRatesViewPager.registerOnPageChangeCallback(switchBtn)

        binding.reservationButton.setOnClickListener {
            if (selectedExaminations.isEmpty()) {
                CustomDialog.showErrorDialog(
                    context = requireContext(),
                    errorMessage = getString(R.string.pick_examination_to_continue)
                )
                return@setOnClickListener
            }

            findNavController().navigate(
                LabDetailsFragmentDirections.actionLabDetailsFragmentToReservationDetailsFragment(
                    selectedExaminations.toTypedArray(),
                    labDetails
                )
            )
        }
        binding.navigateBack.setOnClickListener {
            selectedExaminations.clear()
            findNavController().popBackStack()
        }
        val tabsNames = resources.getStringArray(R.array.lab_details_tabs)
        TabLayoutMediator(binding.testsRatesTabs, binding.testsRatesViewPager) { tab, position ->
            tab.text = tabsNames[position]
        }.attach()

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        labDetails.examinations!!.forEach{
            if (it.isClicked) it.isClicked = false
        }
        binding.testsRatesViewPager.unregisterOnPageChangeCallback(switchBtn)
    }

    private val selectedExaminations = mutableListOf<LabExamination>()
    override fun startBookingExamination(examinations: MutableList<LabExamination>) {
        selectedExaminations.clear()
        selectedExaminations.addAll(examinations)
    }


}