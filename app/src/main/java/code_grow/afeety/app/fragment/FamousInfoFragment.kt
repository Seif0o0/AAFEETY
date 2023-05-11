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
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import code_grow.afeety.app.R
import code_grow.afeety.app.activity.CartActivity
import code_grow.afeety.app.activity.LoginRegisterActivity
import code_grow.afeety.app.activity.MainActivity
import code_grow.afeety.app.activity.ProductCartActivity
import code_grow.afeety.app.adapter.OnAddProductToCartClickListener
import code_grow.afeety.app.adapter.OnProductItemClickListener
import code_grow.afeety.app.adapter.ProductsAdapter
import code_grow.afeety.app.databinding.FragmentFamousInfoBinding
import code_grow.afeety.app.kot_pref.CartInfo
import code_grow.afeety.app.kot_pref.UserInfo
import code_grow.afeety.app.local_model.LocalProduct
import code_grow.afeety.app.model.Product
import code_grow.afeety.app.repository.ProductRepository
import code_grow.afeety.app.retrofit.Resource
import code_grow.afeety.app.utils.Application
import code_grow.afeety.app.utils.CustomDialog
import code_grow.afeety.app.utils.Localize
import code_grow.afeety.app.view_model.FamousInfoViewModel
import code_grow.afeety.app.view_model.FamousInfoViewModelFactory

class FamousInfoFragment : Fragment() {
    private lateinit var binding: FragmentFamousInfoBinding
    private lateinit var viewModel: FamousInfoViewModel
    private lateinit var product: Product

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        binding = FragmentFamousInfoBinding.inflate(inflater, container, false)
        val famous = FamousInfoFragmentArgs.fromBundle(requireArguments()).famous
        val viewModelFactory = FamousInfoViewModelFactory(
            requireActivity().application,
            famous.id,
            ProductRepository(),
            (requireActivity().application as Application).productRepo
        )
        viewModel = ViewModelProvider(this, viewModelFactory)[FamousInfoViewModel::class.java]
        binding.viewModel = viewModel
        binding.famous = famous

        binding.lifecycleOwner = requireActivity()

        val activity = requireActivity() as MainActivity
        activity.hideBottomNav(true)

        binding.navigateBack.setOnClickListener {
            findNavController().popBackStack()
        }

        val loginLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    addToCart(product, famous.id, famous.name)
                }
            }

        val productsAdapter = ProductsAdapter(OnProductItemClickListener {
            findNavController().navigate(
                FamousInfoFragmentDirections.actionFamousInfoFragmentToProductDetailsFragment(
                    productDetails = it,
                    flag = 2
                )
            )
        },
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
                    addToCart(it, famous.id, famous.name)
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
//                viewModel.updateProductsToIdle()
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

        var currentCartStatus = CartInfo.cartStatus
        viewModel.addToCartResponse.observe(viewLifecycleOwner) {
            viewModel.startAddToCart(false, null)
            if (it == 1) {
                CustomDialog.showSuccessDialog(
                    context = requireContext(),
                    successMessage = getString(R.string.add_to_cart_success_message),
                    navController = null
                )
                currentCartStatus = CartInfo.cartStatus
                viewModel.convertAddToCartToIdle()
            } else if (it == 2) {
                CustomDialog.showErrorDialog(
                    context = requireContext(),
                    errorMessage = viewModel.errorMessage
                )
            }
        }

        var fromHome = FamousInfoFragmentArgs.fromBundle(requireArguments()).fromHome
        val cartLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val goHome = it.data?.getBooleanExtra("goHome", true) ?: true
                    if (fromHome) {
                        if (goHome) {
                            findNavController().popBackStack()
                        } else {
                            findNavController().navigate(
                                FamousInfoFragmentDirections.actionFamousInfoFragmentToMyOrdersFragment(
                                    if (currentCartStatus == 0 || currentCartStatus == 1) 0 else currentCartStatus - 1
                                )
                            )
                        }
                    } else {
                        if (goHome) {
                            findNavController().navigate(FamousInfoFragmentDirections.actionFamousInfoFragmentToHomeFragment())
                        } else {
                            val options =
                                NavOptions.Builder().setPopUpTo(R.id.famousFragment, true)
                            findNavController().navigate(
                                FamousInfoFragmentDirections.actionFamousInfoFragmentToMyOrdersFragment(
                                    if (currentCartStatus == 0 || currentCartStatus == 1) 0 else currentCartStatus - 1
                                ), options.build()
                            )
                        }
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

        return binding.root
    }

    private fun addToCart(product: Product, famousId: Int, famousName: String) {
        when (CartInfo.cartStatus) {
            0 -> {/* cart is empty => add product */
                viewModel.startAddToCart(
                    true,
                    LocalProduct(
                        product.productId,
                        famousId = famousId,
                        famousName = famousName,
                        product.name,
                        product.image,
                        product.price,
                        product.categoryName
                    )
                )
            }
            3 -> {/* cart contains products */
                viewModel.startAddToCart(
                    true,
                    LocalProduct(
                        product.productId,
                        famousId = famousId,
                        famousName = famousName,
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