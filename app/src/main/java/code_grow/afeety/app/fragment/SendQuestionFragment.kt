package code_grow.afeety.app.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import code_grow.afeety.app.R
import code_grow.afeety.app.activity.LoginRegisterActivity
import code_grow.afeety.app.activity.MainActivity
import code_grow.afeety.app.databinding.FragmentSendQuestionBinding
import code_grow.afeety.app.kot_pref.UserInfo
import code_grow.afeety.app.model.Speciality
import code_grow.afeety.app.repository.QuestionsRepository
import code_grow.afeety.app.retrofit.Resource
import code_grow.afeety.app.utils.CustomDialog
import code_grow.afeety.app.utils.Localize
import code_grow.afeety.app.view_model.SendQuestionViewModel
import code_grow.afeety.app.view_model.SendQuestionViewModelFactory

class SendQuestionFragment : Fragment() {
    private lateinit var binding: FragmentSendQuestionBinding
    private lateinit var viewModel: SendQuestionViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        val viewModelFactory = SendQuestionViewModelFactory(
            requireActivity().application,
            QuestionsRepository()
        )

        viewModel = ViewModelProvider(this, viewModelFactory)[SendQuestionViewModel::class.java]
        binding = FragmentSendQuestionBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
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
                    viewModel.sendBtnClicked()
                }
            }

        binding.sendButton.setOnClickListener {
            if(UserInfo.userId == 0){
                loginLauncher.launch(
                    Intent(
                        requireContext(),
                        LoginRegisterActivity::class.java
                    ).apply {
                        putExtra("hideGuestLogin", true)
                    })
            }else
                viewModel.sendBtnClicked()
        }

        viewModel.startRequestSpecialities.observe(viewLifecycleOwner) {
            if (it)
                viewModel.getSpecialities()
        }

        viewModel.specialitiesResponse.observe(viewLifecycleOwner) {
            if (it is Resource.Success<*>) {
                val specialities = it.data as MutableList<Speciality>
                val specialitiesNames = mutableListOf<String>()
                for (speciality in specialities)
                    specialitiesNames.add(speciality.name)

                binding.autocompleteSpeciality.setAdapter(
                    ArrayAdapter(
                        requireContext(),
                        R.layout.register_dropdown_item,
                        specialitiesNames
                    )
                )
            }else if (it is Resource.Failed){
                binding.errorView.error = it.message
                binding.errorView.cancelAnimation = false
            }
        }

        binding.errorView.root.setOnClickListener {
            binding.errorView.root.visibility = View.GONE
            binding.errorView.cancelAnimation = true
            viewModel.startRequestSpecialities(true)
        }

        viewModel.startSendQuestion.observe(viewLifecycleOwner) {
            if (it)
                viewModel.sendQuestion()
        }

        viewModel.sendQuestionResponse.observe(viewLifecycleOwner) {
            if (it is Resource.Success<*>) {
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
            viewModel.startSendQuestion(false)
        }

        return binding.root
    }
}