package code_grow.afeety.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import code_grow.afeety.app.databinding.LabExaminationItemBinding
import code_grow.afeety.app.model.LabExamination

class ExaminationAdapter(
    private val checkable: Int,/* flag to check if list item is checkable or not */
    private val clickListener: OnExaminationItemClickListener
) :
    ListAdapter<LabExamination, ExaminationAdapter.ViewHolder>(LabExaminationsDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position + 1, getItem(position), checkable, clickListener)
    }

    class ViewHolder private constructor(private val binding: LabExaminationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            position: Int,
            examination: LabExamination,
            checkable: Int,
            clickListener: OnExaminationItemClickListener
        ) {
            binding.position = position.toString()
            binding.checkable = checkable
            binding.examination = examination
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    LabExaminationItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}


open class OnExaminationItemClickListener(val clickListener: (examination: LabExamination) -> Unit) {
    fun onExaminationClicked(examination: LabExamination) = clickListener(examination)
}

class LabExaminationsDiffUtil : DiffUtil.ItemCallback<LabExamination>() {
    override fun areItemsTheSame(oldItem: LabExamination, newItem: LabExamination) =
        oldItem.examinationId == newItem.examinationId

    override fun areContentsTheSame(oldItem: LabExamination, newItem: LabExamination) =
        oldItem == newItem
}
