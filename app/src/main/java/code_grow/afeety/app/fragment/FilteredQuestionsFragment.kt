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
import code_grow.afeety.app.adapter.OnDoctorSpecialityItemCLickListener
import code_grow.afeety.app.adapter.OnQuestionItemClickListener
import code_grow.afeety.app.adapter.QuestionsAdapter
import code_grow.afeety.app.databinding.FragmentFilteredQuestionsBinding
import code_grow.afeety.app.model.Question
import code_grow.afeety.app.model.Speciality
import code_grow.afeety.app.repository.QuestionsRepository
import code_grow.afeety.app.retrofit.Resource
import code_grow.afeety.app.utils.Localize
import code_grow.afeety.app.view_model.FilteredQuestionsViewModel
import code_grow.afeety.app.view_model.FilteredQuestionsViewModelFactory


class FilteredQuestionsFragment : Fragment() {
    private lateinit var binding: FragmentFilteredQuestionsBinding
    private lateinit var viewModel: FilteredQuestionsViewModel
    private lateinit var specialitiesList: MutableList<Speciality>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())

        val viewModelFactory =
            FilteredQuestionsViewModelFactory(requireActivity().application, QuestionsRepository())

        viewModel =
            ViewModelProvider(this, viewModelFactory)[FilteredQuestionsViewModel::class.java]
        if (this::binding.isInitialized) {
            binding
        } else {
            binding = FragmentFilteredQuestionsBinding.inflate(inflater, container, false)
            binding.viewModel = viewModel
            binding.lifecycleOwner = requireActivity()
        }


        val specialitiesAdapter =
            DoctorSpecialitiesAdapter(OnDoctorSpecialityItemCLickListener { speciality ->
                specialitiesList =
                    (binding.specialitiesList.adapter as DoctorSpecialitiesAdapter).currentList.toMutableList()
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
                viewModel.filterQuestions(speciality.specialityId)
            })

        binding.specialitiesList.adapter = specialitiesAdapter
        binding.specialitiesList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)


        viewModel.startRequestSpecialities.observe(viewLifecycleOwner) {
            if (it)
                viewModel.getSpecialities()
        }

        viewModel.specialitiesResponse.observe(viewLifecycleOwner) {
            if (it is Resource.Success<*>) {
                val data = it.data as MutableList<Speciality>
                specialitiesAdapter.submitList(data)
            } else if (it is Resource.Failed) {
                binding.errorView.error = it.message
                binding.errorView.cancelAnimation = false
            }
            viewModel.startRequestSpecialities(false)
        }

        binding.errorView.root.setOnClickListener {
            binding.errorView.root.visibility = View.GONE
            binding.errorView.cancelAnimation = true
            viewModel.startRequestSpecialities(true)
        }

        val questionsAdapter = QuestionsAdapter(OnQuestionItemClickListener {
            findNavController().navigate(
                AskDoctorFragmentDirections.actionAskDoctorFragmentToQuestionDetailsFragment(
                    it
                )
            )
        })

        binding.questionsList.adapter = questionsAdapter
        binding.questionsList.layoutManager = LinearLayoutManager(requireContext())

        viewModel.startRequestQuestions.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.getQuestions()
            }
        }


        viewModel.questionsResponse.observe(viewLifecycleOwner) {
            if (it is Resource.Success<*>) {
                questionsAdapter.submitList(it.data as MutableList<Question>)
            } else if (it is Resource.Failed) {
                binding.errorView.error = it.message
                binding.errorView.cancelAnimation = false
            }
            viewModel.startRequestQuestions(false)
        }

        return binding.root
    }

    fun filter(query: String) {
        viewModel.currentQuery = query
        viewModel.filterQuestions(specialitiesList.find { it.clicked }?.specialityId!!)
    }

    companion object {
        @JvmStatic
        fun newInstance() = FilteredQuestionsFragment()
    }
}