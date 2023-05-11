package code_grow.afeety.app.fragment

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import code_grow.afeety.app.R
import code_grow.afeety.app.activity.CartActivity
import code_grow.afeety.app.activity.LoginRegisterActivity
import code_grow.afeety.app.activity.MainActivity
import code_grow.afeety.app.activity.ProductCartActivity
import code_grow.afeety.app.adapter.*
import code_grow.afeety.app.databinding.FragmentPharmaciesBinding
import code_grow.afeety.app.databinding.PharmaciesFilterDialogLayoutBinding
import code_grow.afeety.app.kot_pref.CartInfo
import code_grow.afeety.app.kot_pref.UserInfo
import code_grow.afeety.app.model.*
import code_grow.afeety.app.repository.PharmaciesRepository
import code_grow.afeety.app.retrofit.Resource
import code_grow.afeety.app.utils.Localize
import code_grow.afeety.app.view_model.PharmaciesViewModel
import code_grow.afeety.app.view_model.PharmaciesViewModelFactory
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.ModalDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView

private const val TAG = "PharmaciesFragment"

class PharmaciesFragment : Fragment() {
    private lateinit var binding: FragmentPharmaciesBinding
    private lateinit var viewModel: PharmaciesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        binding = FragmentPharmaciesBinding.inflate(inflater, container, false)
        val viewModelFactory = PharmaciesViewModelFactory(
            requireActivity().application,
            PharmaciesRepository()
        )
        viewModel = ViewModelProvider(this, viewModelFactory)[PharmaciesViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()

        // hide bottom navigation while creating this fragment
        val activity = requireActivity() as MainActivity
        activity.hideBottomNav(true)

        binding.navigateBack.setOnClickListener {
            findNavController().popBackStack()
        }

        val cartLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val goHome = it.data?.getBooleanExtra("goHome", true) ?: true
                    if (goHome) {
                        findNavController().popBackStack()
                    } else {
                        findNavController().navigate(
                            PharmaciesFragmentDirections.actionPharmaciesListFragmentToMyOrdersFragment(
                                if (CartInfo.cartStatus == 0 || CartInfo.cartStatus == 1) 0 else CartInfo.cartStatus - 1
                            )
                        )
                    }
                }
            }

        val cartLoginLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    cartLauncher.launch(Intent(requireContext(), CartActivity::class.java))
                }
            }

        binding.cartIcon.setOnClickListener {
            if (UserInfo.userId == 0) {
                cartLoginLauncher.launch(
                    Intent(
                        requireContext(),
                        LoginRegisterActivity::class.java
                    ).apply {
                        putExtra("hideGuestLogin", true)
                    })
            } else {
                cartLauncher.launch(
                    Intent(
                        requireContext(),
                        if (CartInfo.cartStatus == 0 || CartInfo.cartStatus == 1) CartActivity::class.java else ProductCartActivity::class.java
                    )
                )
            }
        }


        val sliderAdapter = SliderAdapter(OnSliderItemClickListener {

        })
        binding.pharmaciesViewPager.adapter = sliderAdapter

        viewModel.startRequestSlider.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.getSlider()
            }
        }

        viewModel.sliderResponse.observe(viewLifecycleOwner) {
            viewModel.startRequestSlider(false)
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
            viewModel.startRequestSlider(true)
        }


        val specialPharmacies = PharmaciesAdapter(OnPharmacyItemClickListener {
            findNavController().navigate(
                PharmaciesFragmentDirections.actionPharmaciesListFragmentToPharmacyDetailsFragment(
                    it
                )
            )
        })
        binding.specialPharmaciesList.adapter = specialPharmacies
        binding.specialPharmaciesList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        viewModel.bestPharmaciesResponse.observe(viewLifecycleOwner) {
            if (it is Resource.Failed) {
                binding.specialPharmaciesErrorView.error = it.message
                binding.specialPharmaciesErrorView.cancelAnimation = false
            } else if (it is Resource.Success<*>) {
                specialPharmacies.submitList(it.data as MutableList<Pharmacy>)
            }
        }
        binding.specialPharmaciesErrorView.root.setOnClickListener {
            binding.specialPharmaciesErrorView.root.visibility = View.GONE
            binding.specialPharmaciesErrorView.cancelAnimation = true
            viewModel.getBestPharmacies()
        }

        val pharmacyAdapter = VerticalPharmaciesAdapter(OnVPharmacyItemClickListener {
            findNavController().navigate(
                PharmaciesFragmentDirections.actionPharmaciesListFragmentToPharmacyDetailsFragment(
                    it
                )
            )
        })
        binding.pharmaciesList.adapter = pharmacyAdapter
        binding.pharmaciesList.layoutManager =
            LinearLayoutManager(requireContext())

        viewModel.pharmaciesResponse.observe(viewLifecycleOwner) {
            if (it is Resource.Failed) {
                binding.pharmaciesErrorView.error = it.message
                binding.pharmaciesErrorView.cancelAnimation = false
            } else if (it is Resource.Success<*>) {
                pharmacyAdapter.submitList(it.data as MutableList<Pharmacy>)
            }
        }
        binding.pharmaciesErrorView.root.setOnClickListener {
            binding.pharmaciesErrorView.root.visibility = View.GONE
            binding.pharmaciesErrorView.cancelAnimation = true
            viewModel.startRequestPharmacies(true)
        }

        viewModel.startRequestPharmacies.observe(viewLifecycleOwner) {
            if (it) {/* start requesting pharmacies and observe its response */
                viewModel.getPharmacies()
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
                    viewModel.pharmaciesQueryMap.clear()
                    if (UserInfo.userId != 0)
                        viewModel.pharmaciesQueryMap["city_id"] = UserInfo.cityId.toString()
                    viewModel.pharmaciesQueryMap["search"] = s.toString()
                    viewModel.startRequestPharmacies(true)
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
                R.layout.pharmacies_filter_dialog_layout,
                noVerticalPadding = true,
                dialogWrapContent = true
            )
        }

        val dialogBinding = PharmaciesFilterDialogLayoutBinding.bind(dialog.getCustomView())
        dialogBinding.viewModel = viewModel

        viewModel.filterByLiveData.observe(viewLifecycleOwner) {
            if (it == 0) {
                dialogBinding.distanceButton.strokeWidth = 0
                dialogBinding.distanceButton.backgroundTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.pharmacies_main_color
                        )
                    )
                dialogBinding.distanceButton.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.pharmacies_background_color
                    )
                )

                dialogBinding.rateButton.strokeWidth = resources.getDimension(com.intuit.sdp.R.dimen._1sdp).toInt()
                dialogBinding.rateButton.strokeColor =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.pharmacies_main_color
                        )
                    )
                dialogBinding.rateButton.backgroundTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.pharmacies_background_color
                        )
                    )
                dialogBinding.rateButton.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.pharmacies_main_color
                    )
                )
            } else {
                dialogBinding.rateButton.strokeWidth = 0
                dialogBinding.rateButton.backgroundTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.pharmacies_main_color
                        )
                    )
                dialogBinding.rateButton.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.pharmacies_background_color
                    )
                )

                dialogBinding.distanceButton.strokeWidth =
                    resources.getDimension(com.intuit.sdp.R.dimen._1sdp).toInt()
                dialogBinding.distanceButton.strokeColor =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.pharmacies_main_color
                        )
                    )
                dialogBinding.distanceButton.backgroundTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.pharmacies_background_color
                        )
                    )
                dialogBinding.distanceButton.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.pharmacies_main_color
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