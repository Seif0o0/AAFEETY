package code_grow.afeety.app.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import code_grow.afeety.app.activity.CartActivity
import code_grow.afeety.app.activity.LoginRegisterActivity
import code_grow.afeety.app.activity.MainActivity
import code_grow.afeety.app.activity.ProductCartActivity
import code_grow.afeety.app.databinding.FragmentMoreBinding
import code_grow.afeety.app.kot_pref.CartInfo
import code_grow.afeety.app.kot_pref.UserInfo
import code_grow.afeety.app.utils.Application
import code_grow.afeety.app.utils.Localize
import kotlinx.coroutines.launch

class MoreFragment : Fragment() {
    private lateinit var binding: FragmentMoreBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        binding = FragmentMoreBinding.inflate(inflater, container, false)
        setUserInfo()
        // hide bottom navigation while creating this fragment
        val activity = requireActivity() as MainActivity
        activity.hideBottomNav(true)

        binding.logoutIcon.setOnClickListener {
            UserInfo.isSigned = false
            UserInfo.userId = 0
            UserInfo.username = ""
            UserInfo.password = ""
            UserInfo.email = ""
            UserInfo.phoneNumber = ""
            UserInfo.profilePicture = ""
            UserInfo.gender = 1
            UserInfo.cityId = 0
            UserInfo.longitude = ""
            UserInfo.latitude = ""
            UserInfo.address = ""
            UserInfo.accessToken = ""
            UserInfo.age = 0

            CartInfo.cartStatus = 0
            CartInfo.pharmacyId = 0
            CartInfo.pharmacyName = ""
            CartInfo.deliveryFees = "0"

            var productRepo = (requireActivity().application as Application).productRepo
            var medicineRepo = (requireActivity().application as Application).medicineRepo

            lifecycleScope.launch {
                productRepo.removeAllProducts()
                medicineRepo.removeAllMedicines()
            }

            requireActivity().finish()
            startActivity(Intent(requireActivity(), LoginRegisterActivity::class.java).apply {
                putExtra("hideGuestLogin", false)
            })
        }

        binding.homeHeader.setOnClickListener { findNavController().popBackStack() }
        binding.homeIcon.setOnClickListener { findNavController().popBackStack() }

