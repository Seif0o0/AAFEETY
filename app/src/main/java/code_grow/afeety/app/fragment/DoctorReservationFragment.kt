package code_grow.afeety.app.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import code_grow.afeety.app.R
import code_grow.afeety.app.activity.MainActivity
import code_grow.afeety.app.databinding.DoctorReservationConfirmationLayoutBinding
import code_grow.afeety.app.databinding.ErrorUploadingImageDialogLayoutBinding
import code_grow.afeety.app.databinding.FragmentDoctorReservationBinding
import code_grow.afeety.app.repository.HospitalsRepository
import code_grow.afeety.app.retrofit.Resource
import code_grow.afeety.app.utils.CustomDialog
import code_grow.afeety.app.utils.Localize
import code_grow.afeety.app.view_model.DoctorReservationViewModel
import code_grow.afeety.app.view_model.DoctorReservationViewModelFactory
import com.afollestad.materialdialogs.DialogBehavior
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.ModalDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.airbnb.lottie.LottieDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.constant.ImageProvider

private const val TAG = "DoctorReservationFragment"

class DoctorReservationFragment : Fragment() {
    private lateinit var binding: FragmentDoctorReservationBinding
    private lateinit var viewModel: DoctorReservationViewModel

    private lateinit var reportLauncher: ActivityResultLauncher<Intent>


    @SuppressLint("LongLogTag")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        binding = FragmentDoctorReservationBinding.inflate(inflater, container, false)
        val doctorDetails =
            DoctorReservationFragmentArgs.fromBundle(requireArguments()).doctorDetails
        val selectedDate =
            DoctorReservationFragmentArgs.fromBundle(requireArguments()).selectedSchedule

        val viewModelFactory = DoctorReservationViewModelFactory(
            doctorDetails, selectedDate, requireActivity().application,
            HospitalsRepository()
        )
        viewModel =
            ViewModelProvider(this, viewModelFactory)[DoctorReservationViewModel::class.java]
        binding.viewModel = viewModel
        binding.schedule = selectedDate
        binding.lifecycleOwner = requireActivity()

        // hide bottom navigation while creating this fragment
        val activity = requireActivity() as MainActivity
        activity.hideBottomNav(true)

        reportLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val file = ImagePicker.getFile(it.data)!!
                    viewModel.setStartUploadingImage(true, file)
                    binding.report.visibility = View.VISIBLE
                    Glide.with(this)
                        .load(file)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(binding.report)
                } else {
                    Log.d(TAG, "PickPic Failed: $it")
                }
            }

        binding.uploadFilesTitle.setOnClickListener {
            pickReport()
        }
        binding.uploadFilesIcon.setOnClickListener {
            pickReport()
        }

        binding.confirmButton.setOnClickListener {
            viewModel.setStartBooking(true)
        }

        binding.navigateBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.removeReport.setOnClickListener {
            binding.report.setImageDrawable(null)
            viewModel.removeReport()
            binding.reportGroup.visibility = View.GONE
            binding.uploadFilesTitle.text = getString(R.string.attach_report)
        }
        viewModel.startBooking.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.bookBtnClicked()
            }
        }

        viewModel.bookingResponse.observe(viewLifecycleOwner) {
            viewModel.setStartBooking(false)
            if (it is Resource.Success<*>) {
                showSuccessDialog()
            } else if (it is Resource.Failed) {
                CustomDialog.showErrorDialog(
                    context = requireContext(),
                    errorMessage = it.message
                )
            }
        }

        viewModel.startUploadingImage.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.uploadReport()
            }
        }

        viewModel.uploadReportResponse.observe(viewLifecycleOwner) {
            viewModel.setStartUploadingImage(false, null)
            if (viewModel.uploadedReport.isEmpty()) {
                binding.uploadFilesTitle.text = getString(R.string.attach_report)
            } else {
                binding.uploadFilesTitle.text = getString(R.string.change_report)
            }
            if (it is Resource.Failed) {
                showErrorUploadingImageDialog(context = requireContext(), errorMessage = it.message)
            }
        }

        return binding.root
    }

    private fun pickReport() {
        ImagePicker.with(requireActivity())
            .crop(1f, 1f)
            .cropOval()
            /* Final image resolution will be less than 1080 x 1080 (Optional) */
            .maxResultSize(1080, 1080, true)
            /* make user able to pick picture from gallery or capture it */
            .provider(ImageProvider.BOTH)
            .createIntentFromDialog { reportLauncher.launch(it) }
    }

    private fun showSuccessDialog() {
        val dialog = MaterialDialog(requireContext(), ModalDialog).show {
            cornerRadius(res = com.intuit.sdp.R.dimen._8sdp)
            cancelOnTouchOutside(true)
            cancelable(false)
            customView(
                R.layout.doctor_reservation_confirmation_layout,
                noVerticalPadding = true,
                dialogWrapContent = true
            )
        }

        val binding = DoctorReservationConfirmationLayoutBinding.bind(dialog.getCustomView())

        binding.homeButton.setOnClickListener {
            dialog.dismiss()
            findNavController().popBackStack(
                findNavController().graph.startDestinationId, false
            )
        }

        binding.myReservationsButton.setOnClickListener {
            dialog.dismiss()
            findNavController().navigate(
                DoctorReservationFragmentDirections.actionDoctorReservationFragmentToMyReservationsFragment(
                    false
                )
            )
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