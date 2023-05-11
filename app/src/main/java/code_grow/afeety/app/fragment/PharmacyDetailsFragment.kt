package code_grow.afeety.app.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import code_grow.afeety.app.R
import code_grow.afeety.app.activity.*
import code_grow.afeety.app.adapter.MedicineAdapter
import code_grow.afeety.app.adapter.OnMedicineItemClickListener
import code_grow.afeety.app.databinding.FragmentPharmacyDetailsBinding
import code_grow.afeety.app.dialog.MedicineSearchDialogFragment
import code_grow.afeety.app.kot_pref.CartInfo
import code_grow.afeety.app.kot_pref.UserInfo
import code_grow.afeety.app.local_model.LocalMedicine
import code_grow.afeety.app.model.Medicine
import code_grow.afeety.app.utils.Application
import code_grow.afeety.app.utils.CustomDialog
import code_grow.afeety.app.utils.Localize
import code_grow.afeety.app.view_model.PharmacyDetailsViewModel
import code_grow.afeety.app.view_model.PharmacyDetailsViewModelFactory

class PharmacyDetailsFragment : Fragment() {
    private lateinit var binding: FragmentPharmacyDetailsBinding
    private lateinit var viewModel: PharmacyDetailsViewModel
    private lateinit var medicine: Medicine

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        binding = FragmentPharmacyDetailsBinding.inflate(inflater, container, false)
        val pharmacyDetails =
            PharmacyDetailsFragmentArgs.fromBundle(requireArguments()).pharmacyDetails
        binding.pharmacy = pharmacyDetails
        binding.lifecycleOwner = requireActivity()


        val viewModelProvider = PharmacyDetailsViewModelFactory(
            pharmacyDetails.pharmacyId,
            pharmacyDetails.name,
            pharmacyDetails.deliveryFees,
            requireActivity().application,
            (requireActivity().application as Application).medicineRepo
        )
        viewModel = ViewModelProvider(this, viewModelProvider)[PharmacyDetailsViewModel::class.java]

        // hide bottom navigation while creating this fragment
        val activity = requireActivity() as MainActivity
        activity.hideBottomNav(true)

