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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import code_grow.afeety.app.R
import code_grow.afeety.app.activity.CartActivity
import code_grow.afeety.app.activity.LoginRegisterActivity
import code_grow.afeety.app.activity.MainActivity
import code_grow.afeety.app.activity.ProductCartActivity
import code_grow.afeety.app.adapter.*
import code_grow.afeety.app.databinding.FragmentFamousBinding
import code_grow.afeety.app.kot_pref.CartInfo
import code_grow.afeety.app.kot_pref.UserInfo
import code_grow.afeety.app.local_model.LocalProduct
import code_grow.afeety.app.model.Famous
import code_grow.afeety.app.model.Product
import code_grow.afeety.app.repository.ProductRepository
import code_grow.afeety.app.retrofit.Resource
import code_grow.afeety.app.utils.Application
import code_grow.afeety.app.utils.CustomDialog
import code_grow.afeety.app.utils.Localize
import code_grow.afeety.app.view_model.FamousViewModel
import code_grow.afeety.app.view_model.FamousViewModelFactory

class FamousFragment : Fragment() {
    private lateinit var binding: FragmentFamousBinding
    private lateinit var viewModel: FamousViewModel
    private lateinit var product: Product

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        binding = FragmentFamousBinding.inflate(inflater, container, false)
        val viewModelFactory = FamousViewModelFactory(
            requireActivity().application,
            ProductRepository(),
            (requireActivity().application as Application).productRepo
        )

        viewModel = ViewModelProvider(this, viewModelFactory)[FamousViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()

        // hide bottom navigation while creating this fragment
        val activity = requireActivity() as MainActivity
        activity.hideBottomNav(true)

        binding.navigateBack.setOnClickListener {
            findNavController().popBackStack()
        }
        if (viewModel.query["gender"] == "1") {
            binding.menText.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.famous_main_color
                )
            )
            binding.menLine.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.famous_main_color
                )
            )
            binding.womenText.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.famous_hint_color
                )
            )
            binding.womenLine.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    android.R.color.transparent
                )
            )
        } else {
            binding.menText.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.famous_hint_color
                )
            )
            binding.menLine.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    android.R.color.transparent
                )
            )
            binding.womenText.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.famous_main_color
                )
            )
            binding.womenLine.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.famous_main_color
                )
            )
        }

        binding.menText.setOnClickListener {
            if (viewModel.query["gender"] == "1") {
                return@setOnClickListener
            }
            binding.menText.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.famous_main_color
                )
            )
            binding.menLine.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.famous_main_color
                )
            )
            binding.womenText.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.famous_hint_color
                )
            )
            binding.womenLine.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    android.R.color.transparent
                )
            )
            viewModel.query["gender"] = "1"
            viewModel.query.remove("search")
            binding.searchEditText.setText("")
            viewModel.startRequestFamous(true)
            viewModel.startRequestProducts(true)
        }

        binding.womenText.setOnClickListener {
            if (viewModel.query["gender"] == "0") {
                return@setOnClickListener
            }
            binding.menText.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.famous_hint_color
                )
            )
            binding.menLine.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    android.R.color.transparent
                )
            )
            binding.womenText.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.famous_main_color
                )
            )
            binding.womenLine.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.famous_main_color
                )
            )
            viewModel.query["gender"] = "0"
            viewModel.query.remove("search")
            binding.searchEditText.setText("")
            viewModel.startRequestFamous(true)
            viewModel.startRequestProducts(true)
        }

        val famousAdapter = GridFamousAdapter(OnGridFamousItemClickListener {
            findNavController().navigate(
                FamousFragmentDirections.actionFamousFragmentToFamousInfoFragment(
                    it,
                    fromHome = false
                )
            )
        })
        binding.list.adapter = famousAdapter
        binding.list.layoutManager =
            GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)


        viewModel.startRequestFamous.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.getFamousList()
            }
        }

        viewModel.famousResponse.observe(viewLifecycleOwner) {

            if (it is Resource.Success<*>) {
                famousAdapter.submitList(it.data as MutableList<Famous>)
                binding.list.visibility = View.VISIBLE
                viewModel.startRequestFamous(false)
//                viewModel.changeResponseToIdle()
            } else if (it is Resource.Empty) {
                binding.list.visibility = View.INVISIBLE
                viewModel.startRequestFamous(false)
            } else if (it is Resource.Failed) {
                binding.errorView.error = it.message
                binding.errorView.cancelAnimation = false
                viewModel.startRequestFamous(false)
            }
        }

        binding.errorView.root.setOnClickListener {
            binding.errorView.cancelAnimation = true
            viewModel.startRequestFamous(true)
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
                ) viewModel.startSearch(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })


        val loginLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    addToCart(product)
                }
            }

        val productsAdapter = ProductsAdapter(OnProductItemClickListener {
            findNavController().navigate(
                FamousFragmentDirections.actionFamousFragmentToProductDetailsFragment(
                    productDetails = it,
                    flag = 1
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
            if (it == 1) {
                CustomDialog.showSuccessDialog(
                    context = requireContext(),
                    successMessage = getString(R.string.add_to_cart_success_message),
                    navController = null
                )
                currentCartStatus = CartInfo.cartStatus
                viewModel.convertAddToCartToIdle()
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
                            FamousFragmentDirections.actionFamousFragmentToMyOrdersFragment(
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
                        product.influencerId!!,
                        product.influencerName!!,
                        product.name,
                        product.image,
                        product.price,
                        product.categoryName
                    )
                )
            }
            3 -> {/* cart contains famous products */
                viewModel.startAddToCart(
                    true,
                    LocalProduct(
                        product.productId,
                        product.influencerId!!,
                        product.influencerName!!,
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

    companion object {
        @JvmStatic
        fun newInstance() = FamousFragment()
    }

}