        val myReservationLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    setUserInfo()
                    navigateTo(MoreFragmentDirections.actionMoreFragmentToMyReservationsFragment())
                }
            }

        binding.myReservationsHeader.setOnClickListener {
            launchActivity(
                myReservationLauncher,
                MoreFragmentDirections.actionMoreFragmentToMyReservationsFragment()
            )
        }
        binding.myReservationsIcon.setOnClickListener {
            launchActivity(
                myReservationLauncher,
                MoreFragmentDirections.actionMoreFragmentToMyReservationsFragment()
            )
        }

        val myOrdersLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    navigateTo(MoreFragmentDirections.actionMoreFragmentToMyOrdersFragment())
                }
            }

        binding.myOrdersHeader.setOnClickListener {
            launchActivity(
                myOrdersLauncher,
                MoreFragmentDirections.actionMoreFragmentToMyOrdersFragment()
            )
        }
        binding.myOrdersIcon.setOnClickListener {
            launchActivity(
                myOrdersLauncher,
                MoreFragmentDirections.actionMoreFragmentToMyOrdersFragment()
            )
        }

        val myCouponsLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    navigateTo(MoreFragmentDirections.actionMoreFragmentToMyCouponsFragment())
                }
            }

        binding.myCouponsHeader.setOnClickListener {
            launchActivity(
                myCouponsLauncher,
                MoreFragmentDirections.actionMoreFragmentToMyCouponsFragment()
            )
        }
        binding.myCouponsIcon.setOnClickListener {
            launchActivity(
                myCouponsLauncher,
                MoreFragmentDirections.actionMoreFragmentToMyCouponsFragment()
            )
        }


        val myFilesLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    setUserInfo()
                    navigateTo(MoreFragmentDirections.actionMoreFragmentToMyMedicalFilesFragment())
                }
            }

        binding.myMedicalFilesHeader.setOnClickListener {
            launchActivity(
                myFilesLauncher,
                MoreFragmentDirections.actionMoreFragmentToMyMedicalFilesFragment()
            )
        }
        binding.myMedicalFilesIcon.setOnClickListener {
            launchActivity(
                myFilesLauncher,
                MoreFragmentDirections.actionMoreFragmentToMyMedicalFilesFragment()
            )
        }


        binding.notificationsHeader.setOnClickListener { }
        binding.notificationsIcon.setOnClickListener { }

        binding.favouritesHeader.setOnClickListener { }
        binding.favouritesIcon.setOnClickListener { }

        val currentCartStatus = CartInfo.cartStatus
        val cartLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    var goHome = it.data?.getBooleanExtra("goHome", true) ?: true
                    if (!goHome) {
                        findNavController().navigate(
                            MoreFragmentDirections.actionMoreFragmentToMyOrdersFragment(
                                if (currentCartStatus == 0 || currentCartStatus == 1) 0 else currentCartStatus - 1
                            )
                        )
                    } else {
                        findNavController().popBackStack()
                    }

                }
            }

        val cartLoginLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    cartLauncher.launch(
                        Intent(
                            requireContext(),
                            if (CartInfo.cartStatus == 0 || CartInfo.cartStatus == 1) CartActivity::class.java else ProductCartActivity::class.java
                        )
                    )
                }
            }

        binding.cartHeader.setOnClickListener { navigateToCart(cartLoginLauncher,cartLauncher) }
        binding.cartIcon.setOnClickListener { navigateToCart(cartLoginLauncher,cartLauncher) }

        val profileLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    setUserInfo()
                    navigateTo(MoreFragmentDirections.actionMoreFragmentToEditProfileFragment())
                }
            }
        binding.profileHeader.setOnClickListener {
            launchActivity(
                profileLauncher,
                MoreFragmentDirections.actionMoreFragmentToEditProfileFragment()
            )
        }
        binding.profileIcon.setOnClickListener {
            launchActivity(
                profileLauncher,
                MoreFragmentDirections.actionMoreFragmentToEditProfileFragment()
            )
        }

        binding.aboutUsHeader.setOnClickListener { }
        binding.aboutUsIcon.setOnClickListener { }

        binding.termsConditionsHeader.setOnClickListener { }
        binding.termsConditionsIcon.setOnClickListener { }

        val contactUsLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    setUserInfo()
                    navigateTo(MoreFragmentDirections.actionMoreFragmentToContactUsFragment())
                }
            }

        binding.contactUsHeader.setOnClickListener {
            launchActivity(
                contactUsLauncher,
                MoreFragmentDirections.actionMoreFragmentToContactUsFragment()
            )
        }
        binding.contactUsIcon.setOnClickListener {
            launchActivity(
                contactUsLauncher,
                MoreFragmentDirections.actionMoreFragmentToContactUsFragment()
            )
        }

        binding.loginIcon.setOnClickListener {
            navigateLogin()
        }
        binding.loginHeader.setOnClickListener {
            navigateLogin()
        }

        binding.brandsIcon.setOnClickListener { navigateTo(MoreFragmentDirections.actionMoreFragmentToBrandsFragment()) }
        binding.brandsHeader.setOnClickListener { navigateTo(MoreFragmentDirections.actionMoreFragmentToBrandsFragment()) }
        return binding.root
    }

    private fun navigateLogin() {
        requireActivity().finish()
        startActivity(Intent(requireContext(), LoginRegisterActivity::class.java).apply {
            putExtra("hideGuestLogin", false)
        })
    }

    private fun navigateTo(destination: NavDirections) {
        findNavController().navigate(destination)
    }

    private fun navigateToCart(cartLoginLauncher: ActivityResultLauncher<Intent>,cartLauncher: ActivityResultLauncher<Intent>) {
        if (UserInfo.userId == 0) {
            cartLoginLauncher.launch(
                Intent(
                    requireContext(),
                    LoginRegisterActivity::class.java
                ).apply {
                    putExtra("hideGuestLogin", true)
                })
        }else {
            cartLauncher.launch(
                Intent(
                    requireContext(),
                    if (CartInfo.cartStatus == 0 || CartInfo.cartStatus == 1) CartActivity::class.java else ProductCartActivity::class.java
                )
            )
        }
    }

    private fun setUserInfo() {
        if (UserInfo.userId != 0) {
            binding.userName = UserInfo.username
            binding.username.visibility = View.VISIBLE
            binding.profilePic = UserInfo.profilePicture
            binding.logoutIcon.visibility = View.VISIBLE
            binding.loginIcon.visibility = View.GONE
            binding.loginHeader.visibility = View.GONE
        } else {
            binding.logoutIcon.visibility = View.GONE
            binding.userName = ""
            binding.username.visibility = View.GONE
            binding.profilePic = "logo"
            binding.loginIcon.visibility = View.INVISIBLE
            binding.loginHeader.visibility = View.VISIBLE
        }
    }

    private fun launchActivity(
        launcher: ActivityResultLauncher<Intent>,
        destination: NavDirections
    ) {
        if (UserInfo.userId == 0) {
            launcher.launch(
                Intent(
                    requireContext(),
                    LoginRegisterActivity::class.java
                ).apply {
                    putExtra("hideGuestLogin", true)
                })
        } else {
            navigateTo(destination)
        }
    }


}