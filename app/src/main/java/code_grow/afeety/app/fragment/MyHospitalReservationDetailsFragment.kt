package code_grow.afeety.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import code_grow.afeety.app.activity.MainActivity
import code_grow.afeety.app.databinding.FragmentMyHospitalReservationDetailsBinding
import code_grow.afeety.app.utils.Localize

class MyHospitalReservationDetailsFragment : Fragment() {
    private lateinit var binding: FragmentMyHospitalReservationDetailsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        binding = FragmentMyHospitalReservationDetailsBinding.inflate(inflater, container, false)
        val bookingDetails =
            MyHospitalReservationDetailsFragmentArgs.fromBundle(requireArguments()).bookingDetails
        binding.booking = bookingDetails

        // hide bottom navigation while creating this fragment
        val activity = requireActivity() as MainActivity
        activity.hideBottomNav(true)

        binding.navigateBack.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }
}