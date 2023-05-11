package code_grow.afeety.app.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.ModalDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import code_grow.afeety.app.R
import code_grow.afeety.app.activity.LoginRegisterActivity
import code_grow.afeety.app.activity.MainActivity
import code_grow.afeety.app.adapter.ExaminationAdapter
import code_grow.afeety.app.adapter.OnExaminationItemClickListener
import code_grow.afeety.app.databinding.FragmentReservationDetailsBinding
import code_grow.afeety.app.databinding.ReservationConfirmationLayoutBinding
import code_grow.afeety.app.kot_pref.UserInfo
import code_grow.afeety.app.repository.LabsRepository
import code_grow.afeety.app.retrofit.Resource
import code_grow.afeety.app.utils.CustomDialog
import code_grow.afeety.app.utils.Localize
import code_grow.afeety.app.view_model.LabReservationViewModel
import code_grow.afeety.app.view_model.LabReservationViewModelFactory

private const val TAG = "ReservationDetailsFragment"

class ReservationDetailsFragment : Fragment() {
    private lateinit var binding: FragmentReservationDetailsBinding
    private lateinit var viewModel: LabReservationViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        val labDetails = ReservationDetailsFragmentArgs.fromBundle(requireArguments()).labDetails
        val selectedExaminations =
            ReservationDetailsFragmentArgs.fromBundle(requireArguments()).selectedExaminations
        val totalPrice = selectedExaminations.sumOf {
            it.price
        }
        binding = FragmentReservationDetailsBinding.inflate(inflater, container, false)
        val viewModelFactory = LabReservationViewModelFactory(
            labId = labDetails.labId,
            examinations = selectedExaminations.toMutableList(),
            requireActivity().application,
            LabsRepository()
        )
        viewModel =
            ViewModelProvider(this, viewModelFactory)[LabReservationViewModel::class.java]
        binding.viewModel = viewModel
        binding.labDetails = labDetails
        binding.examinationsPrice = totalPrice
        binding.lifecycleOwner = requireActivity()

        // hide bottom navigation while creating this fragment
        val activity = requireActivity() as MainActivity
        activity.hideBottomNav(true)

        val examinationsAdapter = ExaminationAdapter(View.GONE, OnExaminationItemClickListener { })
        examinationsAdapter.submitList(selectedExaminations.toMutableList())
        binding.examinationsList.adapter = examinationsAdapter
        binding.examinationsList.layoutManager = LinearLayoutManager(requireContext())

        binding.navigateBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.homeReservationCheckbox.setOnClickListener {
            viewModel.checkIsHomeVisit()
        }

        val loginLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    viewModel.setStartBooking(true)
                }
            }
        binding.confirmButton.setOnClickListener {
            if (UserInfo.userId == 0) {
                // go login
                loginLauncher.launch(
                    Intent(
                        requireContext(),
                        LoginRegisterActivity::class.java
                    ).apply {
                        putExtra("hideGuestLogin", true)
                    })
            } else {
                viewModel.setStartBooking(true)
            }

        }

        viewModel.startBooking.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.book()
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

        return binding.root
    }

    private fun showSuccessDialog() {
        val dialog = MaterialDialog(requireContext(), ModalDialog).show {
            cornerRadius(res = com.intuit.sdp.R.dimen._8sdp)
            cancelOnTouchOutside(true)
            cancelable(false)
            customView(
                R.layout.reservation_confirmation_layout,
                noVerticalPadding = true,
                dialogWrapContent = true
            )
        }

        val binding = ReservationConfirmationLayoutBinding.bind(dialog.getCustomView())

        binding.homeButton.setOnClickListener {
            dialog.dismiss()
            findNavController().popBackStack(
                findNavController().graph.startDestinationId, false
            )
        }

        binding.myReservationsButton.setOnClickListener {
            dialog.dismiss()
            findNavController().navigate(
                ReservationDetailsFragmentDirections.actionReservationDetailsFragmentToMyReservationsFragment()
            )
        }

    }
}