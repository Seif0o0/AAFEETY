package code_grow.afeety.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import code_grow.afeety.app.adapter.CartProductsAdapter
import code_grow.afeety.app.adapter.OnCartProductItemClickListener
import code_grow.afeety.app.databinding.FragmentProductCartBinding
import code_grow.afeety.app.kot_pref.CartInfo
import code_grow.afeety.app.utils.Application
import code_grow.afeety.app.utils.Localize
import code_grow.afeety.app.view_model.ProductCartViewModel
import code_grow.afeety.app.view_model.ProductCartViewModelFactory

class ProductCartFragment : Fragment() {
    private lateinit var binding: FragmentProductCartBinding
    private lateinit var viewModel: ProductCartViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        binding = FragmentProductCartBinding.inflate(inflater, container, false)
        val viewModelFactory = ProductCartViewModelFactory(
            requireActivity().application,
            (requireActivity().application as Application).productRepo
        )
        viewModel = ViewModelProvider(this, viewModelFactory)[ProductCartViewModel::class.java]
        binding.itemsPrice = 0.0

        binding.navigateBack.setOnClickListener {
            requireActivity().finish()
        }

        binding.continueButton.setOnClickListener {

            findNavController().navigate(
                ProductCartFragmentDirections.actionProductCartFragmentToProductAddressesFragment((binding.list.adapter as CartProductsAdapter).currentList.toTypedArray())
            )
        }

        val cartProductAdapter =
            CartProductsAdapter(clickListener = OnCartProductItemClickListener {
                viewModel.removeProduct(it.productId)
            })
        binding.list.adapter = cartProductAdapter
        binding.list.layoutManager = LinearLayoutManager(requireContext())

        /* once creating CartFragment allProducts become null so we have to save CartInfo before observing allProducts */
        val cartStatus = CartInfo.cartStatus
        val deliveryFees = CartInfo.deliveryFees
        viewModel.allProducts.observe(viewLifecycleOwner) {
            if (it.isNullOrEmpty()) {
                binding.emptyCartText.visibility = View.VISIBLE
                binding.list.visibility = View.GONE
                binding.continueButton.visibility = View.GONE
                binding.itemsPrice = 0.0

                CartInfo.deliveryFees = "0"
                CartInfo.cartStatus = 0
            } else {
                binding.list.visibility = View.VISIBLE
                binding.emptyCartText.visibility = View.GONE
                binding.continueButton.visibility = View.VISIBLE
                CartInfo.cartStatus = cartStatus
                CartInfo.deliveryFees = deliveryFees

                binding.itemsPrice = it.sumOf { list ->
                    list.price
                }.plus(deliveryFees.toDouble())
                cartProductAdapter.submitList(it)
            }
        }

        return binding.root
    }
}