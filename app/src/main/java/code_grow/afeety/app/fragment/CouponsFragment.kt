package code_grow.afeety.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import code_grow.afeety.app.R
import code_grow.afeety.app.activity.MainActivity
import code_grow.afeety.app.adapter.CouponsAdapter
import code_grow.afeety.app.adapter.OnCouponsItemClickListener
import code_grow.afeety.app.databinding.FragmentCouponsBinding
import code_grow.afeety.app.model.Coupon
import code_grow.afeety.app.repository.CouponsRepository
import code_grow.afeety.app.retrofit.Resource
import code_grow.afeety.app.utils.Localize
import code_grow.afeety.app.view_model.CouponsViewModel
import code_grow.afeety.app.view_model.CouponsViewModelFactory

class CouponsFragment : Fragment() {
    private lateinit var binding: FragmentCouponsBinding
    private lateinit var viewModel: CouponsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Localize.changeLanguage("ar", requireContext())
        binding = FragmentCouponsBinding.inflate(inflater, container, false)

        val viewModelFactory = CouponsViewModelFactory(
            requireActivity().application,
            CouponsRepository()
        )

        viewModel = ViewModelProvider(this, viewModelFactory)[CouponsViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()

        // hide bottom navigation while creating this fragment
        val activity = requireActivity() as MainActivity
        activity.hideBottomNav(true)

        binding.navigateBack.setOnClickListener {
            findNavController().popBackStack()
        }

        if (viewModel.query["type"] == "1") {
            binding.medicalText.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.famous_main_color
                )
            )
            binding.medicalLine.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.famous_main_color
                )
            )
            binding.beautyText.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.famous_hint_color
                )
            )
            binding.beautyLine.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    android.R.color.transparent
                )
            )
        } else {
            binding.medicalText.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.famous_hint_color
                )
            )
            binding.medicalLine.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    android.R.color.transparent
                )
            )
            binding.beautyText.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.famous_main_color
                )
            )
            binding.beautyLine.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.famous_main_color
                )
            )
        }

        binding.medicalText.setOnClickListener {
            if (viewModel.query["type"] == "1")
                return@setOnClickListener

            binding.medicalText.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.famous_main_color
                )
            )
            binding.medicalLine.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.famous_main_color
                )
            )

            binding.beautyText.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.famous_hint_color
                )
            )
            binding.beautyLine.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    android.R.color.transparent
                )
            )

            viewModel.query["type"] = "1"
            viewModel.startRequestCoupons(true)
        }

        binding.beautyText.setOnClickListener {
            if (viewModel.query["type"] == "2")
                return@setOnClickListener

            binding.medicalText.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.famous_hint_color
                )
            )
            binding.medicalLine.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    android.R.color.transparent
                )
            )
            binding.beautyText.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.famous_main_color
                )
            )
            binding.beautyLine.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.famous_main_color
                )
            )

            viewModel.query["type"] = "2"
            viewModel.startRequestCoupons(true)
        }

        val couponsAdapter = CouponsAdapter(OnCouponsItemClickListener {
            findNavController().navigate(
                CouponsFragmentDirections.actionCouponsFragmentToBookCouponFragment(it)
            )
        })
        binding.list.adapter = couponsAdapter
        binding.list.layoutManager =
            GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)

        viewModel.startRequestCoupons.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.getCoupons()
            }
        }

        viewModel.couponsResponse.observe(viewLifecycleOwner) {
            if (it is Resource.Success<*>) {
                couponsAdapter.submitList(it.data as MutableList<Coupon>)
            } else if (it is Resource.Failed) {
                binding.errorView.error = it.message
                binding.errorView.cancelAnimation = false
            }

            viewModel.startRequestCoupons(false)
        }

        binding.errorView.root.setOnClickListener {
            binding.errorView.cancelAnimation = true
            viewModel.startRequestCoupons(true)
        }


        return binding.root
    }
}