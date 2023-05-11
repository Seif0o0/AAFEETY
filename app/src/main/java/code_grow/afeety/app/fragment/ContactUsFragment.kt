package code_grow.afeety.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import code_grow.afeety.app.R
import code_grow.afeety.app.databinding.FragmentContactUsBinding
import code_grow.afeety.app.repository.ContactUsRepository
import code_grow.afeety.app.retrofit.Resource
import code_grow.afeety.app.utils.CustomDialog
import code_grow.afeety.app.utils.Localize
import code_grow.afeety.app.view_model.ContactUsViewModel
import code_grow.afeety.app.view_model.ContactUsViewModelFactory

class ContactUsFragment : Fragment() {
    private lateinit var binding: FragmentContactUsBinding
    private lateinit var viewModel: ContactUsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        binding = FragmentContactUsBinding.inflate(inflater, container, false)

        val viewModelFactory =
            ContactUsViewModelFactory(requireActivity().application, ContactUsRepository())

        viewModel = ViewModelProvider(this, viewModelFactory)[ContactUsViewModel::class.java]

        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()

        binding.navigateBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.sendButton.setOnClickListener {
            viewModel.sendBtnClicked()
        }

        viewModel.sendFeedbackBooleanLiveData.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.sendFeedback()
            }
        }

        viewModel.sendFeedbackResponse.observe(viewLifecycleOwner) {
            viewModel.startSendingFeedback(false)
            if (it is Resource.Success<*>) {
                CustomDialog.showSuccessDialog(
                    context = requireContext(),
                    successMessage = getString(R.string.your_message_has_been_received),
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