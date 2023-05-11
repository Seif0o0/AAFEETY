package code_grow.afeety.app.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import code_grow.afeety.app.R
import code_grow.afeety.app.activity.CartActivity
import code_grow.afeety.app.activity.LoginRegisterActivity
import code_grow.afeety.app.activity.MainActivity
import code_grow.afeety.app.activity.ProductCartActivity
import code_grow.afeety.app.adapter.*
import code_grow.afeety.app.databinding.FragmentProductsBinding
import code_grow.afeety.app.kot_pref.CartInfo
import code_grow.afeety.app.kot_pref.UserInfo
import code_grow.afeety.app.local_model.LocalProduct
import code_grow.afeety.app.model.*
import code_grow.afeety.app.repository.ProductRepository
import code_grow.afeety.app.retrofit.Resource
import code_grow.afeety.app.utils.Application
import code_grow.afeety.app.utils.CustomDialog
import code_grow.afeety.app.utils.Localize
import code_grow.afeety.app.view_model.ProductsViewModel
import code_grow.afeety.app.view_model.ProductsViewModelFactory

class ProductsFragment : Fragment() {
    private lateinit var binding: FragmentProductsBinding
    private lateinit var viewModel: ProductsViewModel
    private var categoryId = 0;
    private lateinit var product: Product

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        binding = FragmentProductsBinding.inflate(inflater, container, false)

        val viewModelFactory = ProductsViewModelFactory(
            requireActivity().application,
            ProductRepository(),
            (requireActivity().application as Application).productRepo
        )
        viewModel = ViewModelProvider(this, viewModelFactory)[ProductsViewModel::class.java]
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
        binding.productsViewPager.adapter = sliderAdapter

        viewModel.startRequestSlider.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.getSlider()
            }
        }

        viewModel.sliderResponse.observe(viewLifecycleOwner) {

            if (it is Resource.Failed) {
                viewModel.setStartRequestSlider(false)
                binding.sliderErrorView.error = it.message
                binding.sliderErrorView.cancelAnimation = false
            } else if (it is Resource.Success<*>) {
                viewModel.setStartRequestSlider(false)
                sliderAdapter.submitList(it.data as MutableList<SliderItem>)
//                viewModel.updateSlidersToIdle()
            }
        }

        binding.sliderErrorView.root.setOnClickListener {
            binding.sliderErrorView.root.visibility = View.GONE
            binding.sliderErrorView.cancelAnimation = true
            viewModel.setStartRequestSlider(true)
        }


        val categoriesAdapter = ProductCategoryAdapter(OnProductCategoryItemCLickListener {
            val adapter = binding.categoriesList.adapter as ProductCategoryAdapter
            val categoriesList = adapter.currentList.toMutableList()


            for (index in 0 until categoriesList.size) {
                if (categoriesList[index].clicked) {
                    categoriesList[index].clicked = false
                    adapter.submitList(categoriesList)
                    adapter.notifyItemChanged(index, Unit)
                }

                if (categoriesList[index].categoryId == it.categoryId) {
                    categoriesList[index].clicked = true
                    adapter.submitList(categoriesList)
                    adapter.notifyItemChanged(index, Unit)
                }
            }

            categoryId = it.categoryId
            viewModel.updateProductsQueryMap(categoryId, binding.searchEditText.text.toString())
        })
        binding.categoriesList.adapter = categoriesAdapter
        binding.categoriesList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        viewModel.startRequestCategories.observe(viewLifecycleOwner) {
            if (it)
                viewModel.getCategories()
        }

        viewModel.categoriesResponse.observe(viewLifecycleOwner) {
            if (it is Resource.Failed) {
                binding.categoriesErrorView.error = it.message
                binding.categoriesErrorView.cancelAnimation = false
                viewModel.startRequestCategories(false)
            } else if (it is Resource.Success<*>) {
                categoriesAdapter.submitList(it.data as MutableList<ProductCategory>)
                viewModel.startRequestProducts(true)
//                viewModel.updateCategoriesToIdle()
                viewModel.startRequestCategories(false)
            }
        }

        binding.categoriesErrorView.root.setOnClickListener {
            binding.categoriesErrorView.root.visibility = View.GONE
            binding.categoriesErrorView.cancelAnimation = true
            viewModel.startRequestCategories(true)
        }

        val loginLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    addToCart(product)
                }
            }

        val productsAdapter =
            ProductsAdapter(OnProductItemClickListener {
                findNavController().navigate(
                    ProductsFragmentDirections.actionProductsListFragmentToProductDetailsFragment(it)
                )
            }, OnAddProductToCartClickListener {
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
                viewModel.updateProductsToIdle()
                viewModel.startRequestProducts(false)
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

        binding.searchEditText.tag = "false";
        binding.searchEditText.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus -> binding.searchEditText.tag = "$hasFocus" }



        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.searchEditText.tag.toString()
                        .lowercase() == "true"
                ) viewModel.updateProductsQueryMap(categoryId, s.toString())

            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        viewModel.startAddToCart.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.addToCart()
            }
        }

        var currentCartStatus = CartInfo.cartStatus
        viewModel.addToCartResponse.observe(viewLifecycleOwner) {
            if (it == 1) {
                CustomDialog.showSuccessDialog(
                    context = requireContext(),
                    successMessage = getString(R.string.add_to_cart_success_message),
                    navController = null
                )
                currentCartStatus = CartInfo.cartStatus
                viewModel.startAddToCart(false, null)
            } else if (it == 2) {
                CustomDialog.showErrorDialog(
                    context = requireContext(),
                    errorMessage = viewModel.errorMessage
                )
                viewModel.startAddToCart(false, null)
            }
        }


        val cartLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val goHome = it.data?.getBooleanExtra("goHome", true) ?: true
                    if (goHome) {
                        findNavController().popBackStack()
                    } else {
                        findNavController().navigate(
                            ProductsFragmentDirections.actionProductsListFragmentToMyOrdersFragment(
                                if (currentCartStatus == 0 || currentCartStatus == 1) 0 else currentCartStatus - 1
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
                        famousName = "",
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
                        famousName = "",
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