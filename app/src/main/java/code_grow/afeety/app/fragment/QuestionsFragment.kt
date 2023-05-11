package code_grow.afeety.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import code_grow.afeety.app.adapter.OnQuestionItemClickListener
import code_grow.afeety.app.adapter.QuestionsAdapter
import code_grow.afeety.app.databinding.FragmentQuestionsBinding
import code_grow.afeety.app.model.Question
import code_grow.afeety.app.repository.QuestionsRepository
import code_grow.afeety.app.retrofit.Resource
import code_grow.afeety.app.utils.Localize
import code_grow.afeety.app.view_model.QuestionsViewModel
import code_grow.afeety.app.view_model.QuestionsViewModelFactory

class QuestionsFragment : Fragment() {
    private lateinit var binding: FragmentQuestionsBinding
    private lateinit var viewModel: QuestionsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())

        val viewModelFactory = QuestionsViewModelFactory(
            requireActivity().application,
            QuestionsRepository()
        )

        viewModel = ViewModelProvider(this, viewModelFactory)[QuestionsViewModel::class.java]

        binding = FragmentQuestionsBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()


        val questionsAdapter = QuestionsAdapter(OnQuestionItemClickListener {
            findNavController().navigate(
                AskDoctorFragmentDirections.actionAskDoctorFragmentToQuestionDetailsFragment(
                    it
                )
            )
        })

        binding.questionsList.adapter = questionsAdapter
        binding.questionsList.layoutManager = LinearLayoutManager(requireContext())

        viewModel.questionsResponse.observe(viewLifecycleOwner) {
            if (it is Resource.Success<*>) {
                questionsAdapter.submitList(it.data as MutableList<Question>)
            } else if (it is Resource.Failed) {
                binding.errorView.error = it.message
                binding.errorView.cancelAnimation = false
            }

            viewModel.startRequestQuestions(false)
        }

        binding.errorView.root.setOnClickListener {
            binding.errorView.root.visibility = View.GONE
            binding.errorView.cancelAnimation = true
            viewModel.startRequestQuestions(true)
        }

        viewModel.startRequestQuestions.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.getQuestions()
            }
        }

        return binding.root
    }

    fun filter(query: String) {
        viewModel.updateQueryMap(query)
    }

    companion object {
        @JvmStatic
        fun newInstance() = QuestionsFragment()
    }
}