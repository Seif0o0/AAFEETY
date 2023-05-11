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
import androidx.recyclerview.widget.GridLayoutManager
import code_grow.afeety.app.R
import code_grow.afeety.app.activity.LoginRegisterActivity
import code_grow.afeety.app.activity.MainActivity
import code_grow.afeety.app.adapter.OnAddProductToCartClickListener
import code_grow.afeety.app.adapter.OnProductItemClickListener
import code_grow.afeety.app.adapter.ProductsAdapter
import code_grow.afeety.app.databinding.FragmentBrandInfoBinding
import code_grow.afeety.app.kot_pref.CartInfo
import code_grow.afeety.app.kot_pref.UserInfo
import code_grow.afeety.app.local_model.LocalProduct
import code_grow.afeety.app.model.Product
import code_grow.afeety.app.repository.ProductRepository
import code_grow.afeety.app.retrofit.Resource
import code_grow.afeety.app.utils.Application
import code_grow.afeety.app.utils.CustomDialog
import code_grow.afeety.app.utils.Localize
import code_grow.afeety.app.view_model.BrandInfoViewModel
import code_grow.afeety.app.view_model.BrandInfoViewModelFactory

class BrandInfoFragment : Fragment() {
    private lateinit var binding: FragmentBrandInfoBinding
    private lateinit var viewModel: BrandInfoViewModel
    private lateinit var product: Product

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        binding = FragmentBrandInfoBinding.inflate(inflater, container, false)
        val brand = BrandInfoFragmentArgs.fromBundle(requireArguments()).brand
        val viewModelFactory = BrandInfoViewModelFactory(
            requireActivity().application,
            brand.brandId,
            ProductRepository(),
            (requireActivity().application as Application).productRepo
        )
        viewModel = ViewModelProvider(this, viewModelFactory)[BrandInfoViewModel::class.java]
        binding.viewModel = viewModel
        binding.brand = brand
        binding.lifecycleOwner = requireActivity()

        val activity = requireActivity() as MainActivity
        activity.hideBottomNav(true)

        binding.navigateBack.setOnClickListener {
            findNavController().popBackStack()
        }

        val loginLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    addToCart(product)
                }
            }

        val productsAdapter = ProductsAdapter(OnProductItemClickListener { },
            OnAddProductToCartClickListener {
                if (UserInfo.userId == 0) {
                    product = it
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
        binding.productsList.adapter = productsAdapter
        binding.productsList.layoutManager =
            GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)

        viewModel.startRequestProducts.observe(viewLifecycleOwner) {
            if (it)
                viewModel.getProducts()
        }

        viewModel.productsResponse.observe(viewLifecycleOwner) {
            if (it is Resource.Success<*>) {
                productsAdapter.submitList(it.data as MutableList<Product>)
                viewModel.startRequestProducts(false)
                viewModel.updateProductsToIdle()
            } else if (it is Resource.Failed) {
                viewModel.startRequestProducts(false)
                binding.productsErrorView.error = it.message
                binding.productsErrorView.cancelAnimation = false
            }
        }

        binding.productsErrorView.root.setOnClickListener {
            binding.productsErrorView.root.visibility = View.GONE
            binding.productsErrorView.cancelAnimation = true
            viewModel.startRequestProducts(true)
        }


        viewModel.startAddToCart.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.addToCart()
            }
        }

        viewModel.addToCartResponse.observe(viewLifecycleOwner) {
            if (it == 1) {
                CustomDialog.showSuccessDialog(
                    context = requireContext(),
                    successMessage = getString(R.string.add_to_cart_success_message),
                    navController = null
                )
                viewModel.startAddToCart(false, null)
            } else if (it == 2) {
                CustomDialog.showErrorDialog(
                    context = requireContext(),
                    errorMessage = viewModel.errorMessage
                )
                viewModel.startAddToCart(false, null)
            }
        }
        return binding.root
    }

    private fun addToCart(product: Product) {
        when (CartInfo.cartStatus) {
            0 -> {/* cart is empty => add product */
                viewModel.startAddToCart(
                    true,
                    LocalProduct(
                        product.productId,
                        famousId = 0,/* famous id */
                        famousName="",
                        product.name,
                        product.image,
                        product.price,
                        product.categoryName
                    )
                )
            }
            2 -> {/* cart contains products */
                viewModel.startAddToCart(
                    true,
                    LocalProduct(
                        product.productId,
                        famousId = 0,/* famous id */
                        famousName="",
                        product.name,
                        product.image,
                        product.price,
                        product.categoryName
                    )
                )
            }
            else -> {/* cart contains medicines */
                CustomDialog.showErrorDialog(
                    context = requireContext(),
                    errorMessage = getString(R.string.add_to_cart_different_items_error),
                )
            }
        }
    }


}