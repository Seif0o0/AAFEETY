package code_grow.afeety.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import code_grow.afeety.app.activity.MainActivity
import code_grow.afeety.app.adapter.MyMedicalFilesAdapter
import code_grow.afeety.app.databinding.FragmentMyMedicalFilesBinding
import code_grow.afeety.app.utils.Localize

class MyMedicalFilesFragment : Fragment() {
    private lateinit var binding: FragmentMyMedicalFilesBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Localize.changeLanguage("ar", requireContext())
        binding = FragmentMyMedicalFilesBinding.inflate(inflater, container, false)

        // hide bottom navigation while creating this fragment
        val activity = requireActivity() as MainActivity
        activity.hideBottomNav(true)

        binding.medicalFilesList.adapter = MyMedicalFilesAdapter()
        binding.medicalFilesList.layoutManager = LinearLayoutManager(requireContext())

        binding.navigateBack.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }
}