package code_grow.afeety.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import code_grow.afeety.app.databinding.FragmentOffersBinding
import code_grow.afeety.app.utils.Localize

class OffersFragment : Fragment() {
    private lateinit var binding: FragmentOffersBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        binding = FragmentOffersBinding.inflate(inflater, container, false)
        return binding.root
    }


    companion object {
        @JvmStatic
        fun newInstance() = OffersFragment().apply {

        }
    }
}