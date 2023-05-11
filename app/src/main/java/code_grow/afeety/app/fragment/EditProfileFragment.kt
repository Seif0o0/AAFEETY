package code_grow.afeety.app.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.DialogBehavior
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.ModalDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.airbnb.lottie.LottieDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import code_grow.afeety.app.R
import code_grow.afeety.app.databinding.ErrorUploadingImageDialogLayoutBinding
import code_grow.afeety.app.databinding.FragmentEditProfileBinding
import code_grow.afeety.app.kot_pref.UserInfo
import code_grow.afeety.app.model.City
import code_grow.afeety.app.model.LoginUser
import code_grow.afeety.app.repository.AuthRepository
import code_grow.afeety.app.retrofit.Resource
import code_grow.afeety.app.utils.Constants
import code_grow.afeety.app.utils.CustomDialog
import code_grow.afeety.app.utils.Localize
import code_grow.afeety.app.view_model.EditProfileViewModel
import code_grow.afeety.app.view_model.EditProfileViewModelFactory
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.constant.ImageProvider

private const val TAG = "EditProfileFragment"

class EditProfileFragment : Fragment() {
    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var viewModel: EditProfileViewModel

    private val profilePictureLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val file = ImagePicker.getFile(it.data)!!
                viewModel.setStartUploadingImage(true, file)
                Glide.with(this)
                    .load(file)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(binding.profilePicture)
            } else {
                Log.d(TAG, "PickPic Failed: $it")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())

        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        binding.profilePic = UserInfo.profilePicture

        val user = LoginUser(
            UserInfo.userId,
            UserInfo.username,
            "",
            UserInfo.email,
            UserInfo.phoneNumber,
            UserInfo.profilePicture,
            UserInfo.gender,
            UserInfo.cityId,
            UserInfo.longitude.toDouble(),
            UserInfo.latitude.toDouble(),
            UserInfo.address,
            UserInfo.accessToken,
            UserInfo.age,
            0,
            0,
            UserInfo.activated
        )

        val viewModelFactory =
            EditProfileViewModelFactory(user, requireActivity().application, AuthRepository())
        viewModel = ViewModelProvider(this, viewModelFactory)[EditProfileViewModel::class.java]
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

        binding.saveButton.setOnClickListener {
            hideKeyboard()
            viewModel.editProfileBtnClicked()
        }

        binding.changeProfilePicture.setOnClickListener {
            hideKeyboard()
            ImagePicker.with(requireActivity())
                .crop(1f, 1f)
                .cropOval()
                /* Final image resolution will be less than 1080 x 1080 (Optional) */
                .maxResultSize(1080, 1080, true)
                /* make user able to pick picture from gallery or capture it */
                .provider(ImageProvider.BOTH)
                .createIntentFromDialog { profilePictureLauncher.launch(it) }
        }

        viewModel.startUploadingImage.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.uploadImage()
            }
        }

        viewModel.uploadImageResponse.observe(viewLifecycleOwner) {
            viewModel.setStartUploadingImage(false, null)
            if (it is Resource.Failed) {
                showErrorUploadingImageDialog(context = requireContext(), errorMessage = it.message)
            }
        }

        /* observe validation flag to make registration req. */
        viewModel.editProfileBooleanLiveData.observe(viewLifecycleOwner) {
            if (it) {
                // call register req. here
                viewModel.editProfileBooleanLiveData.value = false
                viewModel.editProfile()
            }
        }

        /* observe register req. response */
        viewModel.editProfileResponse.observe(viewLifecycleOwner) {
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
                    "${Constants.BASE_URL}prod_img/".plus(user.profilePicture)
                UserInfo.gender = user.gender
                UserInfo.cityId = user.cityId
                UserInfo.longitude = user.longitude.toString()
                UserInfo.latitude = user.latitude.toString()
                UserInfo.address = user.address
                UserInfo.firebaseToken = user.firebaseToken ?: ""
                UserInfo.age = user.age
                UserInfo.activated = user.activated
                CustomDialog.showSuccessDialog(
                    context = requireContext(),
                    successMessage = getString(R.string.edit_profile_successful_message),
                    navController = findNavController()
                )
            }
        }

        /* observe cities req. response */
        viewModel.citiesResponse.observe(viewLifecycleOwner) {
            if (it is Resource.Failed) {
                binding.error = it.message
                binding.cancelAnimation = false
            } else if (it is Resource.Success<*>) {
                val cities = it.data as MutableList<City>
                val citiesName = mutableListOf<String>()
                for (city in cities) {
                    citiesName.add(city.name)
                }
                binding.autocompleteCity.setAdapter(
                    ArrayAdapter(
                        requireContext(),
                        R.layout.register_dropdown_item,
                        citiesName
                    )
                )
                val city = cities.find { requiredCity ->
                    requiredCity.cityId == UserInfo.cityId
                }
                binding.autocompleteCity.setText(city?.name, false)
            }
        }

        binding.errorView.setOnClickListener {
            binding.errorView.visibility = View.GONE
            binding.cancelAnimation = true
            viewModel.getCities()
        }

        if (UserInfo.gender == 2) {
            binding.autocompleteGender.setText(
                requireContext().resources.getStringArray(R.array.gender)[1],
                false
            )
        } else if (UserInfo.gender == 1) {
            binding.autocompleteGender.setText(
                requireContext().resources.getStringArray(R.array.gender)[0],
                false
            )
        }

        viewModel.imageMessageLiveData.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                CustomDialog.showErrorDialog(
                    context = requireContext(),
                    errorMessage = it
                )
            }
        }


        return binding.root
    }

    private fun hideKeyboard() {
        val imm: InputMethodManager =
            requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.rootView?.windowToken, 0)
    }

    private fun showErrorUploadingImageDialog(
        dialogBehavior: DialogBehavior = ModalDialog,
        context: Context,
        errorMessage: String
    ) {
        val dialog = MaterialDialog(context, dialogBehavior).show {
            cornerRadius(res = com.intuit.sdp.R.dimen._8sdp)
            cancelOnTouchOutside(true)
            customView(
                R.layout.error_uploading_image_dialog_layout,
                noVerticalPadding = true,
                dialogWrapContent = true
            )
        }

        val binding = ErrorUploadingImageDialogLayoutBinding.bind(dialog.getCustomView())
        binding.errorMessage.text = errorMessage

        binding.errorAnimation.setAnimation("error_dialog_animation.json")
        binding.errorAnimation.playAnimation()
        binding.errorAnimation.repeatCount = LottieDrawable.INFINITE

        binding.cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        binding.retryButton.setOnClickListener {
            dialog.dismiss()
            viewModel.setStartUploadingImage(
                true,
                null /* null because file is already exist in viewModel */
            )
        }

        dialog.setOnDismissListener {
            binding.errorAnimation.cancelAnimation()
        }

    }
}