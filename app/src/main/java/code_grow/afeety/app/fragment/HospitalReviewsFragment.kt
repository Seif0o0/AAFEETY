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
import code_grow.afeety.app.databinding.FragmentHospitalReviewsBinding
import code_grow.afeety.app.model.Review
import code_grow.afeety.app.repository.HospitalsRepository
import code_grow.afeety.app.retrofit.Resource
import code_grow.afeety.app.utils.Localize
import code_grow.afeety.app.view_model.HospitalReviewsViewModel
import code_grow.afeety.app.view_model.HospitalReviewsViewModelFactory

class HospitalReviewsFragment : Fragment() {
    private lateinit var binding: FragmentHospitalReviewsBinding
    private lateinit var viewModel: HospitalReviewsViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        binding = FragmentHospitalReviewsBinding.inflate(inflater, container, false)
        val viewModelFactory = HospitalReviewsViewModelFactory(
            requireArguments().getInt("hospitalId"),
            requireActivity().application,
            HospitalsRepository()
        )
        viewModel = ViewModelProvider(this, viewModelFactory)[HospitalReviewsViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()

        val reviewsAdapter = LabReviewsAdapter(1, OnReviewItemClickListener {

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
            viewModel.getReviews()
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(hospitalId: Int) =
            HospitalReviewsFragment().apply {
                arguments = Bundle().apply {
                    putInt("hospitalId", hospitalId)

                }
            }
    }
}