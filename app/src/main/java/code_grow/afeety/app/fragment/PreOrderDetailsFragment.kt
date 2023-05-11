package code_grow.afeety.app.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import code_grow.afeety.app.R
import code_grow.afeety.app.databinding.BuySuccessLayoutBinding
import code_grow.afeety.app.databinding.ErrorUploadingImageDialogLayoutBinding
import code_grow.afeety.app.databinding.FragmentPreOrderDetailsBinding
import code_grow.afeety.app.repository.CartRepository
import code_grow.afeety.app.retrofit.Resource
import code_grow.afeety.app.utils.CustomDialog
import code_grow.afeety.app.utils.Localize
import code_grow.afeety.app.view_model.PreOrderDetailsViewModel
import code_grow.afeety.app.view_model.PreOrderDetailsViewModelFactory
import com.afollestad.materialdialogs.DialogBehavior
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.ModalDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.airbnb.lottie.LottieDrawable

class PreOrderDetailsFragment : Fragment() {
    private lateinit var binding: FragmentPreOrderDetailsBinding
    private lateinit var viewModel: PreOrderDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        binding = FragmentPreOrderDetailsBinding.inflate(inflater, container, false)
        val args = PreOrderDetailsFragmentArgs.fromBundle(requireArguments())
        val pharmacyId = requireActivity().intent.extras!!.getInt("pharmacy_id")
        val addressId = args.addressId
        val addressDetails = args.addressDetails
        val prescription = args.prescription

        val viewModelFactory = PreOrderDetailsViewModelFactory(
            prescriptionUri = prescription,
            addressId = addressId,
            pharmacyId = pharmacyId,
            application = requireActivity().application,
            repo = CartRepository(),
        )

        viewModel =
            ViewModelProvider(this, viewModelFactory)[PreOrderDetailsViewModel::class.java]
        binding.viewModel = viewModel
        binding.prescriptionUri = prescription
        binding.address = addressDetails
        binding.lifecycleOwner = requireActivity()

        binding.navigateBack.setOnClickListener {
            findNavController().popBackStack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            })

        binding.submitButton.setOnClickListener {
            viewModel.startUploadingImage(true)
        }

        viewModel.startUploadingImage.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.uploadPrescription()
            }
        }

        viewModel.uploadReportResponse.observe(viewLifecycleOwner) {
            viewModel.startUploadingImage(false)
            if (it is Resource.Failed) {
                showErrorUploadingImageDialog(context = requireContext(), errorMessage = it.message)
            }

        }

        viewModel.startBuying.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.buy()
            }
        }

        viewModel.buyingResponse.observe(viewLifecycleOwner) {
            viewModel.startBuying(false)
            if (it is Resource.Success<*>) {
                showSuccessDialog()
            } else if (it is Resource.Failed) {
                CustomDialog.showErrorDialog(
                    context = requireContext(),
                    errorMessage = it.message
                )
            }
        }

        return binding.root
    }

    private fun showSuccessDialog() {
        val dialog = MaterialDialog(requireContext(), ModalDialog).show {
            cornerRadius(res = com.intuit.sdp.R.dimen._8sdp)
            cancelOnTouchOutside(true)
            cancelable(false)
            customView(
                R.layout.buy_success_layout,
                noVerticalPadding = true,
                dialogWrapContent = true
            )
        }

        val binding = BuySuccessLayoutBinding.bind(dialog.getCustomView())


        binding.confirmationMessage.text = getString(R.string.buy_success_message_1)
        binding.homeButton.setOnClickListener {
            dialog.dismiss()
            val intent = Intent()
            intent.putExtra("goHome", true)
            requireActivity().setResult(Activity.RESULT_OK, intent)
            requireActivity().finish()
        }

        binding.myOrdersButton.setOnClickListener {
            dialog.dismiss()
            val intent = Intent()
            intent.putExtra("goHome", false)
            requireActivity().setResult(Activity.RESULT_OK, intent)
            requireActivity().finish()
        }

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
            viewModel.startUploadingImage(true)
        }

        dialog.setOnDismissListener {
            binding.errorAnimation.cancelAnimation()
        }

    }
}