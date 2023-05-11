package code_grow.afeety.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import code_grow.afeety.app.activity.MainActivity
import code_grow.afeety.app.adapter.ExaminationAdapter
import code_grow.afeety.app.adapter.OnExaminationItemClickListener
import code_grow.afeety.app.databinding.FragmentBookingExaminationsBinding
import code_grow.afeety.app.utils.Localize

class BookingExaminationsFragment : Fragment() {
    private lateinit var binding: FragmentBookingExaminationsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        binding = FragmentBookingExaminationsBinding.inflate(inflater, container, false)

        // hide bottom navigation while creating this fragment
        val activity = requireActivity() as MainActivity
        activity.hideBottomNav(true)

        binding.navigateBack.setOnClickListener {
            findNavController().popBackStack()
        }

        val adapter =
            ExaminationAdapter(View.VISIBLE, OnExaminationItemClickListener {})
        binding.examinations.adapter = adapter
        binding.examinations.layoutManager = LinearLayoutManager(requireContext())
        adapter.submitList(BookingExaminationsFragmentArgs.fromBundle(requireArguments()).examinations.toMutableList())

        return binding.root
    }
}