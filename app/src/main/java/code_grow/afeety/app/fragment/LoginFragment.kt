package code_grow.afeety.app.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import code_grow.afeety.app.R
import code_grow.afeety.app.activity.MainActivity
import code_grow.afeety.app.databinding.FragmentLoginBinding
import code_grow.afeety.app.model.LoginUser
import code_grow.afeety.app.retrofit.Resource
import code_grow.afeety.app.utils.CustomDialog
import code_grow.afeety.app.kot_pref.UserInfo
import code_grow.afeety.app.repository.AuthRepository
import code_grow.afeety.app.utils.Constants
import code_grow.afeety.app.utils.Localize
import code_grow.afeety.app.view_model.LoginViewModel
import code_grow.afeety.app.view_model.LoginViewModelFactory
import com.google.firebase.messaging.FirebaseMessaging

/*
    913764825
    000000
 */
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        val viewModelFactory =
            LoginViewModelFactory(requireActivity().application, AuthRepository())

        viewModel = ViewModelProvider(this, viewModelFactory)[LoginViewModel::class.java]

        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()
        val actExtras = requireActivity().intent?.extras
        val hide = actExtras?.getBoolean("hideGuestLogin")
        if (hide!!) binding.loginAsGuest.visibility = View.GONE
        Log.d("LoginFragment", "Hide: $hide")

        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (!it.isSuccessful) {
                return@addOnCompleteListener
            }
            Log.d("LoginFragment", "Firebase token: ${it.result} ")
            viewModel.firebaseToken = it.result
        }

        binding.phoneNumberEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                binding.phoneNumberEditTextBackground.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.focused_edit_text_background
                )
            else binding.phoneNumberEditTextBackground.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.edit_text_background)
        }

        binding.register.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
        }

        binding.forgetPassword.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToForgetPasswordFragment())
        }

        binding.loginButton.setOnClickListener {
            hideKeyboard()
            viewModel.loginBtnCLicked()
        }

        /* observe validation flag to make login req. */
        viewModel.loginBooleanLiveData.observe(viewLifecycleOwner) {
            if (it) {
                // call login req. here
                viewModel.loginBooleanLiveData.value = false
                viewModel.login()
            }
        }

        /* observe login req. response */
        viewModel.loginResponse.observe(viewLifecycleOwner) {
            if (it is Resource.Failed) {
                CustomDialog.showErrorDialog(
                    context = requireContext(),
                    errorMessage = it.message
                )
            } else if (it is Resource.Success<*>) {
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
                UserInfo.firebaseToken = user.firebaseToken
                UserInfo.age = user.age
                UserInfo.activated = user.activated

                if (hide) {
                    requireActivity().setResult(Activity.RESULT_OK)
                    requireActivity().finish()
                } else {
                    requireActivity().finish()
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                }


            }
        }

        binding.loginAsGuest.setOnClickListener {
            requireActivity().finish()
            startActivity(Intent(requireContext(), MainActivity::class.java))
        }
        return binding.root
    }

    private fun hideKeyboard() {
        val imm: InputMethodManager =
            requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.rootView?.windowToken, 0)
    }
}