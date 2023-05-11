package code_grow.afeety.app.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import code_grow.afeety.app.R
import code_grow.afeety.app.databinding.MedicineSearchDialogLayoutBinding
import code_grow.afeety.app.model.MedicineCategory
import code_grow.afeety.app.repository.PharmaciesRepository
import code_grow.afeety.app.retrofit.Resource
import code_grow.afeety.app.utils.Localize
import code_grow.afeety.app.view_model.MedicineSearchViewModel
import code_grow.afeety.app.view_model.MedicineSearchViewModelFactory

private const val TAG = "MedicineSearchDialogFragment"

class MedicineSearchDialogFragment : DialogFragment() {
    private lateinit var binding: MedicineSearchDialogLayoutBinding
    private lateinit var viewModel: MedicineSearchViewModel

    @SuppressLint("LongLogTag")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        binding = MedicineSearchDialogLayoutBinding.inflate(inflater, container, false)
        val viewModelFactory = MedicineSearchViewModelFactory(
            requireActivity().application,
            PharmaciesRepository()
        )
        viewModel = ViewModelProvider(this, viewModelFactory)[MedicineSearchViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()


        binding.searchButton.setOnClickListener {
            viewModel.searchBtnClicked()
        }

        viewModel.startSearch.observe(viewLifecycleOwner) {
            if (it) {
                // go to medicines Fragment
                setFragmentResult(
                    "search_medicines_key",
                    bundleOf(
                        "query" to binding.medicineNameEditText.text.toString(),
                        "categoryId" to viewModel.categoryId
                    )
                )
                dismiss()
            }
        }

        viewModel.startRequestMedicineCategories.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.getMedicines()
            }
        }
        viewModel.medicineCategoriesResponse.observe(viewLifecycleOwner) {
            if (it is Resource.Failed) {
                binding.errorView.error = it.message
                binding.errorView.cancelAnimation = false
            } else if (it is Resource.Success<*>) {
                val categories = it.data as MutableList<MedicineCategory>
                val categoriesNames = mutableListOf<String>()
                for (category in categories)
                    categoriesNames.add(category.name)
                binding.autocompleteMedicineSection.setAdapter(
                    ArrayAdapter(
                        requireContext(),
                        R.layout.register_dropdown_item,
                        categoriesNames
                    )
                )
            }
        }

        binding.autocompleteMedicineSection.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, _, _ ->
                binding.autocompleteMedicineSection.error = null
            }

        binding.errorView.root.setOnClickListener {
            binding.errorView.root.visibility = View.GONE
            binding.errorView.cancelAnimation = true
            viewModel.startRequestMedicineCategories(true)
        }




        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.Theme_Material_Light_Dialog_NoMinWidth);
    }

    companion object {
        @JvmStatic
        fun newInstance() = MedicineSearchDialogFragment()
    }
}