package code_grow.afeety.app.fragment

import android.app.Activity
import android.content.Intent
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
import code_grow.afeety.app.activity.LoginRegisterActivity
import code_grow.afeety.app.activity.MainActivity
import code_grow.afeety.app.adapter.MedicineAdapter
import code_grow.afeety.app.adapter.OnMedicineItemClickListener
import code_grow.afeety.app.databinding.FragmentMedicinesBinding
import code_grow.afeety.app.kot_pref.CartInfo
import code_grow.afeety.app.kot_pref.UserInfo
import code_grow.afeety.app.local_model.LocalMedicine
import code_grow.afeety.app.model.Medicine
import code_grow.afeety.app.repository.PharmaciesRepository
import code_grow.afeety.app.retrofit.Resource
import code_grow.afeety.app.utils.Application
import code_grow.afeety.app.utils.CustomDialog
import code_grow.afeety.app.utils.Localize
import code_grow.afeety.app.view_model.MedicinesViewModel
import code_grow.afeety.app.view_model.MedicinesViewModelFactory

private const val TAG = "MedicinesFragment"

class MedicinesFragment : Fragment() {
    private lateinit var binding: FragmentMedicinesBinding
    private lateinit var viewModel: MedicinesViewModel
    private lateinit var medicine: Medicine
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        binding = FragmentMedicinesBinding.inflate(inflater, container, false)
        val args = MedicinesFragmentArgs.fromBundle(requireArguments())

        val viewModelFactory = MedicinesViewModelFactory(
            args.pharmacyId,
            args.pharmacyName,
            args.deliveryFees.toDouble(),
            args.query,
            args.categoryId,
            requireActivity().application,
            PharmaciesRepository(),
            (requireActivity().application as Application).medicineRepo
        )

        viewModel = ViewModelProvider(this, viewModelFactory)[MedicinesViewModel::class.java]
        binding.banner = args.banner
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()

        // hide bottom navigation while creating this fragment
        val activity = requireActivity() as MainActivity
        activity.hideBottomNav(true)


        binding.navigateBack.setOnClickListener {
            findNavController().popBackStack()
        }


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

        binding.medicinesList.adapter = medicineAdapter
        binding.medicinesList.layoutManager = LinearLayoutManager(requireContext())

        viewModel.startRequestMedicines.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.getMedicines()
            }
        }

        viewModel.medicinesResponse.observe(viewLifecycleOwner) {
            if (it is Resource.Failed) {
                binding.errorView.error = it.message
                binding.errorView.cancelAnimation = false
            } else if (it is Resource.Success<*>) {
                medicineAdapter.submitList(it.data as MutableList<Medicine>)
            }
        }
        binding.errorView.root.setOnClickListener {
            binding.errorView.root.visibility = View.GONE
            binding.errorView.cancelAnimation = true
            viewModel.startRequestMedicines(true)
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
            if (CartInfo.pharmacyId == MedicinesFragmentArgs.fromBundle(requireArguments()).pharmacyId) {/* existing medicines and medicines going to be added from the same pharmacy => add medicine */
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
        }
    }
}