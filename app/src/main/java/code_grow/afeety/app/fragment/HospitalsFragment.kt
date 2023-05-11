package code_grow.afeety.app.fragment

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import code_grow.afeety.app.R
import code_grow.afeety.app.activity.MainActivity
import code_grow.afeety.app.adapter.*
import code_grow.afeety.app.databinding.FragmentHospitalsBinding
import code_grow.afeety.app.databinding.HospitalsFilterByDialogLayoutBinding
import code_grow.afeety.app.kot_pref.UserInfo
import code_grow.afeety.app.model.Hospital
import code_grow.afeety.app.model.SliderItem
import code_grow.afeety.app.model.Speciality
import code_grow.afeety.app.repository.HospitalsRepository
import code_grow.afeety.app.retrofit.Resource
import code_grow.afeety.app.utils.Localize
import code_grow.afeety.app.view_model.HospitalsViewModel
import code_grow.afeety.app.view_model.HospitalsViewModelFactory
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.ModalDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView

private const val TAG = "HospitalsViewModel"

class HospitalsFragment : Fragment() {
    private lateinit var binding: FragmentHospitalsBinding
    private lateinit var viewModel: HospitalsViewModel
    private var specialityId = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        binding = FragmentHospitalsBinding.inflate(inflater, container, false)
        val viewModelFactory = HospitalsViewModelFactory(
            requireActivity().application,
            HospitalsRepository()
        )
        viewModel = ViewModelProvider(this, viewModelFactory)[HospitalsViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()

        // hide bottom navigation while creating this fragment
        val activity = requireActivity() as MainActivity
        activity.hideBottomNav(true)

        binding.navigateBack.setOnClickListener {
            findNavController().popBackStack()
        }

        val sliderAdapter = SliderAdapter(OnSliderItemClickListener {

        })
        binding.hospitalsViewPager.adapter = sliderAdapter

        viewModel.startRequestSlider.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.getSlider()
            }
        }

        viewModel.sliderResponse.observe(viewLifecycleOwner) {
            viewModel.setStartRequestSlider(false)
            if (it is Resource.Failed) {
                binding.sliderErrorView.error = it.message
                binding.sliderErrorView.cancelAnimation = false
            } else if (it is Resource.Success<*>) {
                sliderAdapter.submitList(it.data as MutableList<SliderItem>)
            }
        }

        binding.sliderErrorView.root.setOnClickListener {
            binding.sliderErrorView.root.visibility = View.GONE
            binding.sliderErrorView.cancelAnimation = true
            viewModel.setStartRequestSlider(true)
        }

        binding.filterIcon.setOnClickListener {
            if (UserInfo.userId == 0)
                return@setOnClickListener
            showFilterDialog()
        }


        /* setup specialities-section views & data */
        val specialitiesAdapter = SpecialitiesAdapter(OnSpecialityItemCLickListener {
            val adapter = binding.specialitiesList.adapter as SpecialitiesAdapter
            val specialitiesList = adapter.currentList.toMutableList()

            for (index in 0 until specialitiesList.size) {
                if (specialitiesList[index].clicked) {
                    specialitiesList[index].clicked = false
                    adapter.submitList(specialitiesList)
                    adapter.notifyItemChanged(index, Unit)
                }

                if (specialitiesList[index].specialityId == it.specialityId) {
                    specialitiesList[index].clicked = true
                    adapter.submitList(specialitiesList)
                    adapter.notifyItemChanged(index)
                }
            }
            viewModel.hospitalsQueryMap.clear()
            specialityId = it.specialityId
            if (UserInfo.cityId != 0)
                viewModel.hospitalsQueryMap["city_id"] = UserInfo.cityId.toString()
            if (it.specialityId != 0)
                viewModel.hospitalsQueryMap["speciality_id"] = it.specialityId.toString()
            if (binding.searchEditText.text.toString().isNotEmpty()) {
                viewModel.hospitalsQueryMap["search"] = binding.searchEditText.text.toString()
            }
            viewModel.setStartRequestHospitals(true)
        })
        binding.specialitiesList.adapter = specialitiesAdapter
        binding.specialitiesList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        viewModel.specialitiesResponse.observe(viewLifecycleOwner) {
            if (it is Resource.Failed) {
                binding.specialitiesErrorView.error = it.message
                binding.specialitiesErrorView.cancelAnimation = false
            } else if (it is Resource.Success<*>) {
                specialitiesAdapter.submitList(it.data as MutableList<Speciality>)
            } else if (it is Resource.Empty) {
                binding.specialHospitalsGroup.visibility = View.GONE
            }
        }
        binding.specialitiesErrorView.root.setOnClickListener {
            binding.specialitiesErrorView.root.visibility = View.GONE
            binding.specialitiesErrorView.cancelAnimation = true
            viewModel.getSpecialities()
        }

        /* setup hospitals-section views & data */
        val hospitalsAdapter = VerticalHospitalsAdapter(OnVHospitalItemClickListener {
            findNavController().navigate(
                HospitalsFragmentDirections.actionHospitalsListFragmentToHospitalDetailsFragment(
                    it
                )
            )
        })
        binding.hospitalsList.adapter = hospitalsAdapter
        binding.hospitalsList.layoutManager = LinearLayoutManager(requireContext())
        viewModel.hospitalsResponse.observe(viewLifecycleOwner) {
            viewModel.setStartRequestHospitals(false)/* don't observe the response again until value change to true */
            if (it is Resource.Failed) {
                binding.hospitalsErrorView.error = it.message
                binding.hospitalsErrorView.cancelAnimation = false
            } else if (it is Resource.Success<*>) {
                hospitalsAdapter.submitList(it.data as MutableList<Hospital>)
            }
        }
        binding.hospitalsErrorView.root.setOnClickListener {
            binding.hospitalsErrorView.root.visibility = View.GONE
            binding.hospitalsErrorView.cancelAnimation = true
            viewModel.setStartRequestHospitals(true)
        }

        viewModel.startRequestHospitals.observe(viewLifecycleOwner) {
            if (it) {/* start requesting hospitals and observe its response */
                viewModel.getHospitals()
            }
        }

        binding.searchEditText.tag = "false";
        binding.searchEditText.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus -> binding.searchEditText.tag = "$hasFocus" }

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.searchEditText.tag.toString().lowercase() == "true") {
                    viewModel.hospitalsQueryMap.clear()
                    if (UserInfo.cityId != 0)
                        viewModel.hospitalsQueryMap["city_id"] = UserInfo.cityId.toString()
                    if (specialityId != 0)
                        viewModel.hospitalsQueryMap["speciality_id"] = specialityId.toString()
                    viewModel.hospitalsQueryMap["search"] = s.toString()

                    viewModel.setStartRequestHospitals(true)
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })


        /* setup specialHospitals-section views & data */
        val specialHospitalsAdapter = HospitalsAdapter(OnHospitalItemClickListener {
            findNavController().navigate(
                HospitalsFragmentDirections.actionHospitalsListFragmentToHospitalDetailsFragment(
                    it
                )
            )
        })

        binding.specialHospitalsList.adapter = specialHospitalsAdapter
        binding.specialHospitalsList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        viewModel.specialHospitalsResponse.observe(viewLifecycleOwner) {
            if (it is Resource.Failed) {
                binding.specialHospitalsErrorView.error = it.message
                binding.specialHospitalsErrorView.cancelAnimation = false
            } else if (it is Resource.Success<*>) {
                specialHospitalsAdapter.submitList(it.data as MutableList<Hospital>)
            }
        }
        binding.specialHospitalsErrorView.root.setOnClickListener {
            binding.specialHospitalsErrorView.root.visibility = View.GONE
            binding.specialHospitalsErrorView.cancelAnimation = true
            viewModel.getSpecialHospitals()
        }


        return binding.root
    }

    private fun showFilterDialog() {
        val dialog = MaterialDialog(requireContext(), ModalDialog).show {
            cornerRadius(res = com.intuit.sdp.R.dimen._8sdp)
            customView(
                R.layout.hospitals_filter_by_dialog_layout,
                noVerticalPadding = true,
                dialogWrapContent = true
            )
        }

        val dialogBinding = HospitalsFilterByDialogLayoutBinding.bind(dialog.getCustomView())
        dialogBinding.viewModel = viewModel

        viewModel.filterByLiveData.observe(viewLifecycleOwner) {
            if (it == 0) {
                dialogBinding.distanceButton.strokeWidth = 0
                dialogBinding.distanceButton.backgroundTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.hospitals_main_color
                        )
                    )
                dialogBinding.distanceButton.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.hospitals_background_color
                    )
                )

                dialogBinding.rateButton.strokeWidth = resources.getDimension(com.intuit.sdp.R.dimen._1sdp).toInt()
                dialogBinding.rateButton.strokeColor =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.hospitals_main_color
                        )
                    )
                dialogBinding.rateButton.backgroundTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.hospitals_background_color
                        )
                    )
                dialogBinding.rateButton.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.hospitals_main_color
                    )
                )
            } else {
                dialogBinding.rateButton.strokeWidth = 0
                dialogBinding.rateButton.backgroundTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.hospitals_main_color
                        )
                    )
                dialogBinding.rateButton.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.hospitals_background_color
                    )
                )

                dialogBinding.distanceButton.strokeWidth =
                    resources.getDimension(com.intuit.sdp.R.dimen._1sdp).toInt()
                dialogBinding.distanceButton.strokeColor =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.hospitals_main_color
                        )
                    )
                dialogBinding.distanceButton.backgroundTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.hospitals_background_color
                        )
                    )
                dialogBinding.distanceButton.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.hospitals_main_color
                    )
                )
            }
        }

        dialogBinding.distanceButton.setOnClickListener {
            viewModel.filterByLiveData.value = 0
        }

        dialogBinding.rateButton.setOnClickListener {
            viewModel.filterByLiveData.value = 1
        }

        dialogBinding.chooseButton.setOnClickListener {
            viewModel.startFiltering()
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            viewModel.filterByLiveData.removeObservers(viewLifecycleOwner)
        }
    }
}