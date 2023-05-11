package code_grow.afeety.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import code_grow.afeety.app.databinding.DoctorScheduleItemBinding
import code_grow.afeety.app.model.DoctorSchedule

class DoctorScheduleAdapter(private val clickListener: OnScheduleItemClickListener) :
    ListAdapter<DoctorSchedule, DoctorScheduleAdapter.ViewHolder>(DoctorScheduleDiffUtil()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class ViewHolder private constructor(private val binding: DoctorScheduleItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            schedule: DoctorSchedule, clickListener: OnScheduleItemClickListener
        ) {
            binding.schedule = schedule
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    DoctorScheduleItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}

open class OnScheduleItemClickListener(val clickListener: (schedule: DoctorSchedule) -> Unit) {
    fun onScheduleClicked(schedule: DoctorSchedule) = clickListener(schedule)
}

class DoctorScheduleDiffUtil : DiffUtil.ItemCallback<DoctorSchedule>() {
    override fun areItemsTheSame(oldItem: DoctorSchedule, newItem: DoctorSchedule) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: DoctorSchedule, newItem: DoctorSchedule) =
        oldItem == newItem

}