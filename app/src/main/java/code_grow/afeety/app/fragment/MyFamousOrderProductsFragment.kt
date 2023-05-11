package code_grow.afeety.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import code_grow.afeety.app.adapter.MyFamousOrderProductsAdapter
import code_grow.afeety.app.databinding.FragmentMyFamousOrderProductsBinding
import code_grow.afeety.app.utils.Localize

class MyFamousOrderProductsFragment : Fragment() {
    private lateinit var binding: FragmentMyFamousOrderProductsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        binding = FragmentMyFamousOrderProductsBinding.inflate(inflater, container, false)
        val adapter = MyFamousOrderProductsAdapter()
        binding.orderProductsList.layoutManager = LinearLayoutManager(requireContext())
        binding.orderProductsList.adapter = adapter

        binding.orderProductsTitle.setOnClickListener {
            findNavController().navigate(
                MyFamousOrderProductsFragmentDirections.actionMyFamousOrderProductsFragmentToMyFamousOrderProductDetailsFragment()
            )
        }

        binding.navigateBack.setOnClickListener {
            findNavController().popBackStack()
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = MyFamousOrderProductsFragment()
    }
}