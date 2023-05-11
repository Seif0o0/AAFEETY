package code_grow.afeety.app.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import code_grow.afeety.app.R
import code_grow.afeety.app.activity.MainActivity
import code_grow.afeety.app.adapter.HospitalTabsPagerAdapter
import code_grow.afeety.app.databinding.FragmentHospitalDetailsBinding
import code_grow.afeety.app.model.Speciality
import code_grow.afeety.app.repository.HospitalsRepository
import code_grow.afeety.app.retrofit.Resource
import code_grow.afeety.app.utils.CustomDialog
import code_grow.afeety.app.utils.Localize
import code_grow.afeety.app.view_model.HospitalDetailsViewModel
import code_grow.afeety.app.view_model.HospitalDetailsViewModelFactory
import com.google.android.material.tabs.TabLayoutMediator

class HospitalDetailsFragment : Fragment() {
    private lateinit var binding: FragmentHospitalDetailsBinding
    private lateinit var viewModel: HospitalDetailsViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        binding = FragmentHospitalDetailsBinding.inflate(inflater, container, false)
        val hospitalDetails =
            HospitalDetailsFragmentArgs.fromBundle(requireArguments()).hospitalDetails
        val viewModelFactory =
            HospitalDetailsViewModelFactory(
                hospitalDetails.hospitalId,
                requireActivity().application,
                HospitalsRepository()
            )

        viewModel = ViewModelProvider(this, viewModelFactory)[HospitalDetailsViewModel::class.java]
        binding.viewModel = viewModel
        binding.hospital = hospitalDetails
        binding.lifecycleOwner = requireActivity()

        // hide bottom navigation while creating this fragment
        val activity = requireActivity() as MainActivity
        activity.hideBottomNav(true)

        binding.facebook.setOnClickListener {
            navigateSocial(hospitalDetails.facebookLink)
        }

        binding.twitter.setOnClickListener {
            navigateSocial(hospitalDetails.twitterLink)
        }

        binding.instagram.setOnClickListener {
            navigateSocial(hospitalDetails.instagramLink)
        }

        /* setup specialities api - data & send data to doctors fragment using HospitalTabsPagerAdapter */
        viewModel.startRequestSpecialities.observe(viewLifecycleOwner) {
            if (it)
                viewModel.getSpecialities()
        }

        viewModel.specialitiesResponse.observe(viewLifecycleOwner) {
            viewModel.setStartRequestSpecialities(false)
            if (it is Resource.Failed) {
                binding.errorView.error = it.message
                binding.errorView.cancelAnimation = false
            } else if (it is Resource.Success<*>) {
                val specialities = it.data as MutableList<Speciality>
//                specialities[0].clicked = true
                val adapter = HospitalTabsPagerAdapter(
                    childFragmentManager,
                    lifecycle,
                    hospitalDetails.latitude,
                    hospitalDetails.longitude,
                    hospitalDetails.details,
                    hospitalDetails.hospitalId,
                    specialities
                )
                binding.hospitalDetailsViewPager.adapter = adapter

                val tabsNames = resources.getStringArray(R.array.hospital_details_tabs)
                TabLayoutMediator(
                    binding.hospitalDetailsTab,
                    binding.hospitalDetailsViewPager
                ) { tab, position ->
                    tab.text = tabsNames[position]
                }.attach()
            }
        }
        binding.errorView.root.setOnClickListener {
            binding.errorView.root.visibility = View.GONE
            binding.errorView.cancelAnimation = true
            viewModel.setStartRequestSpecialities(true)
        }

        binding.navigateBack.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }

    private fun navigateSocial(url: String) {
        if(url.isEmpty()){
            CustomDialog.showErrorDialog(
                context = requireContext(),
                errorMessage = getString(R.string.social_media_link_error_message)
            )
            return;
        }
        startActivity(Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        })
    }
}