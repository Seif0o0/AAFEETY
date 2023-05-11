package code_grow.afeety.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import code_grow.afeety.app.adapter.DoctorSpecialitiesAdapter
import code_grow.afeety.app.adapter.DoctorsAdapter
import code_grow.afeety.app.adapter.OnDoctorItemClickListener
import code_grow.afeety.app.adapter.OnDoctorSpecialityItemCLickListener
import code_grow.afeety.app.databinding.FragmentDoctorsBinding
import code_grow.afeety.app.model.Doctor
import code_grow.afeety.app.model.Speciality
import code_grow.afeety.app.repository.HospitalsRepository
import code_grow.afeety.app.retrofit.Resource
import code_grow.afeety.app.utils.Localize
import code_grow.afeety.app.view_model.DoctorsViewModel
import code_grow.afeety.app.view_model.DoctorsViewModelFactory

private const val TAG = "DoctorsFragment"

class DoctorsFragment : Fragment() {
    private lateinit var binding: FragmentDoctorsBinding
    private lateinit var viewModel: DoctorsViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        val hospitalId = requireArguments().getInt("hospitalId")
        val specialitiesList =
            (requireArguments().getParcelableArray("specialities") as Array<Speciality>).toMutableList()
        val viewModelFactory =
            DoctorsViewModelFactory(
                hospitalId,
                requireActivity().application,
                HospitalsRepository()
            )
        viewModel = ViewModelProvider(this, viewModelFactory)[DoctorsViewModel::class.java]

        if (this::binding.isInitialized) {
            binding
        } else {
            binding = FragmentDoctorsBinding.inflate(inflater, container, false)
            specialitiesList[0].clicked = true
            binding.viewModel = viewModel
            binding.lifecycleOwner = requireActivity()
        }

        val specialitiesAdapter =
            DoctorSpecialitiesAdapter(OnDoctorSpecialityItemCLickListener { speciality ->

                /* update prev. clicked item */
                val prevClickedSpeciality = specialitiesList.find {
                    it.clicked
                }
                val prevClickedSpecialityIndex = specialitiesList.indexOf(prevClickedSpeciality)
                prevClickedSpeciality!!.clicked = false

                /* update current clicked item */
                val clickedSpeciality = specialitiesList.find {
                    it.specialityId == speciality.specialityId
                }
                val clickedSpecialityIndex = specialitiesList.indexOf(clickedSpeciality)
                clickedSpeciality!!.clicked = true

                specialitiesList[prevClickedSpecialityIndex] = prevClickedSpeciality
                specialitiesList[clickedSpecialityIndex] = clickedSpeciality

                (binding.specialitiesList.adapter as DoctorSpecialitiesAdapter).submitList(
                    specialitiesList
                )
                (binding.specialitiesList.adapter as DoctorSpecialitiesAdapter).notifyItemChanged(
                    prevClickedSpecialityIndex,
                    Unit
                )
                (binding.specialitiesList.adapter as DoctorSpecialitiesAdapter).notifyItemChanged(
                    clickedSpecialityIndex,
                    Unit
                )
                viewModel.filterBySpeciality(speciality.specialityId)
            })
        binding.specialitiesList.adapter = specialitiesAdapter
        binding.specialitiesList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        specialitiesAdapter.submitList(specialitiesList)

        val doctorsAdapter = DoctorsAdapter(OnDoctorItemClickListener {
            it.doctorSchedule!![0].isClicked = true // set the first item clicked by default
            findNavController().navigate(
                HospitalDetailsFragmentDirections.actionHospitalDetailsFragmentToDoctorDetailsFragment(
                    it
                )
            )
        })
        binding.doctorsList.adapter = doctorsAdapter
        binding.doctorsList.layoutManager = LinearLayoutManager(requireContext())

        viewModel.doctorsResponse.observe(viewLifecycleOwner) {
            viewModel.setStartRequestDoctors(false)
            if (it is Resource.Failed) {
                binding.errorView.error = it.message
                binding.errorView.cancelAnimation = false
            } else if (it is Resource.Success<*>) {
                doctorsAdapter.submitList(it.data as MutableList<Doctor>)
            }
        }

        binding.errorView.root.setOnClickListener {
            binding.errorView.root.visibility = View.GONE
            binding.errorView.cancelAnimation = true
            viewModel.setStartRequestDoctors(true)
        }

        viewModel.startRequestDoctors.observe(viewLifecycleOwner) {
            if (it) {/* start requesting doctors and observe its response */
                viewModel.getDoctors()
            }
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(hospitalId: Int, specialities: Array<Speciality>) =
            DoctorsFragment().apply {
                arguments = Bundle().apply {
                    putInt("hospitalId", hospitalId)
                    putParcelableArray("specialities", specialities)
                }
            }
    }
}