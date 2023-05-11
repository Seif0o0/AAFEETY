package code_grow.afeety.app.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import code_grow.afeety.app.activity.MainActivity
import code_grow.afeety.app.databinding.FragmentMyLabReservationDetailsBinding
import code_grow.afeety.app.utils.Localize

private const val TAG = "MyLabReservationDetailsFragment"

class MyLabReservationDetailsFragment : Fragment() {
    private lateinit var binding: FragmentMyLabReservationDetailsBinding

    @SuppressLint("LongLogTag")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        binding = FragmentMyLabReservationDetailsBinding.inflate(inflater, container, false)
        val bookingDetails =
            MyLabReservationDetailsFragmentArgs.fromBundle(requireArguments()).bookingDetails
        binding.booking = bookingDetails

        // hide bottom navigation while creating this fragment
        val activity = requireActivity() as MainActivity
        activity.hideBottomNav(true)

        binding.navigateBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.moreExaminationsCount.setOnClickListener {

            findNavController().navigate(
                MyLabReservationDetailsFragmentDirections.actionMyLabReservationDetailsFragmentToBookingExaminationsFragment(
                    bookingDetails.examinations.apply {
                        for (examination in this) {
                            examination.isClicked = true
                        }
                    }.toTypedArray()
                )
            )
        }

        return binding.root
    }
}