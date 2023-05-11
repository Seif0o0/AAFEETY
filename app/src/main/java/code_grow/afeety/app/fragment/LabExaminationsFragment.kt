package code_grow.afeety.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import code_grow.afeety.app.adapter.ExaminationAdapter
import code_grow.afeety.app.adapter.OnExaminationItemClickListener
import code_grow.afeety.app.databinding.FragmentLabExaminationsBinding
import code_grow.afeety.app.model.LabExamination
import code_grow.afeety.app.utils.Localize

private const val TAG = "LabExaminationsFragment"

class LabExaminationsFragment : Fragment() {
    private lateinit var binding: FragmentLabExaminationsBinding
    private lateinit var bookExamination: BookExaminationInterface
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        val examinations =
            (requireArguments().getSerializable("examinations") as Array<LabExamination>).toMutableList()
        if (this::binding.isInitialized) {
            binding
        } else {
            binding = FragmentLabExaminationsBinding.inflate(inflater, container, false)
        }

        bookExamination = requireParentFragment() as BookExaminationInterface

        val examinationsAdapter =
            ExaminationAdapter(View.VISIBLE, OnExaminationItemClickListener { selectedExamination ->
                /* update clicked item */
                val clickedExamination = examinations.find {
                    it.examinationId == selectedExamination.examinationId
                }
                val clickedScheduleIndex = examinations.indexOf(clickedExamination)
                clickedExamination!!.isClicked = !clickedExamination.isClicked

                examinations[clickedScheduleIndex] = clickedExamination

                (binding.examinationsList.adapter as ExaminationAdapter).submitList(examinations)
                (binding.examinationsList.adapter as ExaminationAdapter).notifyItemChanged(
                    clickedScheduleIndex, Unit
                )

                bookExamination.startBookingExamination(examinations.filter {
                    it.isClicked
                }.toMutableList())
            })
        binding.examinationsList.layoutManager = LinearLayoutManager(requireContext())
        binding.examinationsList.adapter = examinationsAdapter

        if (examinations.isEmpty())
            binding.emptyExaminationsText.visibility = View.VISIBLE
        else
            examinationsAdapter.submitList(examinations)

        return binding.root
    }

    interface BookExaminationInterface {
        fun startBookingExamination(examinations: MutableList<LabExamination>)
    }

    companion object {
        fun newInstance(examinations: MutableList<LabExamination>) =
            LabExaminationsFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArray("examinations", examinations.toTypedArray())
                }
            }
    }
}