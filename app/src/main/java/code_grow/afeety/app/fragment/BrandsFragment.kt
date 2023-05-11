package code_grow.afeety.app.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import code_grow.afeety.app.adapter.BrandsAdapter
import code_grow.afeety.app.adapter.OnBrandItemClickListener
import code_grow.afeety.app.databinding.FragmentBrandsBinding
import code_grow.afeety.app.model.Brand
import code_grow.afeety.app.repository.ProductRepository
import code_grow.afeety.app.retrofit.Resource
import code_grow.afeety.app.utils.Localize
import code_grow.afeety.app.view_model.BrandsViewModel
import code_grow.afeety.app.view_model.BrandsViewModelFactory

class BrandsFragment : Fragment() {
    private lateinit var binding: FragmentBrandsBinding
    private lateinit var viewModel: BrandsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        binding = FragmentBrandsBinding.inflate(inflater, container, false)
        val viewModelFactory = BrandsViewModelFactory(
            requireActivity().application,
            ProductRepository()
        )

        viewModel = ViewModelProvider(this, viewModelFactory)[BrandsViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()

        binding.navigateBack.setOnClickListener {
            findNavController().popBackStack()
        }

        val brandsAdapter = BrandsAdapter(OnBrandItemClickListener {
            findNavController().navigate(
                BrandsFragmentDirections.actionBrandsFragmentToBrandInfoFragment(
                    it
                )
            )
        })

        binding.list.adapter = brandsAdapter
        binding.list.layoutManager =
            GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)

        viewModel.startRequestBrands.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.getBrands()
            }
        }

        viewModel.brandsResponse.observe(viewLifecycleOwner) {
            if (it is Resource.Success<*>) {
                brandsAdapter.submitList(it.data as MutableList<Brand>)
                viewModel.startRequestBrands(false)
                viewModel.changeResponseToIdle()
            } else if (it is Resource.Failed) {
                binding.errorView.error = it.message
                binding.errorView.cancelAnimation = false
                viewModel.startRequestBrands(false)
            }
        }

        binding.errorView.root.setOnClickListener {
            binding.errorView.cancelAnimation = true
            viewModel.startRequestBrands(true)
        }

        binding.searchEditText.tag = "false";
        binding.searchEditText.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus -> binding.searchEditText.tag = "$hasFocus" }


        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.searchEditText.tag.toString()
                        .lowercase() == "true"
                ) viewModel.startSearch(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })


        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = BrandsFragment()
    }
}