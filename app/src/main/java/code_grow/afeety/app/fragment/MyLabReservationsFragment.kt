package code_grow.afeety.app.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import code_grow.afeety.app.adapter.MyLabReservationsAdapter
import code_grow.afeety.app.adapter.OnLabReservationItemCLickListener
import code_grow.afeety.app.databinding.FragmentMyLabReservationsBinding
import code_grow.afeety.app.model.LabBooking
import code_grow.afeety.app.repository.LabsRepository
import code_grow.afeety.app.retrofit.Resource
import code_grow.afeety.app.utils.Localize
import code_grow.afeety.app.view_model.MyLabReservationsViewModel
import code_grow.afeety.app.view_model.MyLabReservationsViewModelFactory

private const val TAG = "MyLabReservationsFragment"

class MyLabReservationsFragment : Fragment() {
    private lateinit var binding: FragmentMyLabReservationsBinding
    private lateinit var viewModel: MyLabReservationsViewModel

    @SuppressLint("LongLogTag")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        binding = FragmentMyLabReservationsBinding.inflate(inflater, container, false)
        val viewModelFactory = MyLabReservationsViewModelFactory(
            requireActivity().application,
            LabsRepository()
        )
        viewModel =
            ViewModelProvider(this, viewModelFactory)[MyLabReservationsViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()

        val adapter = MyLabReservationsAdapter(OnLabReservationItemCLickListener {
            findNavController().navigate(
                MyReservationsFragmentDirections.actionMyReservationsFragmentToMyLabReservationDetailsFragment(
                    it
                )
            )
        })
        binding.labReservationsList.layoutManager = LinearLayoutManager(requireContext())
        binding.labReservationsList.adapter = adapter

        viewModel.bookingResponse.observe(viewLifecycleOwner) {
            if (it is Resource.Failed) {
                binding.errorView.error = it.message
                binding.errorView.cancelAnimation = false
            } else if (it is Resource.Success<*>) {
                adapter.submitList(it.data as MutableList<LabBooking>)
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
        fun newInstance() = MyLabReservationsFragment()
    }
}