        val loginLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    addToCart(medicine)
                }
            }

        val medicineAdapter = MedicineAdapter(OnMedicineItemClickListener {
            if (UserInfo.userId == 0) {
                medicine = it
                loginLauncher.launch(
                    Intent(
                        requireContext(),
                        LoginRegisterActivity::class.java
                    ).apply {
                        putExtra("hideGuestLogin", true)
                    })
            } else {
                addToCart(it)
            }
        })

        if (pharmacyDetails.medicines.isNullOrEmpty())
            binding.emptyMedicinesText.visibility = View.VISIBLE
        else
            medicineAdapter.submitList(pharmacyDetails.medicines)

        binding.medicinesList.adapter = medicineAdapter
        binding.medicinesList.layoutManager = LinearLayoutManager(requireContext())


        binding.navigateBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.searchForMedicineBackground.setOnClickListener {
            showSearchDialog(
                pharmacyDetails.pharmacyId,
                pharmacyDetails.name,
                pharmacyDetails.deliveryFees,
                pharmacyDetails.bannerImage
            )
        }

        val currentCartStatus = CartInfo.cartStatus
        val cartPrescriptionLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    var goHome = it.data?.getBooleanExtra("goHome", true) ?: true
                    if (goHome) {
                        findNavController().navigate(PharmacyDetailsFragmentDirections.actionPharmacyDetailsFragmentToHomeFragment())
                    } else {
                        findNavController().navigate(
                            PharmacyDetailsFragmentDirections.actionPharmacyDetailsFragmentToMyOrdersFragment(
                                if (currentCartStatus == 0 || currentCartStatus == 1) 0 else currentCartStatus - 1
                            )
                        )
                    }

                }
            }


        val cartLoginLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    cartPrescriptionLauncher.launch(
                        Intent(
                            requireContext(),
                            CartActivity::class.java
                        )
                    )
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
                cartPrescriptionLauncher.launch(
                    Intent(
                        requireContext(),
                        if (CartInfo.cartStatus == 0 || CartInfo.cartStatus == 1) CartActivity::class.java else ProductCartActivity::class.java
                    )
                )
            }
        }

        val prescriptionLoginLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    cartPrescriptionLauncher.launch(
                        Intent(
                            requireContext(),
                            PrescriptionActivity::class.java
                        ).apply {
                            putExtra("pharmacy_id", pharmacyDetails.pharmacyId)
                        })
                }
            }
        binding.prescriptionBackground.setOnClickListener {
            if (UserInfo.userId == 0) {
                prescriptionLoginLauncher.launch(
                    Intent(
                        requireContext(),
                        LoginRegisterActivity::class.java
                    ).apply {
                        putExtra("hideGuestLogin", true)
                    })
            } else {
                cartPrescriptionLauncher.launch(
                    Intent(
                        requireContext(),
                        PrescriptionActivity::class.java
                    ).apply {
                        putExtra("pharmacy_id", pharmacyDetails.pharmacyId)
                    })
            }

        }
        binding.callNowBackground.setOnClickListener {
            if (pharmacyDetails.phoneNumber.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:${pharmacyDetails.phoneNumber}")
                startActivity(intent)
            }
        }

        viewModel.startAddToCart.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.addToCart()
            }
        }

        viewModel.addToCartResponse.observe(viewLifecycleOwner) {
            viewModel.startAddToCart(false, null)
            if (it == 1) {
                CustomDialog.showSuccessDialog(
                    context = requireContext(),
                    successMessage = getString(R.string.add_to_cart_success_message),
                    navController = null
                )
            } else if (it == 2) {
                CustomDialog.showErrorDialog(
                    context = requireContext(),
                    errorMessage = viewModel.errorMessage
                )
            }
        }

        return binding.root
    }

    private fun addToCart(medicine: Medicine) {
        if (CartInfo.cartStatus == 0) {/* cart is empty => add medicine */
            viewModel.startAddToCart(
                true,
                LocalMedicine(
                    medicine.medicineId!!,
                    medicine.id,
                    medicine.name,
                    medicine.imageUrl!!,
                    medicine.price,
                    medicine.categoryName
                )
            )
        } else if (CartInfo.cartStatus == 1) {/* cart contains medicines */
            if (CartInfo.pharmacyId == PharmacyDetailsFragmentArgs.fromBundle(requireArguments()).pharmacyDetails.pharmacyId) {/* existing medicines and medicines going to be added from the same pharmacy => add medicine */
                viewModel.startAddToCart(
                    true,
                    LocalMedicine(
                        medicine.medicineId!!,
                        medicine.id,
                        medicine.name,
                        medicine.imageUrl!!,
                        medicine.price,
                        medicine.categoryName
                    )
                )
            } else { /* medicines not from the same pharmacy */
                CustomDialog.showErrorDialog(
                    context = requireContext(),
                    errorMessage = String.format(
                        getString(R.string.add_to_cart_different_pharmacies_error),
                        CartInfo.pharmacyName
                    )
                )
            }
        } else {/* cart contains another items (not medicines) */
            CustomDialog.showErrorDialog(
                context = requireContext(),
                errorMessage = getString(R.string.add_to_cart_different_items_error),
            )
        }
    }

    private fun showSearchDialog(
        pharmacyId: Int,
        pharmacyName: String,
        deliveryFees: Double,
        banner: String
    ) {
        val searchDialog = MedicineSearchDialogFragment.newInstance()
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        val prevFragment =
            requireActivity().supportFragmentManager.findFragmentByTag("searchDialog")
        if (prevFragment != null)
            fragmentTransaction.remove(prevFragment)
        fragmentTransaction.addToBackStack(null)
        /* listen to search dialog action */
        requireActivity().supportFragmentManager.setFragmentResultListener(
            "search_medicines_key",
            viewLifecycleOwner
        ) { requestKey, bundle ->
            if (requestKey == "search_medicines_key")
                findNavController().navigate(
                    PharmacyDetailsFragmentDirections.actionPharmacyDetailsFragmentToMedicinesFragment(
                        pharmacyId = pharmacyId,
                        pharmacyName = pharmacyName,
                        banner = banner,
                        query = bundle["query"] as String,
                        categoryId = bundle["categoryId"] as String,
                        deliveryFees = deliveryFees.toString()
                    )
                )
        }
        searchDialog.show(fragmentTransaction, "searchDialog")
    }

}