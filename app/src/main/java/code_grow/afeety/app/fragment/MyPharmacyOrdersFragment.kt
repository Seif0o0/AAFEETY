package code_grow.afeety.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import code_grow.afeety.app.adapter.MyPharmacyOrdersAdapter
import code_grow.afeety.app.adapter.OnPharmacyOrderItemCLickListener
import code_grow.afeety.app.databinding.FragmentMyPharmacyOrdersBinding
import code_grow.afeety.app.model.MedicineOrder
import code_grow.afeety.app.repository.PharmaciesRepository
import code_grow.afeety.app.retrofit.Resource
import code_grow.afeety.app.utils.Localize
import code_grow.afeety.app.view_model.MyPharmacyOrdersViewModel
import code_grow.afeety.app.view_model.MyPharmacyOrdersViewModelFactory

class MyPharmacyOrdersFragment : Fragment() {
    private lateinit var binding: FragmentMyPharmacyOrdersBinding
    private lateinit var viewModel: MyPharmacyOrdersViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        binding = FragmentMyPharmacyOrdersBinding.inflate(inflater, container, false)
        val viewModelFactory = MyPharmacyOrdersViewModelFactory(
            requireActivity().application,
            PharmaciesRepository()
        )
        viewModel =
            ViewModelProvider(this, viewModelFactory)[MyPharmacyOrdersViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()

        val adapter = MyPharmacyOrdersAdapter(OnPharmacyOrderItemCLickListener {
            findNavController().navigate(
                MyOrdersFragmentDirections.actionMyOrdersFragmentToMyPharmacyOrderProductsFragment(
                    prescription = it.prescription,
                    medicines = if (it.medicines.isEmpty()) null else it.medicines.toTypedArray(),
                    pharmacyName = it.pharmacyInfo.name
                )
            )
        })

        binding.pharmacyOrdersList.layoutManager = LinearLayoutManager(requireContext())
        binding.pharmacyOrdersList.adapter = adapter

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
                adapter.submitList(it.data as MutableList<MedicineOrder>)
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
        fun newInstance() = MyPharmacyOrdersFragment()
    }
}