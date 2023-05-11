package code_grow.afeety.app.fragment

import android.app.Activity
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
import code_grow.afeety.app.adapter.CartProductsAdapter
import code_grow.afeety.app.adapter.OnCartProductItemClickListener
import code_grow.afeety.app.databinding.BuySuccessLayoutBinding
import code_grow.afeety.app.databinding.FragmentProductOrderDetailsBinding
import code_grow.afeety.app.repository.CartRepository
import code_grow.afeety.app.retrofit.Resource
import code_grow.afeety.app.utils.Application
import code_grow.afeety.app.utils.CustomDialog
import code_grow.afeety.app.utils.Localize
import code_grow.afeety.app.view_model.ProductOrderDetailsViewModel
import code_grow.afeety.app.view_model.ProductOrderDetailsViewModelFactory
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.ModalDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView

class ProductOrderDetailsFragment : Fragment() {
    private lateinit var binding: FragmentProductOrderDetailsBinding
    private lateinit var viewModel: ProductOrderDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        binding = FragmentProductOrderDetailsBinding.inflate(inflater, container, false)

        val args = ProductOrderDetailsFragmentArgs.fromBundle(requireArguments())
        val products = args.products
        val addressId = args.addressId
        val addressDetails = args.addressDetails
        val deliveryFees = args.deliveryFees
        val viewModelFactory = ProductOrderDetailsViewModelFactory(
            products = products.toMutableList(),
            addressId = addressId,
            deliveryFees = deliveryFees,
            application = requireActivity().application,
            repo = CartRepository(),
            (requireActivity().application as Application).productRepo
        )
        viewModel =
            ViewModelProvider(this, viewModelFactory)[ProductOrderDetailsViewModel::class.java]
        binding.viewModel = viewModel
        binding.address = addressDetails
        val subtotal = products.sumOf {
            it.price
        }

        binding.total = subtotal.plus(deliveryFees.toDouble())
        binding.delivery = deliveryFees.toDouble()
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


        val cartProductsAdapter = CartProductsAdapter(
            removeVisibility = View.INVISIBLE,
            clickListener = OnCartProductItemClickListener {})
        cartProductsAdapter.submitList(products.toMutableList())
        binding.list.adapter = cartProductsAdapter
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
            requireActivity().setResult(Activity.RESULT_OK, intent)
            requireActivity().finish()
        }

        binding.myOrdersButton.setOnClickListener {
            dialog.dismiss()
            val intent = Intent()
            intent.putExtra("goHome", false)
            requireActivity().setResult(Activity.RESULT_OK, intent)
            requireActivity().finish()
        }

    }
}