package code_grow.afeety.app.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import code_grow.afeety.app.activity.LoginRegisterActivity
import code_grow.afeety.app.activity.MainActivity
import code_grow.afeety.app.adapter.DoctorScheduleAdapter
import code_grow.afeety.app.adapter.OnScheduleItemClickListener
import code_grow.afeety.app.databinding.FragmentDoctorDetailsBinding
import code_grow.afeety.app.kot_pref.UserInfo
import code_grow.afeety.app.model.DoctorSchedule
import code_grow.afeety.app.utils.Localize

class DoctorDetailsFragment : Fragment() {
    private lateinit var binding: FragmentDoctorDetailsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        val doctorDetails = DoctorDetailsFragmentArgs.fromBundle(requireArguments()).doctorDetails
        if (this::binding.isInitialized) {
            binding
        } else {
            binding = FragmentDoctorDetailsBinding.inflate(inflater, container, false)
            binding.doctor = doctorDetails
            binding.lifecycleOwner = requireActivity()
            binding.selectedTime = doctorDetails.doctorSchedule!![0]
        }


        // hide bottom navigation while creating this fragment
        val activity = requireActivity() as MainActivity
        activity.hideBottomNav(true)


        val scheduleAdapter =
            DoctorScheduleAdapter(OnScheduleItemClickListener { selectedSchedule ->
                binding.selectedTime = selectedSchedule
                val schedule = doctorDetails.doctorSchedule!!.toMutableList()

                /* update prev. clicked item */
                val prevClickedSchedule = schedule.find {
                    it.isClicked
                }
                val prevClickedScheduleIndex = schedule.indexOf(prevClickedSchedule)
                prevClickedSchedule!!.isClicked = false

                /* update clicked item */
                val clickedSchedule = schedule.find {
                    it.id == selectedSchedule.id
                }
                val clickedScheduleIndex = schedule.indexOf(clickedSchedule)
                clickedSchedule!!.isClicked = true

                schedule[prevClickedScheduleIndex] = prevClickedSchedule
                schedule[clickedScheduleIndex] = clickedSchedule

                (binding.doctorSchedulesList.adapter as DoctorScheduleAdapter).submitList(schedule)
                (binding.doctorSchedulesList.adapter as DoctorScheduleAdapter).notifyItemChanged(
                    clickedScheduleIndex, Unit
                )
                (binding.doctorSchedulesList.adapter as DoctorScheduleAdapter).notifyItemChanged(
                    prevClickedScheduleIndex, Unit
                )
            })

        binding.doctorSchedulesList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.doctorSchedulesList.adapter = scheduleAdapter
        scheduleAdapter.submitList(doctorDetails.doctorSchedule)

        val loginLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    findNavController().navigate(
                        DoctorDetailsFragmentDirections.actionDoctorDetailsFragmentToDoctorReservationFragment(
                            doctorDetails, binding.selectedTime as DoctorSchedule
                        )
                    )
                }
            }

        binding.bookNowButton.setOnClickListener {
            if (UserInfo.userId == 0) {
                loginLauncher.launch(
                    Intent(
                        requireContext(),
                        LoginRegisterActivity::class.java
                    ).apply {
                        putExtra("hideGuestLogin", true)
                    })
            } else {
                findNavController().navigate(
                    DoctorDetailsFragmentDirections.actionDoctorDetailsFragmentToDoctorReservationFragment(
                        doctorDetails, binding.selectedTime as DoctorSchedule
                    )
                )
            }

        }
        binding.navigateBack.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }
}