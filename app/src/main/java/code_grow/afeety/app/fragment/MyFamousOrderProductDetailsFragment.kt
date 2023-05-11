package code_grow.afeety.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import code_grow.afeety.app.activity.MainActivity
import code_grow.afeety.app.databinding.*
import code_grow.afeety.app.utils.Localize

class MyFamousOrderProductDetailsFragment : Fragment() {
    private lateinit var binding: FragmentMyFamousOrderProductDetailsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        binding = FragmentMyFamousOrderProductDetailsBinding.inflate(inflater, container, false)

        // hide bottom navigation while creating this fragment
        val activity = requireActivity() as MainActivity
        activity.hideBottomNav(true)

        binding.navigateBack.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }
}