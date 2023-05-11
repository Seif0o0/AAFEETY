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
import code_grow.afeety.app.databinding.FragmentLabsBinding
import code_grow.afeety.app.databinding.LabsFilterByDialogLayoutBinding
import code_grow.afeety.app.kot_pref.UserInfo
import code_grow.afeety.app.model.Lab
import code_grow.afeety.app.model.SliderItem
import code_grow.afeety.app.repository.LabsRepository
import code_grow.afeety.app.retrofit.Resource
import code_grow.afeety.app.utils.Localize
import code_grow.afeety.app.view_model.LabsViewModel
import code_grow.afeety.app.view_model.LabsViewModelFactory
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.ModalDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView

private const val TAG = "LabsFragment"

class LabsFragment : Fragment() {
    private lateinit var binding: FragmentLabsBinding
    private lateinit var viewModel: LabsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        binding = FragmentLabsBinding.inflate(inflater, container, false)
        val viewModelFactory = LabsViewModelFactory(
            requireActivity().application,
            LabsRepository()
        )
        viewModel = ViewModelProvider(this, viewModelFactory)[LabsViewModel::class.java]
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
        binding.labsViewPager.adapter = sliderAdapter

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


        binding.homeReservationCheckbox.tag = "0"
        binding.homeReservationCheckbox.setOnClickListener {
            viewModel.labsQueryMap.clear()
            if (binding.homeReservationCheckbox.tag == "0") {
                binding.homeReservationCheckbox.tag = "1"
                viewModel.labsQueryMap["home_visit"] = "1"
                binding.homeReservationCheckbox.setImageResource(R.drawable.ic_labs_checked_check_box)
            } else {
                binding.homeReservationCheckbox.tag = "0"
                viewModel.labsQueryMap["home_visit"] = "0"
                binding.homeReservationCheckbox.setImageResource(R.drawable.ic_labs_empty_check_box)
            }
            val cityId = UserInfo.cityId
            if (cityId != 0)
                viewModel.labsQueryMap["city_id"] = cityId.toString()
            if (binding.searchEditText.text.toString().isNotEmpty())
                viewModel.labsQueryMap["search"] = binding.searchEditText.text.toString()

            viewModel.setStartRequestLabs(true)
        }

        val bestLabsAdapter = LabsAdapter(OnLabItemClickListener {
            findNavController().navigate(
                LabsFragmentDirections.actionLabsListFragmentToLabDetailsFragment(it)
            )
        })

        binding.bestLabsList.adapter = bestLabsAdapter
        binding.bestLabsList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        viewModel.bestLabsResponse.observe(viewLifecycleOwner) {
            if (it is Resource.Failed) {
                binding.bestLabsErrorView.error = it.message
                binding.bestLabsErrorView.cancelAnimation = false
            } else if (it is Resource.Success<*>) {
                bestLabsAdapter.submitList(it.data as MutableList<Lab>)
            } else if (it is Resource.Empty) {
                binding.bestLabsGroup.visibility = View.GONE
            }
        }

        binding.bestLabsErrorView.root.setOnClickListener {
            binding.bestLabsErrorView.root.visibility = View.GONE
            binding.bestLabsErrorView.cancelAnimation = true
            viewModel.getBestLabs()
        }

        val labsAdapter = VerticalLabsAdapter(OnVLabItemClickListener {
            findNavController().navigate(
                LabsFragmentDirections.actionLabsListFragmentToLabDetailsFragment(it)
            )
        })

        binding.labsList.adapter = labsAdapter
        binding.labsList.layoutManager =
            LinearLayoutManager(requireContext())
        viewModel.labsResponse.observe(viewLifecycleOwner) {
            if (it is Resource.Failed) {
                binding.labsErrorView.error = it.message
                binding.labsErrorView.cancelAnimation = false
            } else if (it is Resource.Success<*>) {
                labsAdapter.submitList(it.data as MutableList<Lab>)
            }
        }

        binding.labsErrorView.root.setOnClickListener {
            binding.labsErrorView.root.visibility = View.GONE
            binding.labsErrorView.cancelAnimation = true
            viewModel.setStartRequestLabs(true)
        }

        viewModel.startRequestLabs.observe(viewLifecycleOwner) {
            if (it) {/* start requesting labs and observe its response */
                viewModel.getLabs()
            }
        }

        binding.searchEditText.tag = "false";
        binding.searchEditText.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus -> binding.searchEditText.tag = "$hasFocus" }

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.searchEditText.tag.toString().lowercase() == "true"){
                    viewModel.labsQueryMap.clear()
                    val cityId = UserInfo.cityId
                    if (cityId != 0)
                        viewModel.labsQueryMap["city_id"] = cityId.toString()
                    viewModel.labsQueryMap["home_visit"] =
                        binding.homeReservationCheckbox.tag.toString()
                    if (s.toString().isNotEmpty())
                        viewModel.labsQueryMap["search"] = s.toString()
                    viewModel.setStartRequestLabs(true)
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })



        binding.filterIcon.setOnClickListener {
            if (UserInfo.userId == 0)
                return@setOnClickListener
            showFilterDialog()
        }

        return binding.root
    }

    private fun showFilterDialog() {
        val dialog = MaterialDialog(requireContext(), ModalDialog).show {
            cornerRadius(res = com.intuit.sdp.R.dimen._8sdp)
            customView(
                R.layout.labs_filter_by_dialog_layout,
                noVerticalPadding = true,
                dialogWrapContent = true
            )
        }

        val dialogBinding = LabsFilterByDialogLayoutBinding.bind(dialog.getCustomView())
        dialogBinding.viewModel = viewModel

        viewModel.filterByLiveData.observe(viewLifecycleOwner) {
            if (it == 0) {
                dialogBinding.distanceButton.strokeWidth = 0
                dialogBinding.distanceButton.backgroundTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.labs_main_color
                        )
                    )
                dialogBinding.distanceButton.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.labs_background_color
                    )
                )

                dialogBinding.rateButton.strokeWidth = resources.getDimension(com.intuit.sdp.R.dimen._1sdp).toInt()
                dialogBinding.rateButton.strokeColor =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.labs_main_color
                        )
                    )
                dialogBinding.rateButton.backgroundTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.labs_background_color
                        )
                    )
                dialogBinding.rateButton.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.labs_main_color
                    )
                )
            } else {
                dialogBinding.rateButton.strokeWidth = 0
                dialogBinding.rateButton.backgroundTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.labs_main_color
                        )
                    )
                dialogBinding.rateButton.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.labs_background_color
                    )
                )

                dialogBinding.distanceButton.strokeWidth =
                    resources.getDimension(com.intuit.sdp.R.dimen._1sdp).toInt()
                dialogBinding.distanceButton.strokeColor =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.labs_main_color
                        )
                    )
                dialogBinding.distanceButton.backgroundTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.labs_background_color
                        )
                    )
                dialogBinding.distanceButton.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.labs_main_color
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