package code_grow.afeety.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import code_grow.afeety.app.databinding.FragmentResetPasswordBinding
import code_grow.afeety.app.repository.AuthRepository
import code_grow.afeety.app.utils.Localize
import code_grow.afeety.app.view_model.ResetPasswordViewModel
import code_grow.afeety.app.view_model.ResetPasswordViewModelFactory

class ResetPasswordFragment : Fragment() {
    private lateinit var binding: FragmentResetPasswordBinding
    private lateinit var viewModel: ResetPasswordViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        binding = FragmentResetPasswordBinding.inflate(inflater, container, false)
        val viewModelFactory =
            ResetPasswordViewModelFactory(requireActivity().application, AuthRepository())
        viewModel = ViewModelProvider(this, viewModelFactory)[ResetPasswordViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.navigateBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.saveChangesButton.setOnClickListener {
            viewModel.changePasswordBtnClicked()
        }

        viewModel.changePasswordBooleanLiveData.observe(viewLifecycleOwner) {
            if (it) {
                // start changing password
                    viewModel.changePasswordBooleanLiveData.value = false
                Toast.makeText(requireContext(), "done", Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }
}