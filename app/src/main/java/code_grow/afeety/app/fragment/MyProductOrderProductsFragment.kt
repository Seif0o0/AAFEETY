package code_grow.afeety.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import code_grow.afeety.app.adapter.MyProductOrderProductAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import code_grow.afeety.app.adapter.OnProductOrderProductItemCLickListener
import code_grow.afeety.app.databinding.FragmentMyProductOrderProductsBinding
import code_grow.afeety.app.utils.Localize

class MyProductOrderProductsFragment : Fragment() {
    private lateinit var binding: FragmentMyProductOrderProductsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        binding = FragmentMyProductOrderProductsBinding.inflate(inflater, container, false)
        val products = MyProductOrderProductsFragmentArgs.fromBundle(requireArguments()).products.toMutableList()

        val adapter = MyProductOrderProductAdapter(OnProductOrderProductItemCLickListener{})
        adapter.submitList(products)

        binding.orderProductsList.layoutManager = LinearLayoutManager(requireContext())
        binding.orderProductsList.adapter = adapter

//        binding.orderProductsTitle.setOnClickListener {
//            findNavController().navigate(
//                MyProductOrderProductsFragmentDirections.actionMyProductOrderProductsFragmentToMyProductOrderProductDetailsFragment()
//            )
//        }

        binding.navigateBack.setOnClickListener {
            findNavController().popBackStack()
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = MyProductOrderProductsFragment()
    }
}