package code_grow.afeety.app.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import code_grow.afeety.app.activity.LoginRegisterActivity
import code_grow.afeety.app.activity.MainActivity
import code_grow.afeety.app.databinding.FragmentBookCouponBinding
import code_grow.afeety.app.kot_pref.UserInfo
import code_grow.afeety.app.repository.CouponsRepository
import code_grow.afeety.app.retrofit.Resource
import code_grow.afeety.app.utils.CustomDialog
import code_grow.afeety.app.utils.Localize
import code_grow.afeety.app.view_model.BookCouponViewModel
import code_grow.afeety.app.view_model.BookCouponViewModelFactory

class BookCouponFragment : Fragment() {
    private lateinit var binding: FragmentBookCouponBinding
    private lateinit var viewModel: BookCouponViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())

        binding = FragmentBookCouponBinding.inflate(inflater, container, false)
        val coupon = BookCouponFragmentArgs.fromBundle(requireArguments()).coupon

        val viewModelFactory = BookCouponViewModelFactory(
            requireActivity().application,
            coupon,
            CouponsRepository()
        )

        viewModel = ViewModelProvider(this, viewModelFactory)[BookCouponViewModel::class.java]
        binding.viewModel = viewModel
        binding.coupon = coupon
        binding.lifecycleOwner = requireActivity()

        // hide bottom navigation while creating this fragment
        val activity = requireActivity() as MainActivity
        activity.hideBottomNav(true)

        binding.navigateBack.setOnClickListener {
            findNavController().popBackStack()
        }

        val loginLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    viewModel.startBookCoupon(true)
                }
            }

        binding.bookNowButton.setOnClickListener {
            if (UserInfo.userId == 0) {
                loginLauncher.launch(
                    Intent(
                        requireContext(),
                        LoginRegisterActivity::class.java
                    ).apply {
                        putExtra("hideGuestLogin", true)
                    })
            } else
                viewModel.startBookCoupon(true)
        }

        viewModel.startBookCoupon.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.bookCoupon()
            }
        }

        viewModel.bookingResponse.observe(viewLifecycleOwner) {
            if (it is Resource.Success<*>) {
                Log.d("SuccessMessage", "Message: ${it.data as String}")
                CustomDialog.showSuccessDialog(
                    context = requireContext(),
                    successMessage = it.data as String,
                    navController = findNavController()
                )
            } else if (it is Resource.Failed) {
                CustomDialog.showErrorDialog(
                    context = requireContext(),
                    errorMessage = it.message
                )
            }
        }


        return binding.root
    }
}