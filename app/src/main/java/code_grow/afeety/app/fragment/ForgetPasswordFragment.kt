package code_grow.afeety.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import code_grow.afeety.app.R
import code_grow.afeety.app.databinding.FragmentForgetPasswordBinding
import code_grow.afeety.app.repository.AuthRepository
import code_grow.afeety.app.utils.Localize
import code_grow.afeety.app.view_model.ForgetPasswordViewModel
import code_grow.afeety.app.view_model.ForgetPasswordViewModelFactory

class ForgetPasswordFragment : Fragment() {
    private lateinit var binding: FragmentForgetPasswordBinding
    private lateinit var viewModel: ForgetPasswordViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        binding = FragmentForgetPasswordBinding.inflate(inflater, container, false)
        val viewModelFactory = ForgetPasswordViewModelFactory(
            requireActivity().application,
            AuthRepository()
        )

        viewModel = ViewModelProvider(this, viewModelFactory)[ForgetPasswordViewModel::class.java]

        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()

        binding.phoneNumberEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                binding.phoneNumberEditTextBackground.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.focused_edit_text_background
                )
            else binding.phoneNumberEditTextBackground.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.edit_text_background)
        }

        binding.navigateBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.sendCodeButton.setOnClickListener {
            viewModel.sendCodeBtnClicked()
        }

        viewModel.sendCodeBooleanLiveData.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.sendCodeBooleanLiveData.value = false
                findNavController().navigate(ForgetPasswordFragmentDirections.actionForgetPasswordFragmentToOtpFragment())
            }
        }
        return binding.root
    }
}