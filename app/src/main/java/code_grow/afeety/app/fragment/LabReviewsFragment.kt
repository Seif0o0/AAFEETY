package code_grow.afeety.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import code_grow.afeety.app.adapter.LabReviewsAdapter
import code_grow.afeety.app.adapter.OnReviewItemClickListener
import code_grow.afeety.app.databinding.FragmentLabReviewsBinding
import code_grow.afeety.app.model.Review
import code_grow.afeety.app.repository.LabsRepository
import code_grow.afeety.app.retrofit.Resource
import code_grow.afeety.app.utils.Localize
import code_grow.afeety.app.view_model.LabReviewsViewModel
import code_grow.afeety.app.view_model.LabReviewsViewModelFactory

class LabReviewsFragment : Fragment() {
    private lateinit var binding: FragmentLabReviewsBinding
    private lateinit var viewModel: LabReviewsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        binding = FragmentLabReviewsBinding.inflate(inflater, container, false)
        val labId = requireArguments().getInt("labId")

        val viewModelFactory = LabReviewsViewModelFactory(
            labId,
            requireActivity().application,
            LabsRepository()
        )
        viewModel = ViewModelProvider(this, viewModelFactory)[LabReviewsViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()


        val reviewsAdapter = LabReviewsAdapter(0,OnReviewItemClickListener {

        })

        binding.reviewsList.adapter = reviewsAdapter
        binding.reviewsList.layoutManager = LinearLayoutManager(requireContext())
        viewModel.reviewsResponse.observe(viewLifecycleOwner) {
            if (it is Resource.Failed) {
                binding.errorView.error = it.message
                binding.errorView.cancelAnimation = false
            } else if (it is Resource.Success<*>) {
                reviewsAdapter.submitList(it.data as MutableList<Review>)
            }
        }

        binding.errorView.root.setOnClickListener {
            binding.errorView.root.visibility = View.GONE
            binding.errorView.cancelAnimation = true
            viewModel.getReviews(labId)
        }
        return binding.root
    }

    companion object {
        fun newInstance(labId: Int) = LabReviewsFragment().apply {
            arguments = Bundle().apply {
                putInt("labId", labId)
            }
        }
    }
}