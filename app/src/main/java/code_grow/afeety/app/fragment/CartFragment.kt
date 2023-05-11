package code_grow.afeety.app.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import code_grow.afeety.app.adapter.CartMedicinesAdapter
import code_grow.afeety.app.adapter.OnCartMedicineItemClickListener
import code_grow.afeety.app.databinding.FragmentCartBinding
import code_grow.afeety.app.kot_pref.CartInfo
import code_grow.afeety.app.utils.Localize
import code_grow.afeety.app.view_model.CartViewModel
import code_grow.afeety.app.view_model.CartViewModelFactory

private const val TAG = "CartFragment"

class CartFragment : Fragment() {
    private lateinit var binding: FragmentCartBinding
    private lateinit var viewModel: CartViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        binding = FragmentCartBinding.inflate(inflater, container, false)
        val viewModelFactory = CartViewModelFactory(
            requireActivity().application,
            (requireActivity().application as code_grow.afeety.app.utils.Application).medicineRepo
        )
        viewModel = ViewModelProvider(this, viewModelFactory)[CartViewModel::class.java]
        binding.itemsPrice = 0.0
        Log.d(TAG, "delivery: ${CartInfo.deliveryFees}")

        binding.navigateBack.setOnClickListener {
            requireActivity().finish()
        }

        binding.continueButton.setOnClickListener {
            findNavController().navigate(
                CartFragmentDirections.actionCartFragmentToAddressesFragment((binding.list.adapter as CartMedicinesAdapter).currentList.toTypedArray())
            )
        }

        val cartMedicineAdapter =
            CartMedicinesAdapter(clickListener = OnCartMedicineItemClickListener {
                viewModel.removeMedicine(it.id)
            })
        binding.list.adapter = cartMedicineAdapter
        binding.list.layoutManager = LinearLayoutManager(requireContext())

        /* once creating CartFragment allMedicines become null so we have to save CartInfo before observing allMedicines */
        val deliveryFees = CartInfo.deliveryFees
        val cartStatus = CartInfo.cartStatus
        val pharmacyId = CartInfo.pharmacyId
        val pharmacyName = CartInfo.pharmacyName
        viewModel.allMedicines.observe(viewLifecycleOwner) {
            if (it.isNullOrEmpty()) {
                Log.d(TAG, "AllMedicinesNullOrEmpty")
                binding.emptyCartText.visibility = View.VISIBLE
                binding.list.visibility = View.GONE
                binding.continueButton.visibility = View.GONE
                binding.itemsPrice = 0.0

                CartInfo.cartStatus = 0
                CartInfo.pharmacyId = 0
                CartInfo.deliveryFees = "0"
                CartInfo.pharmacyName = ""

            } else {
                binding.list.visibility = View.VISIBLE
                binding.emptyCartText.visibility = View.GONE
                binding.continueButton.visibility = View.VISIBLE
                CartInfo.deliveryFees = deliveryFees
                CartInfo.cartStatus = cartStatus
                CartInfo.pharmacyId = pharmacyId
                CartInfo.pharmacyName = pharmacyName
                binding.itemsPrice = it.sumOf { list ->
                    list.price
                }.plus(deliveryFees.toDouble())
                cartMedicineAdapter.submitList(it)
            }
        }

        return binding.root
    }
}