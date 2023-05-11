package code_grow.afeety.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import code_grow.afeety.app.adapter.MyHospitalReservationsAdapter
import code_grow.afeety.app.adapter.OnReservationItemCLickListener
import code_grow.afeety.app.databinding.FragmentMyHospitalReservationsBinding
import code_grow.afeety.app.model.HospitalBooking
import code_grow.afeety.app.repository.HospitalsRepository
import code_grow.afeety.app.retrofit.Resource
import code_grow.afeety.app.utils.Localize
import code_grow.afeety.app.view_model.MyHospitalReservationsViewModel
import code_grow.afeety.app.view_model.MyHospitalReservationsViewModelFactory

class MyHospitalReservationsFragment : Fragment() {
    private lateinit var binding: FragmentMyHospitalReservationsBinding
    private lateinit var viewModel: MyHospitalReservationsViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        binding = FragmentMyHospitalReservationsBinding.inflate(inflater, container, false)
        val viewModelFactory = MyHospitalReservationsViewModelFactory(
            requireActivity().application,
            HospitalsRepository()
        )
        viewModel =
            ViewModelProvider(this, viewModelFactory)[MyHospitalReservationsViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()


        val adapter = MyHospitalReservationsAdapter(OnReservationItemCLickListener {
            findNavController().navigate(
                MyReservationsFragmentDirections.actionMyReservationsFragmentToMyHospitalReservationDetailsFragment(
                    it
                )
            )
        })
        binding.hospitalReservationsList.layoutManager = LinearLayoutManager(requireContext())
        binding.hospitalReservationsList.adapter = adapter

        viewModel.bookingResponse.observe(viewLifecycleOwner) {
            if (it is Resource.Failed) {
                binding.errorView.error = it.message
                binding.errorView.cancelAnimation = false
            } else if (it is Resource.Success<*>) {
                adapter.submitList((it.data as MutableList<HospitalBooking>))
            }
        }

        binding.errorView.root.setOnClickListener {
            binding.errorView.root.visibility = View.GONE
            binding.errorView.cancelAnimation = true
            viewModel.getReservations()
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = MyHospitalReservationsFragment()
    }
}