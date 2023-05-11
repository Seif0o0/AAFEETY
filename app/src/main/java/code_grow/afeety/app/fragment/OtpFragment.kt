package code_grow.afeety.app.fragment

import `in`.aabhasjindal.otptextview.OTPListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import code_grow.afeety.app.databinding.FragmentOtpBinding
import code_grow.afeety.app.repository.AuthRepository
import code_grow.afeety.app.utils.Localize
import code_grow.afeety.app.view_model.OtpViewModel
import code_grow.afeety.app.view_model.OtpViewModelFactory

class OtpFragment : Fragment() {
    private lateinit var binding: FragmentOtpBinding
    private lateinit var viewModel: OtpViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        binding = FragmentOtpBinding.inflate(inflater, container, false)
        val viewModelFactory = OtpViewModelFactory(requireActivity().application, AuthRepository())
        viewModel = ViewModelProvider(this, viewModelFactory)[OtpViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.navigateBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.submitButton.setOnClickListener {
            viewModel.submitBtnClicked()
        }

        viewModel.submitBooleanLiveData.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.submitBooleanLiveData.value = false
                findNavController().navigate(OtpFragmentDirections.actionOtpFragmentToResetPasswordFragment())
            }
        }
        binding.otpView.otpListener = object : OTPListener {
            override fun onInteractionListener() {
                // clear any error if exist
                viewModel.setOtpErrorLiveData(0)

                if (binding.otpView.otp != null)
                    viewModel.setOtpLiveData(binding.otpView.otp!!)
            }

            override fun onOTPComplete(otp: String) {
            }

        }
        return binding.root
    }
}