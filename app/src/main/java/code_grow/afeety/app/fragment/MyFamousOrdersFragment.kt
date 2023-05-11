package code_grow.afeety.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import code_grow.afeety.app.adapter.MyProductOrdersAdapter
import code_grow.afeety.app.adapter.OnProductOrderItemCLickListener
import code_grow.afeety.app.databinding.FragmentMyFamouseOrdersBinding
import code_grow.afeety.app.model.ProductOrder
import code_grow.afeety.app.repository.ProductRepository
import code_grow.afeety.app.retrofit.Resource
import code_grow.afeety.app.utils.Localize
import code_grow.afeety.app.view_model.MyProductOrdersViewModel
import code_grow.afeety.app.view_model.MyProductOrdersViewModelFactory

class MyFamousOrdersFragment : Fragment() {
    private lateinit var binding: FragmentMyFamouseOrdersBinding
    private lateinit var viewModel: MyProductOrdersViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        binding = FragmentMyFamouseOrdersBinding.inflate(inflater, container, false)

        val viewModelFactory = MyProductOrdersViewModelFactory(
            requireActivity().application,
            2,
            ProductRepository()
        )
        viewModel =
            ViewModelProvider(this, viewModelFactory)[MyProductOrdersViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()

        val adapter = MyProductOrdersAdapter(OnProductOrderItemCLickListener {
            findNavController().navigate(
                MyOrdersFragmentDirections.actionMyOrdersFragmentToMyProductOrderProductsFragment(it.products.toTypedArray())
            )
        })

        binding.ordersList.layoutManager = LinearLayoutManager(requireContext())
        binding.ordersList.adapter = adapter


        viewModel.startRequestOrders.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.getOrders()
            }
        }

        viewModel.ordersResponse.observe(viewLifecycleOwner) {
            if (it is Resource.Failed) {
                binding.errorView.error = it.message
                binding.errorView.cancelAnimation = false
            } else if (it is Resource.Success<*>) {
                adapter.submitList(it.data as MutableList<ProductOrder>)
            }
        }

        binding.errorView.root.setOnClickListener {
            binding.errorView.root.visibility = View.GONE
            binding.errorView.cancelAnimation = true
            viewModel.getOrders()
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = MyFamousOrdersFragment()
    }
}