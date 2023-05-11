package code_grow.afeety.app.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import code_grow.afeety.app.R
import code_grow.afeety.app.adapter.CartMedicinesAdapter
import code_grow.afeety.app.adapter.OnCartMedicineItemClickListener
import code_grow.afeety.app.databinding.BuySuccessLayoutBinding
import code_grow.afeety.app.databinding.FragmentOrderDetailsBinding
import code_grow.afeety.app.kot_pref.CartInfo
import code_grow.afeety.app.repository.CartRepository
import code_grow.afeety.app.retrofit.Resource
import code_grow.afeety.app.utils.Application
import code_grow.afeety.app.utils.CustomDialog
import code_grow.afeety.app.utils.Localize
import code_grow.afeety.app.view_model.OrderDetailsViewModel
import code_grow.afeety.app.view_model.OrderDetailsViewModelFactory
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.ModalDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView

class OrderDetailsFragment : Fragment() {
    private lateinit var binding: FragmentOrderDetailsBinding
    private lateinit var viewModel: OrderDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        binding = FragmentOrderDetailsBinding.inflate(inflater, container, false)
        val args = OrderDetailsFragmentArgs.fromBundle(requireArguments())
        val medicines = args.medicines
        val addressId = args.addressId
        val addressDetails = args.addressDetails
        val viewModelFactory = OrderDetailsViewModelFactory(
            medicines = medicines.toMutableList(),
            addressId = addressId,
            application = requireActivity().application,
            repo = CartRepository(),
            (requireActivity().application as Application).medicineRepo
        )
        viewModel =
            ViewModelProvider(this, viewModelFactory)[OrderDetailsViewModel::class.java]
        binding.viewModel = viewModel
        binding.address = addressDetails
        val subtotal = medicines.sumOf {
            it.price
        }

        binding.total = subtotal.plus(CartInfo.deliveryFees.toDouble())
        binding.delivery = CartInfo.deliveryFees.toDouble()
        binding.subTotalPrice = subtotal
        binding.lifecycleOwner = requireActivity()

        binding.navigateBack.setOnClickListener {
            findNavController().popBackStack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            })

        binding.submitButton.setOnClickListener {
            viewModel.startBuying(true)
        }


        val cartMedicineAdapter = CartMedicinesAdapter(
            removeVisibility = View.INVISIBLE,
            clickListener = OnCartMedicineItemClickListener {})
        cartMedicineAdapter.submitList(medicines.toMutableList())
        binding.list.adapter = cartMedicineAdapter
        binding.list.layoutManager = LinearLayoutManager(requireContext())


        viewModel.startBuying.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.buy()
            }
        }

        viewModel.buyingResponse.observe(viewLifecycleOwner) {
            viewModel.startBuying(false)
            if (it is Resource.Success<*>) {
                showSuccessDialog()
            } else if (it is Resource.Failed) {
                CustomDialog.showErrorDialog(
                    context = requireContext(),
                    errorMessage = it.message
                )
            }
        }

        return binding.root
    }

    private fun showSuccessDialog() {
        val dialog = MaterialDialog(requireContext(), ModalDialog).show {
            cornerRadius(res = com.intuit.sdp.R.dimen._8sdp)
            cancelOnTouchOutside(true)
            cancelable(false)
            customView(
                R.layout.buy_success_layout,
                noVerticalPadding = true,
                dialogWrapContent = true
            )
        }

        val binding = BuySuccessLayoutBinding.bind(dialog.getCustomView())

        binding.confirmationMessage.text = getString(R.string.buy_success_message_1)
        binding.homeButton.setOnClickListener {
            dialog.dismiss()
            val intent = Intent()
            intent.putExtra("goHome", true)
            requireActivity().setResult(RESULT_OK, intent)
            requireActivity().finish()
        }

        binding.myOrdersButton.setOnClickListener {
            dialog.dismiss()
            val intent = Intent()
            intent.putExtra("goHome", false)
            requireActivity().setResult(RESULT_OK, intent)
            requireActivity().finish()
        }

    }
}