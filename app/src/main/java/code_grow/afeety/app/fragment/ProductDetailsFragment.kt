package code_grow.afeety.app.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import code_grow.afeety.app.R
import code_grow.afeety.app.activity.CartActivity
import code_grow.afeety.app.activity.LoginRegisterActivity
import code_grow.afeety.app.activity.MainActivity
import code_grow.afeety.app.activity.ProductCartActivity
import code_grow.afeety.app.databinding.FragmentProductDetailsBinding
import code_grow.afeety.app.kot_pref.CartInfo
import code_grow.afeety.app.kot_pref.UserInfo
import code_grow.afeety.app.utils.Localize

class ProductDetailsFragment : Fragment() {
    private lateinit var binding: FragmentProductDetailsBinding
    private var prevFragment: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        binding = FragmentProductDetailsBinding.inflate(inflater, container, false)
        val product = ProductDetailsFragmentArgs.fromBundle(requireArguments()).productDetails
        binding.product = product

        prevFragment = ProductDetailsFragmentArgs.fromBundle(requireArguments()).flag

        // hide bottom navigation while creating this fragment
        val activity = requireActivity() as MainActivity
        activity.hideBottomNav(true)



        binding.navigateBack.setOnClickListener {
            findNavController().popBackStack()
        }


        val cartLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    var goHome = it.data?.getBooleanExtra("goHome", true) ?: true
                    if (goHome) {
                        findNavController().navigate(ProductDetailsFragmentDirections.actionProductDetailsFragmentToHomeFragment())
                    } else {
                        findNavController().navigate(ProductDetailsFragmentDirections.actionProductDetailsFragmentToMyOrdersFragment(1))
                    }

                }
            }

        val productCartLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    var goHome = it.data?.getBooleanExtra("goHome", true) ?: true
                    if (goHome) {
                        findNavController().navigate(ProductDetailsFragmentDirections.actionProductDetailsFragmentToHomeFragment())
                    } else {
                        when (prevFragment) {
                            0 -> {
                                findNavController().navigate(ProductDetailsFragmentDirections.actionProductDetailsFragmentToMyOrdersFragment(1))
                            }
                            1 -> {
                                val options =
                                    NavOptions.Builder().setPopUpTo(R.id.famousFragment, true)
                                findNavController().navigate(
                                    ProductDetailsFragmentDirections.actionProductDetailsFragmentToMyOrdersFragment(
                                        2
                                    ), options.build()
                                )
                            }
                            2 -> {
                                val options =
                                    NavOptions.Builder().setPopUpTo(R.id.famousInfoFragment, true)
                                findNavController().navigate(
                                    ProductDetailsFragmentDirections.actionProductDetailsFragmentToMyOrdersFragment(
                                        2
                                    ), options.build()
                                )
                            }
                        }

                    }
                }
            }

        val cartLoginLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    when (CartInfo.cartStatus) {
                        0,1 -> cartLauncher.launch(Intent(requireContext(), CartActivity::class.java))
                        2, 3 -> productCartLauncher.launch(
                            Intent(
                                requireContext(),
                                ProductCartActivity::class.java
                            )
                        )
                    }
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
                when (CartInfo.cartStatus) {
                    0,1 -> cartLauncher.launch(Intent(requireContext(), CartActivity::class.java))
                    2, 3 -> productCartLauncher.launch(
                        Intent(
                            requireContext(),
                            ProductCartActivity::class.java
                        )
                    )

                }
            }
        }


        return binding.root
    }
}