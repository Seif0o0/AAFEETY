package code_grow.afeety.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import code_grow.afeety.app.adapter.MyPharmacyOrderProductsAdapter
import code_grow.afeety.app.adapter.OnPharmacyOrderProductItemCLickListener
import code_grow.afeety.app.databinding.FragmentMyPharmacyOrderProductsBinding
import code_grow.afeety.app.utils.Constants
import code_grow.afeety.app.utils.Localize
import com.bumptech.glide.Glide

class MyPharmacyOrderProductsFragment : Fragment() {
    private lateinit var binding: FragmentMyPharmacyOrderProductsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        binding = FragmentMyPharmacyOrderProductsBinding.inflate(inflater, container, false)
        val args = MyPharmacyOrderProductsFragmentArgs.fromBundle(requireArguments())
        val prescription = args.prescription
        val medicines = args.medicines

        if (medicines.isNullOrEmpty()) {
            // set prescription photo
            binding.prescriptionGroup.visibility = View.VISIBLE
            binding.list.visibility = View.GONE

            Glide.with(this)
                .load("${Constants.BASE_URL}prod_img/$prescription")
                .into(binding.prescriptionImage)
        } else {
            binding.prescriptionGroup.visibility = View.GONE
            binding.list.visibility = View.VISIBLE
            val adapter = MyPharmacyOrderProductsAdapter(args.pharmacyName,
                OnPharmacyOrderProductItemCLickListener { })
            adapter.submitList(medicines.toMutableList())
            binding.list.layoutManager = LinearLayoutManager(requireContext())
            binding.list.adapter = adapter
        }



        binding.navigateBack.setOnClickListener {
            findNavController().popBackStack()
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = MyPharmacyOrderProductsFragment()
    }
}