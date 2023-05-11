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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import code_grow.afeety.app.activity.CartActivity
import code_grow.afeety.app.activity.LoginRegisterActivity
import code_grow.afeety.app.activity.MainActivity
import code_grow.afeety.app.activity.ProductCartActivity
import code_grow.afeety.app.adapter.*
import code_grow.afeety.app.databinding.FragmentHomeBinding
import code_grow.afeety.app.kot_pref.CartInfo
import code_grow.afeety.app.kot_pref.UserInfo
import code_grow.afeety.app.model.HomeData
import code_grow.afeety.app.model.LoginUser
import code_grow.afeety.app.model.SliderItem
import code_grow.afeety.app.repository.AuthRepository
import code_grow.afeety.app.repository.HomeRepository
import code_grow.afeety.app.retrofit.Resource
import code_grow.afeety.app.utils.Application
import code_grow.afeety.app.utils.Constants
import code_grow.afeety.app.utils.Localize
import code_grow.afeety.app.view_model.HomeViewModel
import code_grow.afeety.app.view_model.HomeViewModelFactory
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch

private const val TAG = "HomeFragment"

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val viewModelFactory =
            HomeViewModelFactory(requireActivity().application, HomeRepository(), AuthRepository())

        viewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()

        // hide bottom navigation while creating this fragment
        val activity = requireActivity() as MainActivity
        activity.hideBottomNav(false)

        if (UserInfo.isSigned) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener {
                if (!it.isSuccessful) {
                    return@addOnCompleteListener
                }
                UserInfo.firebaseToken = it.result
                viewModel.startUpdateToken(true)
            }
        }
        /* setup home slider views & data */
        val sliderAdapter = SliderAdapter(OnSliderItemClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToCouponsFragment()
            )
        })

        binding.topViewPager.adapter = sliderAdapter

        viewModel.startRequestHomeSlider.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.getHomeSlider()
            }
        }

        viewModel.sliderResponse.observe(viewLifecycleOwner) {
            viewModel.setStartRequestHomeSlider(false)
            if (it is Resource.Failed) {
                binding.sliderGroup.visibility = View.INVISIBLE
                binding.homeSliderErrorView.error = it.message
                binding.homeSliderErrorView.cancelAnimation = false
            } else if (it is Resource.Success<*>) {
                binding.sliderGroup.visibility = View.VISIBLE
                sliderAdapter.submitList(it.data as MutableList<SliderItem>)
                binding.topViewPagerIndicator.setViewPager2(binding.topViewPager)
            } else if (it is Resource.Empty) {
                binding.sliderGroup.visibility = View.GONE
            }
        }
        binding.homeSliderErrorView.root.setOnClickListener {
            binding.homeSliderErrorView.root.visibility = View.GONE
            binding.homeSliderErrorView.cancelAnimation = true
            viewModel.setStartRequestHomeSlider(true)
        }

        binding.moreOptionsIcon.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToMoreFragment()
            )
        }

        val cartLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    var goHome = it.data?.getBooleanExtra("goHome", true) ?: true
                    if (!goHome) {
                        findNavController().navigate(
                            HomeFragmentDirections.actionHomeFragmentToMyOrdersFragment()
                        )
                    }

                }
            }

        val productCartLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    var goHome = it.data?.getBooleanExtra("goHome", true) ?: true
                    if (!goHome) {
                        findNavController().navigate(
                            HomeFragmentDirections.actionHomeFragmentToMyOrdersFragment(1)
                        )
                    }

                }
            }
        val famousCartLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    var goHome = it.data?.getBooleanExtra("goHome", true) ?: true
                    if (!goHome) {
                        findNavController().navigate(
                            HomeFragmentDirections.actionHomeFragmentToMyOrdersFragment(2)
                        )
                    }

                }
            }

        val cartLoginLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    when (CartInfo.cartStatus) {
                        1 -> cartLauncher.launch(Intent(requireContext(), CartActivity::class.java))
                        2 -> productCartLauncher.launch(
                            Intent(
                                requireContext(),
                                ProductCartActivity::class.java
                            )
                        )
                        else -> famousCartLauncher.launch(
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
                    1 -> cartLauncher.launch(Intent(requireContext(), CartActivity::class.java))
                    2 -> productCartLauncher.launch(
                        Intent(
                            requireContext(),
                            ProductCartActivity::class.java
                        )
                    )
                    else -> famousCartLauncher.launch(
                        Intent(
                            requireContext(),
                            ProductCartActivity::class.java
                        )
                    )
                }
            }
        }



        binding.showMoreFamous.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToFamousFragment()
            )
        }
        binding.showMoreBestLabs.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToLabsListFragment()
            )
        }
        binding.showMoreBestHospitals.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToHospitalsListFragment()
            )
        }



        binding.labsContainer.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToLabsListFragment()
            )
        }
        binding.hospitalsContainer.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToHospitalsListFragment()
            )
        }
        binding.pharmaciesContainer.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToPharmaciesListFragment()
            )
        }
        binding.medicalProductsContainer.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToProductsListFragment()
            )
        }

//        if (requireActivity().intent.getIntExtra("order", 1) == 1) {
//            binding.firstDepartmentSecondFamousGroup.visibility = View.VISIBLE
//            binding.secondDepartmentFirstFamousGroup.visibility = View.GONE
//            binding.secondFamousListSection.famousPeopleList.adapter = famousAdapter
//            binding.secondFamousListSection.famousPeopleList.layoutManager =
//                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
//            binding.firstDepartmentSection.labsCount.text =
//                String.format(getString(R.string.labs_count), "99+")
//            binding.firstDepartmentSection.hospitalsCount.text =
//                String.format(getString(R.string.hospitals_count), "99+")
//            binding.firstDepartmentSection.pharmaciesCount.text =
//                String.format(getString(R.string.pharmacies_count), "99+")
//            binding.firstDepartmentSection.medicalProductsCount.text =
//                String.format(getString(R.string.medical_products_count), "99+")
//            binding.firstDepartmentSection.labsContainer.setOnClickListener {
//                findNavController().navigate(
//                    HomeFragmentDirections.actionHomeFragmentToLabsListFragment()
//                )
//            }
//            binding.firstDepartmentSection.hospitalsContainer.setOnClickListener {
//                findNavController().navigate(
//                    HomeFragmentDirections.actionHomeFragmentToHospitalsListFragment()
//                )
//            }
//            binding.firstDepartmentSection.pharmaciesContainer.setOnClickListener {
//                findNavController().navigate(
//                    HomeFragmentDirections.actionHomeFragmentToPharmaciesListFragment()
//                )
//            }
//            binding.firstDepartmentSection.medicalProductsContainer.setOnClickListener {
//                findNavController().navigate(
//                    HomeFragmentDirections.actionHomeFragmentToProductsListFragment()
//                )
//            }
//        } else {
//            binding.firstDepartmentSecondFamousGroup.visibility = View.GONE
//            binding.secondDepartmentFirstFamousGroup.visibility = View.VISIBLE
//            binding.firstFamousListSection.famousPeopleList.adapter = famousAdapter
//            binding.firstFamousListSection.famousPeopleList.layoutManager =
//                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
//            binding.secondDepartmentSection.labsCount.text =
//                String.format(getString(R.string.labs_count), "99+")
//            binding.secondDepartmentSection.hospitalsCount.text =
//                String.format(getString(R.string.hospitals_count), "99+")
//            binding.secondDepartmentSection.pharmaciesCount.text =
//                String.format(getString(R.string.pharmacies_count), "99+")
//            binding.secondDepartmentSection.medicalProductsCount.text =
//                String.format(getString(R.string.medical_products_count), "99+")
//
//            binding.secondDepartmentSection.labsContainer.setOnClickListener {
//                findNavController().navigate(
//                    HomeFragmentDirections.actionHomeFragmentToLabsListFragment()
//                )
//            }
//            binding.secondDepartmentSection.hospitalsContainer.setOnClickListener {
//                findNavController().navigate(
//                    HomeFragmentDirections.actionHomeFragmentToHospitalsListFragment()
//                )
//            }
//            binding.secondDepartmentSection.pharmaciesContainer.setOnClickListener {
//                findNavController().navigate(
//                    HomeFragmentDirections.actionHomeFragmentToPharmaciesListFragment()
//                )
//            }
//            binding.secondDepartmentSection.medicalProductsContainer.setOnClickListener {
//                findNavController().navigate(
//                    HomeFragmentDirections.actionHomeFragmentToProductsListFragment()
//                )
//            }
//        }

        val famousAdapter = HFamousAdapter(OnFamousItemClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToFamousInfoFragment(it)
            )
        })

        binding.famousPeopleList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.famousPeopleList.adapter = famousAdapter

        val bestHospitalsAdapter = HospitalsAdapter(OnHospitalItemClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToHospitalDetailsFragment(it)
            )
        })
        binding.bestHospitalsList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.bestHospitalsList.adapter = bestHospitalsAdapter

        val bestLabsAdapter = LabsAdapter(OnLabItemClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToLabDetailsFragment(it)
            )
        })
        binding.bestLabsList.adapter = bestLabsAdapter
        binding.bestLabsList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)


        viewModel.startRequestHomeData.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.getHomeData()
            }
        }

        viewModel.homeResponse.observe(viewLifecycleOwner) {
            viewModel.setStartRequestHomeData(false)
            if (it is Resource.Failed) {
                binding.homeDataErrorView.error = it.message
                binding.homeDataErrorView.cancelAnimation = false
            } else if (it is Resource.Success<*>) {
                val data = it.data as HomeData

                if (data.influencers.isNullOrEmpty()) {
                    binding.famousGroup.visibility = View.GONE
                } else {
                    binding.famousGroup.visibility = View.VISIBLE
                    famousAdapter.submitList(data.influencers)
                }
                if (data.bestHospitals.isNullOrEmpty()) {
                    binding.hospitalsGroup.visibility = View.GONE
                } else {
                    binding.hospitalsGroup.visibility = View.VISIBLE
                    bestHospitalsAdapter.submitList(data.bestHospitals)
                }
                if (data.bestLabs.isNullOrEmpty()) {
                    binding.labsGroup.visibility = View.GONE
                } else {
                    binding.labsGroup.visibility = View.VISIBLE
                    bestLabsAdapter.submitList(data.bestLabs)
                }
            }
        }
        binding.homeDataErrorView.root.setOnClickListener {
            binding.homeDataErrorView.root.visibility = View.GONE
            binding.homeDataErrorView.cancelAnimation = true
            viewModel.setStartRequestHomeData(true)
        }


        viewModel.startUpdateToken.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.updateToken()
            }
        }

        viewModel.updateTokenResponse.observe(viewLifecycleOwner) {
            viewModel.startUpdateToken(false)
            if (it is Resource.Success<*>) {
                val user = (it.data as LoginUser)
                UserInfo.isSigned = true
                UserInfo.userId = user.userId
                UserInfo.username = user.username
                UserInfo.password = user.password
                UserInfo.email = user.email
                UserInfo.phoneNumber = user.phoneNumber
                UserInfo.profilePicture =
                    Constants.BASE_URL.plus(user.profilePicture)
                UserInfo.gender = user.gender
                UserInfo.cityId = user.cityId
                UserInfo.longitude = user.longitude.toString()
                UserInfo.latitude = user.latitude.toString()
                UserInfo.address = user.address
                UserInfo.age = user.age
                UserInfo.activated = user.activated
                UserInfo.firebaseToken = user.firebaseToken ?: ""
            } else if (it is Resource.Failed) {
                logout()
            }
        }

        return binding.root
    }

    private fun logout(){
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

}